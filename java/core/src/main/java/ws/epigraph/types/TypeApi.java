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

package ws.epigraph.types;

import org.jetbrains.annotations.NotNull;
import ws.epigraph.annotations.Annotated;
import ws.epigraph.annotations.Annotations;
import ws.epigraph.names.TypeName;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public interface TypeApi extends Annotated {
  @NotNull TypeKind kind();

  @NotNull
  TypeName name();

  @NotNull List<@NotNull ? extends TypeApi> supertypes();

  /**
   * @see Class#isAssignableFrom(Class)
   */
  default boolean isAssignableFrom(@NotNull TypeApi type) {
    return type.equals(this) || type.supertypes().contains(this);
  }

  @NotNull Collection<@NotNull ? extends TagApi> tags();

  @NotNull Map<@NotNull String, @NotNull ? extends TagApi> tagsMap();

  /** Create data type without default tag */
  @NotNull DataTypeApi dataType();

  @Override
  default @NotNull Annotations annotations() { return Annotations.EMPTY; }
}
