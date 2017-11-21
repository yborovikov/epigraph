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

package ws.epigraph.projections.op.delete;

import org.jetbrains.annotations.NotNull;
import ws.epigraph.lang.MessagesContext;
import ws.epigraph.projections.abs.AbstractEntityProjection;
import ws.epigraph.projections.op.OpModelProjection;
import ws.epigraph.projections.op.OpProjectionTraversal;
import ws.epigraph.types.TypeKind;

/**
 * Checks that 'delete' flag is only set on entity types
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpDeleteOnlyOnEntitiesChecker extends OpProjectionTraversal {
  private final @NotNull MessagesContext context;

  public OpDeleteOnlyOnEntitiesChecker(final @NotNull MessagesContext context) {this.context = context;}

  @Override
  protected boolean visitModelProjection(final @NotNull OpModelProjection<?, ?, ?, ?> projection) {
    AbstractEntityProjection<?, ?, ?> ep = projection.entityProjection();
    if (projection.flag() && (ep == null || ep.type().kind() == TypeKind.ENTITY))
      context.addWarning("'delete' flag is only supported on entity projections, ignoring", projection.location());
    return super.visitModelProjection(projection);
  }

}
