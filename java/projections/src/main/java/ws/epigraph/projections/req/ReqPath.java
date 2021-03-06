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
import ws.epigraph.types.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class ReqPath {

  private ReqPath() {}

  public static @NotNull ReqProjection<?, ?> pathEnd(@NotNull TypeApi type, @NotNull TextLocation location) {

    switch (type.kind()) {
      case ENTITY:
        return ReqEntityProjection.pathEnd(type, location);
      case RECORD:
        return ReqRecordModelProjection.pathEnd((RecordTypeApi) type, ReqParams.EMPTY, Directives.EMPTY, location);
      case MAP:
        return ReqMapModelProjection.pathEnd((MapTypeApi) type, ReqParams.EMPTY, Directives.EMPTY, location);
      case LIST:
        return ReqListModelProjection.pathEnd((ListTypeApi) type, ReqParams.EMPTY, Directives.EMPTY, location);
      case ENUM:
        throw new IllegalArgumentException("Unsupported model kind");
      case PRIMITIVE:
        return ReqPrimitiveModelProjection.pathEnd(
            (PrimitiveTypeApi) type,
            ReqParams.EMPTY,
            Directives.EMPTY,
            location
        );
    }

    return null;
  }

  public static @NotNull ReqProjection<?, ?> pathEnd(
      @NotNull DatumTypeApi type,
      @NotNull ReqParams params,
      @NotNull Directives directives,
      @NotNull TextLocation location) {

    switch (type.kind()) {
      case ENTITY:
        throw new IllegalArgumentException("Unsupported model kind");
      case RECORD:
        return ReqRecordModelProjection.pathEnd((RecordTypeApi) type, params, directives, location);
      case MAP:
        return ReqMapModelProjection.pathEnd((MapTypeApi) type, params, directives, location);
      case LIST:
        return ReqListModelProjection.pathEnd((ListTypeApi) type, params, directives, location);
      case ENUM:
        throw new IllegalArgumentException("Unsupported model kind");
      case PRIMITIVE:
        return ReqPrimitiveModelProjection.pathEnd((PrimitiveTypeApi) type, params, directives, location);
    }

    return null;
  }
}
