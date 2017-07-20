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

import epigraph.schema.FieldName;
import epigraph.schema.NameString;
import ws.epigraph.services._resources.epigraph.projections.output.fieldprojection.Field_Assembler;
import ws.epigraph.types.FieldApi;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class FieldAssemblerImpl extends Field_Assembler<FieldApi> {
  public static final FieldAssemblerImpl INSTANCE = new FieldAssemblerImpl();

  private FieldAssemblerImpl() {
    super(
        AnnotationsAssemblerImpl.INSTANCE.on(FieldApi::annotations),
        (f, p, c) -> FieldName.create().setString(NameString.create(f.name())),
        DataTypeAssemblerImpl.INSTANCE.on(FieldApi::dataType)
    );
  }

}
