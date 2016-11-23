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

package ws.epigraph.projections.op.output;

import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.gen.GenModelProjection;
import ws.epigraph.projections.gen.GenPrimitiveModelProjection;
import ws.epigraph.projections.op.OpParams;
import ws.epigraph.types.PrimitiveType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpOutputPrimitiveModelProjection
    extends OpOutputModelProjection<OpOutputPrimitiveModelProjection, PrimitiveType<?>>
    implements GenPrimitiveModelProjection<OpOutputPrimitiveModelProjection, PrimitiveType<?>> {

  public OpOutputPrimitiveModelProjection(
      @NotNull PrimitiveType model,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @Nullable OpOutputPrimitiveModelProjection metaProjection,
      @NotNull TextLocation location) {
    super(model, params, annotations, metaProjection, location);
  }

  @Override
  protected OpOutputPrimitiveModelProjection merge(
      @NotNull final PrimitiveType<?> model,
      @NotNull final List<? extends GenModelProjection<?, ?>> modelProjections,
      @NotNull final OpParams mergedParams,
      @NotNull final Annotations mergedAnnotations,
      @Nullable final OpOutputPrimitiveModelProjection mergedMetaProjection) {

    return new OpOutputPrimitiveModelProjection(
        model,
        mergedParams,
        mergedAnnotations,
        mergedMetaProjection,
        TextLocation.UNKNOWN
    );
  }
}
