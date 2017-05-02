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

package ws.epigraph.projections.req.update;

import de.uka.ilkd.pp.Layouter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.projections.ProjectionsPrettyPrinterContext;
import ws.epigraph.projections.gen.ProjectionReferenceName;
import ws.epigraph.projections.req.AbstractReqProjectionsPrettyPrinter;
import ws.epigraph.types.TypeKind;

import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqUpdateProjectionsPrettyPrinter<E extends Exception>
    extends AbstractReqProjectionsPrettyPrinter<
    ReqUpdateVarProjection,
    ReqUpdateTagProjectionEntry,
    ReqUpdateModelProjection<?, ?, ?>,
    ReqUpdateRecordModelProjection,
    ReqUpdateFieldProjectionEntry,
    ReqUpdateFieldProjection,
    E> {

  public ReqUpdateProjectionsPrettyPrinter(
      final @NotNull Layouter<E> layouter,
      final @NotNull ProjectionsPrettyPrinterContext<ReqUpdateVarProjection, ReqUpdateModelProjection<?, ?, ?>> context) {
    super(layouter, context);
  }

  public ReqUpdateProjectionsPrettyPrinter(final @NotNull Layouter<E> layouter) {
    this(layouter, new ProjectionsPrettyPrinterContext<>(ProjectionReferenceName.EMPTY));
  }

  @Override
  protected void printVarOnly(final @NotNull ReqUpdateVarProjection p, final int pathSteps) throws E {
    if (p.type().kind() != TypeKind.UNION) {
      ReqUpdateTagProjectionEntry tp = p.tagProjections().values().iterator().next();
      ReqUpdateModelProjection<?, ?, ?> projection = tp.projection();
      if (isUpdateModelProjection(projection))
        l.print(":"); // print ':' before '+' for tag-less sef var projections
    }
    super.printVarOnly(p, pathSteps);
  }

  @Override
  public void printTag(
      final @Nullable String tagName,
      final @NotNull ReqUpdateTagProjectionEntry entry,
      final int pathSteps)
      throws E {

    final ReqUpdateModelProjection<?, ?, ?> projection = entry.projection();
    if (isUpdateModelProjection(projection)) l.print("+");

    super.printTag(tagName, entry, pathSteps);
  }

  @Override
  protected String modelTailTypeNamePrefix(final @NotNull ReqUpdateModelProjection<?, ?, ?> projection) {
    return isUpdateModelProjection(projection) ? "+" : super.modelTailTypeNamePrefix(projection);
  }

  private boolean isUpdateModelProjection(final ReqUpdateModelProjection<?, ?, ?> projection) {
    return projection.replace() && projection.type().kind() != TypeKind.PRIMITIVE;
  }

  @Override
  public void printModelOnly(@NotNull ReqUpdateModelProjection<?, ?, ?> mp, int pathSteps) throws E {
    if (mp instanceof ReqUpdateRecordModelProjection)
      printModelOnly((ReqUpdateRecordModelProjection) mp);
    else if (mp instanceof ReqUpdateMapModelProjection)
      printModelOnly((ReqUpdateMapModelProjection) mp);
    else if (mp instanceof ReqUpdateListModelProjection)
      printModelOnly((ReqUpdateListModelProjection) mp, pathSteps);
  }

  private void printModelOnly(@NotNull ReqUpdateRecordModelProjection mp) throws E {
    Map<String, ReqUpdateFieldProjectionEntry> fieldProjections = mp.fieldProjections();

    l.print("(").beginCInd();
    boolean first = true;
    for (Map.Entry<String, ReqUpdateFieldProjectionEntry> entry : fieldProjections.entrySet()) {
      if (first) first = false;
      else l.print(",");
      l.brk();

      print(entry.getKey(), entry.getValue().fieldProjection(), 0);

    }
    l.brk(1, -l.getDefaultIndentation()).end().print(")");
  }

  @Override
  protected String fieldNamePrefix(final @NotNull ReqUpdateFieldProjection fieldProjection) {
    return fieldProjection.replace() ? "+" : "";
  }

  private void printModelOnly(ReqUpdateMapModelProjection mp) throws E {
//    if (mp.updateKeys()) l.print("+");
    printMapModelProjection(mp.keys(), mp.updateKeys() ? "+" : "", mp.itemsProjection());
  }

  private void printModelOnly(ReqUpdateListModelProjection mp, int pathSteps) throws E {
    if (pathSteps > 0) throw new IllegalArgumentException(
        String.format("Encountered list projection while still having %d path steps", pathSteps)
    );
    l.beginIInd();
    l.print("*(").brk();
    printVar(mp.itemsProjection(), 0);
    l.brk(1, -l.getDefaultIndentation()).end().print(")");
  }

}
