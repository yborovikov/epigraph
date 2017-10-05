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

package ws.epigraph.projections.req;

import org.jetbrains.annotations.NotNull;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.types.DataTypeApi;

import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqFieldProjection extends AbstractReqFieldProjection<
    ReqEntityProjection,
    ReqTagProjectionEntry,
    ReqModelProjection<?, ?, ?>,
    ReqFieldProjection
    > {

//  private final boolean flag;

  public ReqFieldProjection(
//      @NotNull ReqParams reqParams,
//      @NotNull Annotations annotations,
      @NotNull ReqEntityProjection projection,
//      boolean flag,
      @NotNull TextLocation location) {
    super(/*reqParams, annotations, */projection, location);
//    this.flag = flag;
  }

  /**
   * @return {@code true} iff field entity projection is flag
   */
  public boolean flag() {
    return flag(entityProjection());
  }

  private static boolean flag(@NotNull ReqEntityProjection vp) {
    return vp.flag();
  }

  @Override
  public @NotNull ReqFieldProjection setEntityProjection(final @NotNull ReqEntityProjection varProjection) {
    return new ReqFieldProjection(varProjection, TextLocation.UNKNOWN);
  }

  @Override
  protected ReqFieldProjection merge(
      final @NotNull DataTypeApi type,
      final @NotNull List<ReqFieldProjection> fieldProjections,
//      final @NotNull ReqParams mergedParams,
//      final @NotNull Annotations mergedAnnotations,
      final @NotNull ReqEntityProjection mergedEntityProjection) {

    return new ReqFieldProjection(
//        mergedParams,
//        mergedAnnotations,
        mergedEntityProjection,
//        fieldProjections.stream().anyMatch(ReqOutputFieldProjection::flag),
        TextLocation.UNKNOWN
    );
  }

//  @Override
//  public boolean equals(final Object o) {
//    if (this == o) return true;
//    if (o == null || getClass() != o.getClass()) return false;
//    if (!super.equals(o)) return false;
//    final ReqOutputFieldProjection that = (ReqOutputFieldProjection) o;
//    return flag == that.flag;
//  }
//
//  @Override
//  public int hashCode() {
//    return Objects.hash(super.hashCode(), flag);
//  }
}
