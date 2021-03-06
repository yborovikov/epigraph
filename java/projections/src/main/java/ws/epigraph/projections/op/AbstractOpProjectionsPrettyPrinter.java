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

package ws.epigraph.projections.op;

import de.uka.ilkd.pp.Layouter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.annotations.Annotation;
import ws.epigraph.annotations.Annotations;
import ws.epigraph.gdata.*;
import ws.epigraph.lang.Keywords;
import ws.epigraph.printers.DataPrinter;
import ws.epigraph.projections.ProjectionsPrettyPrinterContext;
import ws.epigraph.projections.abs.AbstractProjectionsPrettyPrinter;
import ws.epigraph.types.DatumTypeApi;

import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class AbstractOpProjectionsPrettyPrinter<E extends Exception>
    extends AbstractProjectionsPrettyPrinter<
    OpProjection<?, ?>,
    OpEntityProjection,
    OpTagProjectionEntry,
    OpModelProjection<?, ?, ?, ?>,
    E> {

  protected final @NotNull DataPrinter<E> dataPrinter;

  protected AbstractOpProjectionsPrettyPrinter(
      final @NotNull Layouter<E> layouter,
      final @NotNull ProjectionsPrettyPrinterContext<OpProjection<?, ?>> context) {
    super(layouter, context);
    dataPrinter = new DataPrinter<>(layouter);
  }

  @Override
  protected void printTagName(@NotNull String tagName, @NotNull OpModelProjection<?, ?, ?, ?> mp) throws E {
    l.print(escape(tagName));
  }

  @Override
  protected boolean printModelParams(@NotNull OpModelProjection<?, ?, ?, ?> mp) throws E {
    if (!modelParamsEmpty(mp)) {
      l.beginCInd();
      l.print("{");
      brk();

      boolean empty = printModelParamsInBlock(mp);

      if (!empty) brk(1, -l.getDefaultIndentation());
      l.end();
      l.print("}");

      return false;
    }

    return true;
  }

  @SuppressWarnings("unchecked")
  protected boolean printModelParamsInBlock(final @NotNull OpModelProjection<?, ?, ?, ?> projection) throws E {
    final OpParams params = projection.params();
    final Annotations annotations = projection.annotations();
    final OpModelProjection<?, ?, ?, ?> metaProjection = projection.metaProjection();

    boolean first = true;
    if (!params.isEmpty())
      //noinspection ConstantConditions
      first = printOpParams(params, false, first);
    if (!annotations.isEmpty())
      first = printAnnotations(annotations, false, first);

    if (metaProjection != null) {
      if (first)
        first = false;
      else
        brk();

      l.beginIInd(0).print("meta:");
      brk();
      printModel(metaProjection, 0);
      l.end();
    }

    return first;
  }

//  public void printOpParams(@NotNull OpParams p) throws E {
//    printOpParams(p, false, true);
//  }

  public boolean printOpParams(@NotNull OpParams p, boolean needCommas, boolean first) throws E {
    for (OpParam param : p.asMap().values()) {
      if (first) {
        first = false;
      } else {
        if (needCommas)
          l.print(",");
        brk();
      }
      l.beginCInd(0);
      printOpParam(param);
      l.end();
    }

    return first;
  }

  public void printOpParam(@NotNull OpParam p) throws E {
    OpModelProjection<?, ?, ?, ?> projection = p.projection();

    l.beginIInd();
    l.print(";");
    if (projection.flag()) l.print("+");
    l.print(escape(p.name())).print(":");
    brk();
    l.print(projection.type().name().toString());

    OpProjectionsPrettyPrinter<E> ipp = new OpProjectionsPrettyPrinter<>(l);
    if (!ipp.modelParamsEmpty(projection) || !ipp.isPrintoutNoParamsEmpty(projection)) {
      brk();
      ipp.printModel(projection, 0);
    }

//    Annotations annotations = projection.annotations();
//
//    GDatum defaultValue = projection.defaultValue();
//    if (defaultValue != null) {
//      brk().print("=");
//      brk();
//      gdataPrettyPrinter.print(defaultValue);
//    }
//
//    if (!annotations.isEmpty()) {
//      l.beginCInd();
//      l.print(" {");
//      printAnnotations(annotations);
//      brk(1, -l.getDefaultIndentation()).end().print("}");
    l.end();
  }

  public void printRecordProjection(@NotNull OpRecordModelProjection recordProjection) throws E {
    @SuppressWarnings("unchecked")
    Map<String, OpFieldProjectionEntry> fieldProjections = recordProjection.fieldProjections();

    l.print("(").beginCInd();
    boolean first = true;
    for (Map.Entry<String, OpFieldProjectionEntry> entry : fieldProjections.entrySet()) {
      if (first) first = false;
      else l.print(",");
      brk();

      @NotNull String prefix = fieldNamePrefix(entry.getValue());
      @NotNull OpFieldProjection fieldProjection = entry.getValue().fieldProjection();

      printFieldProjection(prefix + escape(entry.getKey()), fieldProjection);
    }
    brk(1, -l.getDefaultIndentation()).end().print(")");

  }

  protected String fieldNamePrefix(@NotNull OpFieldProjectionEntry fieldEntry) { return ""; }

  public void print(@NotNull OpFieldProjection fieldProjection) throws E {
    OpProjection<?, ?> fieldVarProjection = fieldProjection.projection();
//    @NotNull OpParams fieldParams = fieldProjection.params();
//    @NotNull Annotations fieldAnnotations = fieldProjection.annotations();

//    if (fieldParams.isEmpty() && fieldAnnotations.isEmpty()) {
    if (!isPrintoutEmpty(fieldVarProjection)) {
      printProjection(fieldVarProjection, 0);
    }
//    } else {
//      l.beginCInd();
//      l.print("{");
//      if (!fieldParams.isEmpty()) print(fieldParams);
//      if (!fieldAnnotations.isEmpty()) print(fieldAnnotations);
//      brk(1, -l.getDefaultIndentation()).end().print("}");
//      if (!isPrintoutEmpty(fieldVarProjection)) {
//        l.beginIInd();
//        brk();
//        print(fieldVarProjection, 0);
//        l.end();
//      }
//    }
  }

//  protected void printMapModelProjection(
//      @Nullable String keysProjectionPrefix,
//      @NotNull AbstractOpKeyProjection keyProjection,
//      VP itemsProjection) throws E {
//
//    printMapModelProjection(keysProjectionPrefix, keyProjection, "", itemsProjection);
//  }

  protected void printMapModelProjection(
      @Nullable String keysProjectionPrefix,
      @NotNull OpKeyProjection keyProjection,
      @NotNull String itemsProjectionPrefix,
      OpProjection<?,?> itemsProjection) throws E {

    l.beginIInd(0);
    { // keys
      l.beginCInd();
      l.print("[");
      brk();
      boolean commaNeeded = false;
      boolean first = true;

      if (keysProjectionPrefix != null) {
        l.print(keysProjectionPrefix);
        commaNeeded = true;
        first = false;
      }

      @NotNull OpParams keyParams = keyProjection.params();
      if (!keyParams.isEmpty()) {
//        if (commaNeeded) brk();
        first = printOpParams(keyParams, true, first);
        commaNeeded = !keyParams.isEmpty();
      }

      @NotNull Annotations keyAnnotations = keyProjection.annotations();
      if (!keyAnnotations.isEmpty()) {
//        if (commaNeeded) brk();
        first = printAnnotations(keyAnnotations, true, first);
      }

      OpModelProjection<?, ?, ?, ?> keySpec = keyProjection.spec();
      if (keySpec != null) {
        if (!first) {
          l.print(",");
          brk();
        }

        l.beginCInd(0);
        l.print("projection:");
        brk();
        OpProjectionsPrettyPrinter<E> opp = new OpProjectionsPrettyPrinter<>(l);
        opp.printModel(keySpec, 0);
        l.end();

        first = false;
      }

      if (!first) brk(1, -l.getDefaultIndentation());
      l.end().print("]");
    }
    l.print(itemsProjectionPrefix);
    l.print("(");
    l.beginIInd();
    brk();
    printProjection(itemsProjection, 0);
    brk(1, -l.getDefaultIndentation()).end().print(")").end();
  }

  public void printFieldProjection(@NotNull String prefix, @NotNull OpFieldProjection fieldProjection) throws E {
    printFieldProjection(prefix, "", fieldProjection);
  }

  public void printFieldProjection(
      @NotNull String prefix,
      @NotNull String prefix2,
      @NotNull OpFieldProjection fieldProjection)
      throws E {
    if (isPrintoutEmpty(fieldProjection)) {
      l.print(prefix);
    } else {
      boolean isBlock = isBlockProjection(fieldProjection);

      if (!isBlock) l.beginIInd(0);
      l.print(prefix);

//      if (isBlock) l.print(" ");
//      else brk();
      l.print(" ");
      l.print(prefix2);

      print(fieldProjection);
      if (!isBlock) l.end();
    }
  }

  @Override
  protected boolean isPrintoutEmpty(@NotNull OpEntityProjection EP) {

    if (!super.isPrintoutEmpty(EP)) return false;

    for (OpTagProjectionEntry tagProjection : EP.tagProjections().values()) {
      final OpModelProjection<?, ?, ?, ?> modelProjection = tagProjection.modelProjection();
      if (!modelProjection.params().isEmpty()) return false;
      if (!modelProjection.annotations().isEmpty()) return false;
    }

    return true;
  }

  @Override
  public boolean modelParamsEmpty(final @NotNull OpModelProjection<?, ?, ?, ?> mp) {
    return mp.metaProjection() == null && mp.params().isEmpty() && mp.annotations.isEmpty();
  }

  public boolean isPrintoutEmpty(@NotNull OpFieldProjection fieldProjection) {
    OpProjection<?, ?> fieldVarProjection = fieldProjection.projection();
    return !isBlockProjection(fieldProjection) && isPrintoutEmpty(fieldVarProjection);
  }

  public boolean isBlockProjection(@NotNull OpFieldProjection fieldProjection) {
    //return !fieldProjection.params().isEmpty() || !fieldProjection.annotations().isEmpty();
    return false;
  }

  protected @NotNull String escape(@NotNull String s) { return Keywords.schema.escape(s); }

//  public void printAnnotations(@NotNull Annotations cp) throws E {
//    printAnnotations(cp, false, true);
//  }

  public boolean printAnnotations(@NotNull Annotations cp, boolean needCommas, boolean first) throws E {
    for (Map.Entry<DatumTypeApi, Annotation> entry : cp.asMap().entrySet()) {
      if (first) {
        first = false;
      } else {
        if (needCommas) l.print(",");
        brk();
      }
      l.beginCInd(0);
      l.print("@");
      DatumTypeApi type = entry.getKey();
      l.print(type.name().toString());
      GDatum gDatum = entry.getValue().gDatum();
      if (!isDefaultAnnotationValue(gDatum)) {
        brk();
        gdataPrettyPrinter.print(gDatum); // should take type into account
      }
      l.end();
    }

    return first;
  }

  private boolean isDefaultAnnotationValue(@NotNull GDatum datum) {
    if (datum instanceof GPrimitiveDatum) {
      GPrimitiveDatum primitiveDatum = (GPrimitiveDatum) datum;
      // we only collapse booleans here
      return Boolean.TRUE.equals(primitiveDatum.value());
    } else if (datum instanceof GRecordDatum) {
      GRecordDatum recordDatum = (GRecordDatum) datum;
      return recordDatum.fields().isEmpty();
    } else if (datum instanceof GMapDatum) {
      GMapDatum mapDatum = (GMapDatum) datum;
      return mapDatum.entries().isEmpty();
    } else if (datum instanceof GListDatum) {
      GListDatum listDatum = (GListDatum) datum;
      return listDatum.values().isEmpty();
    }
    return false;
  }

}
