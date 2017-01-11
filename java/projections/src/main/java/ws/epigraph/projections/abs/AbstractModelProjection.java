/*
 * Copyright 2016 Sumo Logic
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

package ws.epigraph.projections.abs;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.gen.GenModelProjection;
import ws.epigraph.types.DatumTypeApi;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class AbstractModelProjection<
    MP extends GenModelProjection</*MP*/?, /*SMP*/?, ?>,
    SMP extends GenModelProjection</*MP*/?, /*SMP*/?, ?>,
    M extends DatumTypeApi> implements GenModelProjection<MP, SMP, M> {

  protected final @NotNull M model;
  protected final @Nullable MP metaProjection;
  protected final @NotNull Annotations annotations;
  private final @NotNull TextLocation location;

  protected AbstractModelProjection(
      @NotNull M model,
      @Nullable MP metaProjection,
      @NotNull Annotations annotations,
      @NotNull TextLocation location
  ) {
    this.model = model;
    this.metaProjection = metaProjection;
    this.annotations = annotations;
    this.location = location;
  }

  @Override
  public @NotNull M model() { return model; }

  @Override
  public @Nullable MP metaProjection() { return metaProjection; }

  @Override
  public @NotNull Annotations annotations() { return annotations; }

  @SuppressWarnings("unchecked")
  @Override
  /* static */
  public SMP merge(
      final @NotNull M model,
      final @NotNull List<SMP> modelProjections) {

    if (modelProjections.isEmpty()) return null;
    if (modelProjections.size() == 1) return modelProjections.get(0);

    List<Annotations> annotationsList = new ArrayList<>();
    List<MP> metaProjectionsList = new ArrayList<>();

    for (final GenModelProjection<?, ?, ?> p : modelProjections) {
      AbstractModelProjection<MP, SMP, ?> mp = (AbstractModelProjection<MP, SMP, ?>) p;
      annotationsList.add(mp.annotations());
      final @Nullable MP meta = mp.metaProjection();
      if (meta != null) metaProjectionsList.add(meta);
    }

    final MP mergedMetaProjection;
    if (metaProjectionsList.isEmpty()) mergedMetaProjection = null;
    else {
      final MP metaProjection = metaProjectionsList.get(0);
      DatumTypeApi metaModel = model/*.metaModel()*/; // TODO should get meta-model type here
      mergedMetaProjection = ((GenModelProjection<MP, MP, M>) metaProjection).merge((M) metaModel, metaProjectionsList);
    }

    return merge(
        model,
        modelProjections,
        Annotations.merge(annotationsList),
        mergedMetaProjection
    );
  }

  protected SMP merge(
      @NotNull M model,
      @NotNull List<SMP> modelProjections,
      @NotNull Annotations mergedAnnotations,
      @Nullable MP mergedMetaProjection) {

    throw new RuntimeException("unimplemented"); // todo
  }

  @Override
  public @NotNull TextLocation location() { return location; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AbstractModelProjection<?, ?, ?> that = (AbstractModelProjection<?, ?, ?>) o;
    return Objects.equals(model, that.model) &&
           Objects.equals(metaProjection, that.metaProjection) &&
           Objects.equals(annotations, that.annotations);
  }

  @Override
  public int hashCode() {
    return Objects.hash(model, metaProjection, annotations);
  }
}
