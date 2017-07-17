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

package ws.epigraph.services.resources.epigraph.types;

import epigraph.schema.NameString;
import epigraph.schema.TagName;
import epigraph.schema.Tag_;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.services._resources.epigraph.projections.output.datumtypeprojection.OutputDatumTypeProjection;
import ws.epigraph.services._resources.epigraph.projections.output.tagprojection.OutputTag_Projection;
import ws.epigraph.types.TagApi;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class TagBuilder {
  private TagBuilder() {}

//  public static @NotNull Tag_ buildTag(
//      @NotNull TagApi tag,
//      @NotNull OutputTag_Projection projection,
//      @NotNull TypeBuilder.Context context) {
//
//    final Tag_.Builder builder = Tag_.create();
//
//    // name
//    builder.setName(TagName.create().setString(NameString.create(tag.name())));
//
//    // todo doc
//
//    // type
//    final @Nullable OutputDatumTypeProjection typeProjection = projection.type();
//    if (typeProjection != null) {
//      builder.setType(
//          DatumTypeBuilder.buildDatumType(
//              tag.type(),
//              typeProjection,
//              context
//          )
//      );
//    }
//
//    return builder;
//  }
}
