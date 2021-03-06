/*
 * Copyright 2017 Sumo Logic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* Created by yegor on 10/8/16. */

package ws.epigraph.wire.json.writer;

import net.jcip.annotations.NotThreadSafe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.data.*;
import ws.epigraph.errors.ErrorValue;
import ws.epigraph.projections.gen.*;
import ws.epigraph.types.*;
import ws.epigraph.wire.AbstractFormatWriter;
import ws.epigraph.wire.WireUtil;
import ws.epigraph.wire.json.JsonFormat;
import ws.epigraph.wire.json.JsonFormatCommon;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ws.epigraph.wire.json.JsonFormatCommon.*;

/**
 * Abstract projection-driven JSON data writer
 * <p>
 * See {@link ws.epigraph.wire.json.reader.AbstractJsonFormatReader} for format specification
 */
@NotThreadSafe
public abstract class AbstractJsonFormatWriter<
    P extends GenProjection<? extends P, TP, EP, ? extends MP>,
    EP extends GenEntityProjection<EP, TP, MP>,
    TP extends GenTagProjectionEntry<TP, MP>,
    MP extends GenModelProjection<EP, TP, /*MP*/?, ?, ?>,
    RMP extends GenRecordModelProjection<P, TP, EP, MP, RMP, FPE, FP, ?>,
    FPE extends GenFieldProjectionEntry<P, TP, MP, FP>,
    FP extends GenFieldProjection<P, TP, MP, FP>,
    MMP extends GenMapModelProjection<P, TP, EP, MP, MMP, ?>,
    LMP extends GenListModelProjection<P, TP, EP, MP, LMP, ?>,
    PMP extends GenPrimitiveModelProjection<EP, TP, MP, PMP, ?>,
    KP // key projection
    > extends AbstractFormatWriter<P, TP, MP, RMP, FPE, FP, MMP> {

  private final @NotNull Writer out;
  private final @NotNull Map<Data, List<VisitedDataEntry>> visitedData = new IdentityHashMap<>();
  private final @NotNull Map<Data, Integer> visitedDataNoProjection = new IdentityHashMap<>();
  private int dataStackDepth = 0;

  protected AbstractJsonFormatWriter(@NotNull OutputStream out, @NotNull Charset charset) {
    this.out = new BufferedWriter(new OutputStreamWriter(out, charset));
  }

  @Override
  public void close() throws IOException { out.close(); }

  public void reset() {
    visitedData.clear();
    visitedDataNoProjection.clear();
    dataStackDepth = 0;
  }

  @Override
  public void writeData(@NotNull P projection, @Nullable Data data) throws IOException {
    if (data == null) {
      out.write("null");
    } else {
      TypeApi type = data.type();
      assert projection.type().isAssignableFrom(type) :
          "Projection type " + projection.type().name() + " is not assignable from " + type.name();
      writeData(projection.polymorphicTails() != null, projections(projection, type), data);
    }
  }

  private void writeData(
      boolean renderPoly,
      @NotNull Deque<P> projections, // non-empty, polymorphic tails ignored
      @NotNull Data data
  ) throws IOException {
    TypeApi type = projections.peekLast().type();

    // don't do recursion check on primitives
    final TypeKind kind = type.kind();
    boolean doRecursionCheck = kind != TypeKind.PRIMITIVE;

    List<VisitedDataEntry> visitedDataEntries = visitedData.get(data);
    if (doRecursionCheck) {
      Integer prevStackDepth = null;
      if (visitedDataEntries == null) {
        visitedDataEntries = new ArrayList<>();
        visitedData.put(data, visitedDataEntries);
      } else {
        // NB this can lead to O(N^3), optimize if causes problems
        VisitedDataEntry entry =
            visitedDataEntries.stream().filter(e -> e.matches(projections)).findFirst().orElse(null);
        if (entry != null)
          prevStackDepth = entry.depth;
      }

      if (prevStackDepth != null) {
        out.write("{\"" + JsonFormat.REC_FIELD + "\":");
        out.write(String.valueOf(dataStackDepth - prevStackDepth));
        out.write('}');
        return;
      }

      visitedDataEntries.add(new VisitedDataEntry(dataStackDepth++, projections));
    }

    // TODO check all projections (not just the ones that matched actual data type)?
    boolean renderMulti = type.kind() == TypeKind.ENTITY && monoTag(projections) == null;
    if (renderPoly) {
      out.write("{\"" + JsonFormat.POLYMORPHIC_TYPE_FIELD + "\":\"");
      out.write(type.name().toString()); // TODO use (potentially short) type name used in request projection?
      out.write("\",\"" + JsonFormat.POLYMORPHIC_VALUE_FIELD + "\":");
    }
    if (renderMulti) out.write('{');
    boolean comma = false;
    for (TagApi tag : type.tags()) {
      Deque<MP> tagModelProjections =
          tagModelProjections(tag, projections, () -> new ArrayDeque<>(projections.size()));
      if (tagModelProjections != null) { // if this tag was mentioned in at least one projection
        if (renderMulti) {
          if (comma) out.write(',');
          else comma = true;
          out.write('"');
          out.write(tag.name());
          out.write("\":");
        }
        final @Nullable Val value = data._raw().getValue((Tag) tag);
        @SuppressWarnings("unchecked")
        Deque<MP> modelProjections = value == null || value.getDatum() == null ? tagModelProjections :
            (Deque<MP>) JsonFormatCommon. // java 10 quirk requires explicit type arguments
                <GenModelProjection<?, TP, ?, ?, ?>, Deque<GenModelProjection<?, TP, ?, ?, ?>>>flatten(
                new ArrayDeque<>(), tagModelProjections, value.getDatum().type()
            );
        writeValue(modelProjections, value);
      }
    } // TODO if we're not rendering multi and zero tags were requested (projection error) - render error instead
    if (renderMulti) out.write('}');
    if (renderPoly) out.write('}');

    if (doRecursionCheck) {
      dataStackDepth--;
      visitedDataEntries.removeIf(e -> e.depth == dataStackDepth);
      if (visitedDataEntries.isEmpty())
        visitedData.remove(data);
    }

  }

  private void writeValue(@NotNull Deque<? extends MP> projections, @Nullable Val value)
      throws IOException {
    if (value == null) { // TODO in case of null value we should probably render NO_VALUE error?
      out.write("null");
    } else {
      ErrorValue error = value.getError();
      if (error == null) writeDatum(projections, value.getDatum());
      else writeError(error);
    }
  }

  @Override
  public void writeDatum(
      @NotNull MP projection,
      @Nullable Datum datum)
      throws IOException {

    final Deque<MP> projections;
    if (datum == null) {
      projections = new ArrayDeque<>(1);
      projections.add(projection);
    } else
      projections = modelProjections(projection, datum.type());

    writeDatum(projections, datum);
  }

  @SuppressWarnings("unchecked")
  private void writeDatum(
      @NotNull Deque<? extends MP> projections,
      @Nullable Datum datum)
      throws IOException {

    DatumTypeApi model = projections.peekLast().type();
    boolean renderPoly = WireUtil.needPoly(model, projections);

    Deque<? extends MP> metaProjections = projections.stream()
        .map(m -> (MP) (m.metaProjection()))
        .filter(Objects::nonNull)
        .collect(Collectors.toCollection(ArrayDeque::new));

    if (renderPoly) {
      out.write("{\"" + JsonFormat.POLYMORPHIC_TYPE_FIELD + "\":\"");
      out.write(model.name().toString()); // TODO use (potentially short) type name used in request projection?
      out.write("\",\"" + JsonFormat.POLYMORPHIC_VALUE_FIELD + "\":");
    }
    if (!metaProjections.isEmpty()) {
      out.write("{\"");
      out.write(JsonFormat.DATUM_META_FIELD);
      out.write("\":");
      writeDatum(metaProjections, datum == null ? null : datum._raw().meta());
      out.write(",\"");
      out.write(JsonFormat.DATUM_VALUE_FIELD);
      out.write("\":");
    }

    if (datum == null) {
      out.write("null");
    } else {
      switch (model.kind()) {
        case RECORD:
          writeRecord((Deque<RMP>) projections, (RecordDatum) datum);
          break;
        case MAP:
          writeMap((Deque<MMP>) projections, (MapDatum) datum);
          break;
        case LIST:
          writeList((Deque<LMP>) projections, (ListDatum) datum);
          break;
        case PRIMITIVE:
          writePrimitive((Deque<PMP>) projections, (PrimitiveDatum<?>) datum);
          break;
        case ENUM:
//            writeEnum((Deque<ReqOutputEnumModelProjection>) modelProjections, (EnumDatum) datum);
//            break;
        case ENTITY:
        default:
          throw new UnsupportedOperationException(model.kind().name());
      }
    }

    if (!metaProjections.isEmpty()) {
      out.write('}');
    }
    if (renderPoly) out.write('}');
  }

  @Override
  public void writeError(@NotNull ErrorValue error) throws IOException {
    out.write("{\"" + JsonFormat.ERROR_CODE_FIELD + "\":");
    out.write(error.statusCode().toString());
    out.write(",\"" + JsonFormat.ERROR_MESSAGE_FIELD + "\":");
    writeString(error.message());
    out.write('}');
  }

  private void writeRecord(
      @NotNull Deque<RMP> projections, // non-empty
      @NotNull RecordDatum datum
  ) throws IOException {
    out.write('{');
    // TODO take type from announced type tag (same for other datum kinds)?
    RecordTypeApi type = projections.peekLast().type();
    boolean comma = false;
    for (FieldApi field : type.fields()) {
      Deque<P> fieldProjections =
          fieldProjections(projections, field, () -> new ArrayDeque<>(projections.size()));
      if (fieldProjections != null) { // if this field was mentioned in at least one projection
        Data fieldData = datum._raw().getData((Field) field);
        if (fieldData != null) {
          if (comma) out.write(',');
          else comma = true;
          out.write('"');
          out.write(field.name());
          out.write("\":");
          writeData(
              WireUtil.needPoly(fieldProjections),
              flatten(new ArrayDeque<>(), fieldProjections, WireUtil.type(fieldData)),
              fieldData
          );
        }
      }
    }
    out.write('}');
  }

  @SuppressWarnings("unchecked")
  private void writeMap(
      @NotNull Deque<MMP> projections, // non-empty
      @NotNull MapDatum datum
  ) throws IOException {
    out.write("[");
    List<KP> keyProjections = keyProjections(projections);
    Deque<P> valueProjections = subProjections(
        projections,
        MMP::itemsProjection
    );
    boolean polymorphicValue = WireUtil.needPoly(valueProjections);
    Map<Type, Deque<P>> polymorphicCache = polymorphicValue ? new HashMap<>() : null;
    if (keyProjections == null) writeMapEntries(
        datum.type().keyType(),
        getKeyModelProjections(projections),
        valueProjections,
        polymorphicCache,
        datum._raw().elements().entrySet(),
        Map.Entry::getKey,
        Map.Entry::getValue
    );
    else writeMapEntries( // TODO check ReqOutputMapModelProjection::keysRequired() and throw(?) if a key is missing
        datum.type().keyType(),
        getKeyModelProjections(projections),
        valueProjections,
        polymorphicCache,
        keyProjections,
        this::keyDatum,
        kp -> datum._raw().elements().get(keyDatum(kp)) // Datum.equals() contract says this is ok.
    );
    out.write("]");
  }

  /**
   * Builds a superset of all key projections. `null` is treated as wildcard and yields wildcard result immediately.
   */
  protected abstract @Nullable List<KP> keyProjections(
      @NotNull Deque<MMP> projections // non-empty
  );

  protected @Nullable Deque<? extends MP> getKeyModelProjections(@NotNull Collection<MMP> projections) { return null; }

  protected abstract @NotNull Datum keyDatum(@NotNull KP keyProjection);

  private <PR, SP> @NotNull Deque<SP> subProjections(
      @NotNull Deque<? extends PR> projections, // non-empty
      @NotNull Function<PR, SP> func
  ) {
    assert !projections.isEmpty() : "no projection(s)";
    ArrayDeque<SP> subProjections = new ArrayDeque<>(projections.size());
    for (PR projection : projections) subProjections.add(func.apply(projection));
    return subProjections;
  }

  private <E> void writeMapEntries(
      @NotNull DatumType keyType,
      @Nullable Deque<? extends MP> keyModelProjections,
      @NotNull Deque<P> valueProjections,
      @Nullable Map<Type, Deque<P>> polymorphicCache,
      @NotNull Iterable<E> entries,
      @NotNull Function<E, @NotNull Datum> keyFunc,
      @NotNull Function<E, @Nullable Data> valueFunc
  ) throws IOException {
    boolean comma = false;
    for (E entry : entries) {
      @NotNull Datum key = keyFunc.apply(entry);
      @Nullable Data valueData = valueFunc.apply(entry);
      if (valueData != null) {
        if (comma) out.write(',');
        else comma = true;
        out.write("{\"" + JsonFormat.MAP_ENTRY_KEY_FIELD + "\":");
        if (keyModelProjections == null)
          writeDatum(keyType, key);
        else
          writeDatum(keyModelProjections, key);
        out.write(",\"" + JsonFormat.MAP_ENTRY_VALUE_FIELD + "\":");
        Deque<P> flatValueProjections = polymorphicCache == null
                                        ? valueProjections
                                        : polymorphicCache.computeIfAbsent(
                                            valueData.type(),
                                            t -> flatten(new ArrayDeque<>(), valueProjections, t)
                                        );
        writeData(polymorphicCache != null, flatValueProjections, valueData);
        out.write('}');
      }
    }
  }

  @SuppressWarnings("unchecked")
  private void writeList(@NotNull Deque<LMP> projections, @NotNull ListDatum datum)
      throws IOException {
    out.write('[');
    Deque<P> elementProjections = subProjections(
        projections,
        LMP::itemsProjection
    );
    boolean polymorphicValue = WireUtil.needPoly(elementProjections);
    Map<Type, Deque<P>> polymorphicCache = polymorphicValue ? new HashMap<>() : null;
    boolean comma = false;
    for (Data element : datum._raw().elements()) {
      if (comma) out.write(',');
      else comma = true;
      Deque<P> flatElementProjections = polymorphicCache == null
                                        ? elementProjections
                                        : polymorphicCache.computeIfAbsent(
                                            element.type(),
                                            t -> flatten(new ArrayDeque<>(), elementProjections, t)
                                        );
      writeData(polymorphicCache != null, flatElementProjections, element);
    }
    out.write(']');
  }

  private void writePrimitive(
      @NotNull Deque<PMP> projections,
      @NotNull PrimitiveDatum<?> datum
  ) throws IOException { writePrimitive(datum); }

  private @NotNull Deque<P> projections(@NotNull P projection, @NotNull TypeApi varType) {
    return append(new ArrayDeque<>(5), projection, varType);
  }

  @SuppressWarnings("unchecked")
  private @NotNull Deque<MP> modelProjections(@NotNull MP projection, @NotNull DatumTypeApi modelType) {
    // MP should extend P
    return (Deque<MP>) append(new ArrayDeque<>(5), (P) projection, modelType);
  }

  // FIXME take explicit type for all projectionless writes below

  @Override
  public void writeData(@NotNull Type valueType, @Nullable Data data) throws IOException {
    if (data == null) {
      writeNullData();
    } else {
      final Integer prevStackDepth = visitedDataNoProjection.get(data);

      if (prevStackDepth != null) {
        out.write("{\"" + JsonFormat.REC_FIELD + "\":");
        out.write(String.valueOf(dataStackDepth - prevStackDepth));
        out.write('}');
        return;
      }
      visitedDataNoProjection.put(data, dataStackDepth++);

      Type type = data.type();
      if (type.kind() == TypeKind.ENTITY) { // TODO use instanceof instead of kind?
        boolean poly = !type.equals(valueType);
        if (poly) {
          out.write("{\"" + JsonFormat.POLYMORPHIC_TYPE_FIELD + "\":\"");
          out.write(type.name().toString());
          out.write("\",\"" + JsonFormat.POLYMORPHIC_VALUE_FIELD + "\":");
        }
        out.write('{');
        boolean comma = false;
        for (Tag tag : type.tags()) {
          Val value = data._raw().getValue(tag);
          if (value != null) {
            if (comma) out.write(',');
            else comma = true;
            out.write('"');
            out.write(tag.name());
            out.write("\":");
            writeValue(tag.type, value);
          }
        }
        out.write('}');
        if (poly) out.write('}');
      } else {
        Tag selfTag = ((DatumType) valueType).self();
        Val value = data._raw().getValue(selfTag);
        if (value == null) writeError(NO_VALUE);
        else writeValue(selfTag.type, value);
      }
    }

    visitedDataNoProjection.remove(data);
    dataStackDepth--;
  }

  @Override
  public void writeNullData() throws IOException {
    out.write("null");
  }

  private static final ErrorValue NO_VALUE = new ErrorValue(500, "No value", null);

  @Override
  public void writeValue(@NotNull DatumType valueType, @NotNull Val value) throws IOException {
    ErrorValue error = value.getError();
    if (error == null) writeDatum(valueType, value.getDatum());
    else writeError(error);
  }

  @Override
  public void writeDatum(@NotNull DatumType valueType, @Nullable Datum datum) throws IOException {
    if (datum == null) {
      out.write("null");
    } else {
      DatumType model = datum.type();
      boolean poly = !model.equals(valueType);
      if (poly) {
        out.write("{\"" + JsonFormat.POLYMORPHIC_TYPE_FIELD + "\":\"");
        out.write(model.name().toString());
        out.write("\",\"" + JsonFormat.POLYMORPHIC_VALUE_FIELD + "\":");
      }
      switch (model.kind()) {
        case RECORD:
          writeRecord((RecordDatum) datum);
          break;
        case MAP:
          writeMap((MapDatum) datum);
          break;
        case LIST:
          writeList((ListDatum) datum);
          break;
        case PRIMITIVE:
          writePrimitive((PrimitiveDatum<?>) datum);
          break;
        case ENUM:
//        writeEnum((EnumDatum) datum);
//        break;
        case ENTITY:
        default:
          throw new UnsupportedOperationException(model.kind().name());
      }
      if (poly) out.write('}');
    }
  }

  private void writeRecord(@NotNull RecordDatum datum) throws IOException {
    out.write('{');
    boolean comma = false;
    for (Field field : datum.type().fields()) {
      Data fieldData = datum._raw().getData(field);
      if (fieldData != null) {
        if (comma) out.write(',');
        else comma = true;
        out.write('"');
        out.write(field.name());
        out.write("\":");
        writeData(field.dataType().type(), fieldData);
      }
    }
    out.write('}');
  }

  private void writeMap(@NotNull MapDatum datum) throws IOException {
    DatumType keyType = datum.type().keyType();
    Type valueType = datum.type().valueType().type();

    out.write("[");
    boolean comma = false;
    for (Map.Entry<Datum.Imm, @NotNull ? extends Data> entry : datum._raw().elements().entrySet()) {
      if (comma) out.write(',');
      else comma = true;
      out.write("{\"" + JsonFormat.MAP_ENTRY_KEY_FIELD + "\":");
      writeDatum(keyType, entry.getKey());
      out.write(",\"" + JsonFormat.MAP_ENTRY_VALUE_FIELD + "\":");
      writeData(valueType, entry.getValue());
      out.write('}');
    }
    out.write("]");
  }

  private void writeList(@NotNull ListDatum datum) throws IOException {
    Type elementType = datum.type().elementType().type();

    out.write('[');
    boolean comma = false;
    for (Data elementData : datum._raw().elements()) {
      if (comma) out.write(',');
      else comma = true;
      writeData(elementType, elementData);
    }
    out.write(']');
  }

  private void writePrimitive(@NotNull PrimitiveDatum<?> datum) throws IOException {
    if (datum instanceof StringDatum) writeString(((StringDatum) datum).getVal());
    else if (datum instanceof DoubleDatum) writeDouble(((DoubleDatum) datum).getVal());
    else out.write(datum.getVal().toString());
  }

  /**
   * See https://tools.ietf.org/html/rfc7159#section-6.
   */
  private void writeDouble(@NotNull Double d) throws IOException {
    if (d.isInfinite() || d.isNaN()) out.write("null"); // TODO render ErrorValue(500) instead?
    else out.write(d.toString()); // TODO more compact representation / better rfc compliance?
  }

  /**
   * See https://tools.ietf.org/html/rfc7159#section-7.
   */
  private void writeString(@Nullable String s) throws IOException {
    if (s == null) {
      out.write("null");
    } else {
      out.write('"');
      int length = s.length(), from = 0;
      String escape = null;
      for (int i = 0; i < length; ++i) {
        char c = s.charAt(i);
        switch (c) {
          case '\b':
            escape = "\\b";
            break;
          case '\t':
            escape = "\\t";
            break;
          case '\n':
            escape = "\\n";
            break;
          case '\f':
            escape = "\\f";
            break;
          case '\r':
            escape = "\\r";
            break;
          case '"':
            escape = "\\\"";
            break;
          case '\\':
            escape = "\\\\";
            break;
          default:
            if (c < 0x20) escape = "\\u00" + HEX_DIGITS[c >> 4] + HEX_DIGITS[c & 0x0f];
        }
        if (escape != null) {
          int len = i - from;
          if (len != 0) out.write(s, from, len);
          out.write(escape);
          escape = null;
          from = i + 1;
        }
      }
      int len = length - from;
      if (len != 0) out.write(s, from, len);
      out.write('"');
    }
  }

  private static final char[] HEX_DIGITS = "0123456789ABCDEF".toCharArray();

  private final class VisitedDataEntry {
    final int depth;
    final Collection<P> projections;

    VisitedDataEntry(final int depth, final Collection<P> projections) {
      this.depth = depth;
      this.projections = projections;
    }

    boolean matches(Collection<P> projections) {
      return new GenProjectionsComparator<P, TP, EP, MP, RMP, MMP, LMP, PMP, FPE, FP>().projectionsEquals(
          projections,
          this.projections
      );
    }
  }

  @Override
  protected void writeNull() throws IOException {
    out.write("null");
  }
}
