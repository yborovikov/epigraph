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

package ws.epigraph.projections.req;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.abs.AbstractModelProjection;
import ws.epigraph.types.DatumType;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class AbstractReqModelProjection<
    MP extends AbstractReqModelProjection</*MP*/?, ?>,
    M extends DatumType>
    extends AbstractModelProjection<MP, M> {

  @NotNull
  protected final ReqParams params;

  public AbstractReqModelProjection(
      @NotNull final M model,
      @NotNull ReqParams params,
      @Nullable final MP metaProjection,
      @NotNull final Annotations annotations,
      @NotNull final TextLocation location) {

    super(model, metaProjection, annotations, location);
    this.params = params;
  }

  @NotNull
  public ReqParams params() { return params; }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    final AbstractReqModelProjection<?, ?> that = (AbstractReqModelProjection<?, ?>) o;
    return Objects.equals(params, that.params);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), params);
  }
}
