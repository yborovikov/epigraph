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

package ws.epigraph.projections.op.input;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.data.Datum;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.op.AbstractOpModelProjection;
import ws.epigraph.projections.op.OpParams;
import ws.epigraph.types.DatumType;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class OpInputModelProjection<
    MP extends OpInputModelProjection</*MP*/?, M, D>,
    M extends DatumType,
    D extends Datum>
    extends AbstractOpModelProjection<MP, M> {

  protected final boolean required;
  @Nullable
  protected final D defaultValue;

  public OpInputModelProjection(
      @NotNull M model,
      boolean required,
      @Nullable D defaultValue,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @Nullable MP metaProjection,
      @NotNull TextLocation location) {
    super(model, metaProjection, params, annotations, location);
    this.required = required;
    this.defaultValue = defaultValue;
  }

  public boolean required() { return required; }

  @Nullable
  public D defaultValue() { return defaultValue; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    OpInputModelProjection<?, ?, ?> that = (OpInputModelProjection<?, ?, ?>) o;
    return required == that.required && Objects.equals(defaultValue, that.defaultValue);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), required, defaultValue);
  }
}