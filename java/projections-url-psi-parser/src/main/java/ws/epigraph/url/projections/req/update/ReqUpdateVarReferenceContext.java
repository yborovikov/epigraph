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

package ws.epigraph.url.projections.req.update;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.lang.Qn;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.VarReferenceContext;
import ws.epigraph.projections.req.update.ReqUpdateVarProjection;
import ws.epigraph.types.TypeApi;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqUpdateVarReferenceContext extends VarReferenceContext<ReqUpdateVarProjection> {

  public ReqUpdateVarReferenceContext(
      final @NotNull Qn referencesNamespace,
      final @Nullable VarReferenceContext<ReqUpdateVarProjection> parent) {
    super(referencesNamespace, parent);
  }

  @Override
  protected @NotNull ReqUpdateVarProjection newReference(
      final @NotNull TypeApi type,
      final @NotNull Qn name,
      final @NotNull TextLocation location) {

    return new ReqUpdateVarProjection(type, name, location);
  }
}