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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.annotations.Annotations;
import ws.epigraph.gdata.GRecordDatum;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.RecordModelProjectionHelper;
import ws.epigraph.projections.gen.GenRecordModelProjection;
import ws.epigraph.projections.gen.ProjectionReferenceName;
import ws.epigraph.types.FieldApi;
import ws.epigraph.types.RecordTypeApi;
import ws.epigraph.types.TypeApi;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static ws.epigraph.projections.RecordModelProjectionHelper.reattachFields;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpRecordModelProjection
    extends OpModelProjection<OpModelProjection<?, ?, ?, ?>, OpRecordModelProjection, RecordTypeApi, GRecordDatum>
    implements GenRecordModelProjection<
    OpProjection<?, ?>,
    OpTagProjectionEntry,
    OpEntityProjection,
    OpModelProjection<?, ?, ?, ?>,
    OpRecordModelProjection,
    OpFieldProjectionEntry,
    OpFieldProjection,
    RecordTypeApi
    > {

  private /*final*/ @NotNull Map<String, OpFieldProjectionEntry> fieldProjections;

  public OpRecordModelProjection(
      @NotNull RecordTypeApi model,
      boolean flag,
      @Nullable GRecordDatum defaultValue,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @Nullable OpModelProjection<?, ?, ?, ?> metaProjection,
      @NotNull Map<String, OpFieldProjectionEntry> fieldProjections,
      @Nullable List<OpRecordModelProjection> tails,
      @NotNull TextLocation location) {

    super(model, flag, defaultValue, params, annotations, metaProjection, tails, location);
    this.fieldProjections = Collections.unmodifiableMap(fieldProjections);

    RecordModelProjectionHelper.checkFields(fieldProjections, model);
  }

  public OpRecordModelProjection(final @NotNull RecordTypeApi model, final @NotNull TextLocation location) {
    super(model, location);
    this.fieldProjections = Collections.emptyMap();
  }

  public static @NotNull OpRecordModelProjection pathEnd(@NotNull RecordTypeApi model, @NotNull TextLocation location) {
    return new OpRecordModelProjection(
        model,
        false,
        null,
        OpParams.EMPTY,
        Annotations.EMPTY,
        null,
        Collections.emptyMap(), // marks path end
        null,
        location
    );
  }

  @Override
  public @NotNull Map<String, OpFieldProjectionEntry> fieldProjections() {
    assert isResolved();
    return fieldProjections;
  }

  @Override
  protected OpRecordModelProjection merge(
      final @NotNull RecordTypeApi model,
      final boolean mergedFlag,
      final @Nullable GRecordDatum mergedDefault,
      final @NotNull List<OpRecordModelProjection> modelProjections,
      final @NotNull OpParams mergedParams,
      final @NotNull Annotations mergedAnnotations,
      final @Nullable OpModelProjection<?, ?, ?, ?> mergedMetaProjection,
      final @Nullable List<OpRecordModelProjection> mergedTails) {

    Map<FieldApi, OpFieldProjection> mergedFieldProjections =
        RecordModelProjectionHelper.mergeFieldProjections(modelProjections);

    Map<String, OpFieldProjectionEntry> mergedFieldEntries = new LinkedHashMap<>();
    for (final Map.Entry<FieldApi, OpFieldProjection> entry : mergedFieldProjections.entrySet()) {
      mergedFieldEntries.put(
          entry.getKey().name(),
          new OpFieldProjectionEntry(
              entry.getKey(),
              entry.getValue(),
              TextLocation.UNKNOWN
          )
      );
    }

    return new OpRecordModelProjection(
        model,
        mergedFlag,
        mergedDefault,
        mergedParams,
        mergedAnnotations,
        mergedMetaProjection,
        mergedFieldEntries,
        mergedTails,
        TextLocation.UNKNOWN
    );
  }

  @Override
  public @NotNull OpRecordModelProjection postNormalizedForType(
      final @NotNull TypeApi targetType,
      final @NotNull OpRecordModelProjection n) {
    RecordTypeApi targetRecordType = (RecordTypeApi) targetType;

    final Map<String, OpFieldProjection> normalizedFields =
        RecordModelProjectionHelper.normalizeFields(targetRecordType, n);

    final Map<String, OpFieldProjectionEntry> normalizedFieldEntries = reattachFields(
        targetRecordType,
        normalizedFields,
        OpFieldProjectionEntry::new
    );

    return new OpRecordModelProjection(
        n.type(),
        n.flag(),
        n.defaultValue(),
        n.params(),
        n.annotations(),
        n.metaProjection(),
        normalizedFieldEntries,
        n.polymorphicTails(),
        TextLocation.UNKNOWN
    );
  }

  @Override
  public void resolve(
      final @Nullable ProjectionReferenceName name,
      final @NotNull OpRecordModelProjection value) {
    preResolveCheck(value);
    this.fieldProjections = value.fieldProjections();
    super.resolve(name, value);
  }

  @Override
  public boolean equals(Object o) {
    return super.equals(o) && RecordModelProjectionHelper.equals(this, o);
  }

  @Override
  public int hashCode() {
    return super.hashCode() * 31 + fieldProjections.size();
  }
}
