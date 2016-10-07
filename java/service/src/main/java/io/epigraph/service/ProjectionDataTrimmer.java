package io.epigraph.service;

import io.epigraph.data.*;
import io.epigraph.errors.ErrorValue;
import io.epigraph.projections.req.output.*;
import io.epigraph.types.RecordType;
import io.epigraph.types.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ProjectionDataTrimmer { // todo move somewhere else?

  @NotNull
  public static Data trimData(@NotNull Data data, @NotNull ReqOutputVarProjection projection) {
    @NotNull final Data.Raw raw = data._raw();
    @NotNull final Data.Builder.Raw b = data.type().createDataBuilder()._raw();

    for (Map.Entry<Type.Tag, ReqOutputTagProjection> entry : projection.tagProjections().entrySet()) {
      final Type.Tag tag = entry.getKey();

      @Nullable final Val val = raw.getValue(tag);
      if (val != null) {
        @Nullable final ErrorValue error = val.getError();
        if (error != null) b.setError(tag, error);

        @Nullable final Datum datum = val.getDatum();
        if (datum != null) b.setDatum(tag, trimDatum(datum, entry.getValue().projection()));
      }
    }

    // todo deal with tails

    return b;
  }

  @NotNull
  public static Datum trimDatum(@NotNull Datum datum, @NotNull ReqOutputModelProjection<?> projection) {
    switch (datum.type().kind()) {
      case RECORD:
        return trimRecordDatum((RecordDatum) datum, (ReqOutputRecordModelProjection) projection);
      case MAP:
        return trimMapDatum((MapDatum) datum, (ReqOutputMapModelProjection) projection);
      case LIST:
        return trimListDatum((ListDatum) datum, (ReqOutputListModelProjection) projection);
      case PRIMITIVE:
        return trimPrimitiveDatum((PrimitiveDatum) datum, (ReqOutputPrimitiveModelProjection) projection);
      case ENUM:
        throw new RuntimeException("Unsupported type kind: " + datum.type().kind());
      case UNION:
        throw new RuntimeException("Unexpected type kind: " + datum.type().kind());
      default:
        throw new RuntimeException("Unknown type kind: " + datum.type().kind());
    }
  }

  @NotNull
  public static Datum trimRecordDatum(@NotNull RecordDatum datum, @NotNull ReqOutputRecordModelProjection projection) {
    @NotNull final RecordDatum.Raw raw = datum._raw();
    @NotNull final RecordDatum.Builder.Raw b = datum.type().createBuilder()._raw();

    @Nullable
    LinkedHashMap<RecordType.Field, ReqOutputFieldProjection> fieldProjections = projection.fieldProjections();

    if (fieldProjections != null) {
      for (Map.Entry<RecordType.Field, ReqOutputFieldProjection> entry : fieldProjections.entrySet()) {
        final RecordType.Field field = entry.getKey();
        @Nullable final Data data = raw.getData(field);

        if (data != null) b.setData(field, trimData(data, entry.getValue().projection()));
      }
    }

    return b;
  }

  @NotNull
  public static Datum trimMapDatum(@NotNull MapDatum datum, @NotNull ReqOutputMapModelProjection projection) {

    @Nullable final List<ReqOutputKeyProjection> keyProjections = projection.keys();

    if (keyProjections != null) {
      @NotNull final MapDatum.Raw raw = datum._raw();
      @NotNull final MapDatum.Builder.Raw b = datum.type().createBuilder()._raw();

      for (ReqOutputKeyProjection keyProjection : keyProjections) {
        @NotNull final Datum.Imm keyValue = keyProjection.value().toImmutable();
        @Nullable final Data data = raw.elements().get(keyValue);

        if (data != null) b.elements().put(keyValue, trimData(data, projection.itemsProjection()));
      }

      return b;
    } else {
      return datum; // all keys?
    }

  }

  @NotNull
  public static Datum trimListDatum(@NotNull ListDatum datum, @NotNull ReqOutputListModelProjection projection) {
    // nothing to trim
    return datum;
  }

  @NotNull
  public static Datum trimPrimitiveDatum(@NotNull PrimitiveDatum datum,
                                         @NotNull ReqOutputPrimitiveModelProjection projection) {
    // nothing to trim
    return datum;
  }

//  public static Datum trimEnumDatum(@NotNull EnumDatum datum, @NotNull ReqOutputEnumModelProjection projection) { }
}
