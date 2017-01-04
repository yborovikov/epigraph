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

package ws.epigraph.java.service.gdata

import ws.epigraph.gdata.GData
import ws.epigraph.java.NewlineStringInterpolator.{NewlineHelper, i}
import ws.epigraph.java.service.ServiceObjectGen.gen
import ws.epigraph.java.service.{ServiceGenContext, ServiceGenUtils, ServiceObjectGen}

import scala.collection.JavaConversions._

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class GDataGen(data: GData) extends ServiceObjectGen[GData](data) {
  override protected def generateObject(ctx: ServiceGenContext): String =
  /*@formatter:off*/sn"""\
new GenMapDatum(
  ${gen(data.typeRef(), ctx)},
  ${i(ServiceGenUtils.genLinkedMap("String", "GDatum", data.tags().entrySet().map{e => (gen(e.getKey, ctx), gen(e.getValue, ctx))}, ctx))},
  ${gen(data.location(), ctx)}
)"""/*@formatter:on*/
}