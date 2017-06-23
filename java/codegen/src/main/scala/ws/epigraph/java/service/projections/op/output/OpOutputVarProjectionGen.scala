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

package ws.epigraph.java.service.projections.op.output

import ws.epigraph.java.NewlineStringInterpolator.{NewlineHelper, i}
import ws.epigraph.java.ObjectGenUtils._
import ws.epigraph.java.service.ServiceObjectGenerators.gen
import ws.epigraph.java.{ObjectGen, ObjectGenContext}
import ws.epigraph.projections.op.output.{OpOutputTagProjectionEntry, OpOutputVarProjection}
import ws.epigraph.types.TypeApi

import scala.collection.JavaConversions._

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class OpOutputVarProjectionGen(p: OpOutputVarProjection) extends ObjectGen[OpOutputVarProjection](p) {

  override protected def generateObject(ctx: ObjectGenContext): String = {

    val opName = p.referenceName()
    if (opName != null) {
      val opNameString = p.referenceName().toString

      val visitedKey = "projections.op.output.output." + opNameString
      val methodName = "constructOutputVarProjectionFor$" + opNameString.replace(".", "_")

      if (!ctx.visited(visitedKey)) {

        ctx.addVisited(visitedKey)
        ctx.use("java.util.Map")
        ctx.use("java.util.HashMap")

        if (ctx.addField("private static Map<String, OpOutputVarProjection> outputProjectionRefs = new HashMap<>();"))
          ctx.addStatic("outputProjectionRefs = null;")
        ctx.addMethod(
          /*@formatter:off*/sn"""\
private static OpOutputVarProjection $methodName() {
  OpOutputVarProjection ref = outputProjectionRefs.get("$opNameString");
  if (ref != null && ref.isResolved()) return ref;
  if (ref == null) {
    ref = new OpOutputVarProjection(
      ${genTypeExpr(p.`type`(), ctx.gctx)},
      ${gen(p.location(), ctx)}
    );
    outputProjectionRefs.put("$opNameString", ref);
    OpOutputVarProjection value = new OpOutputVarProjection(
      ${genTypeExpr(p.`type`(), ctx.gctx)},
      ${i(genLinkedMap("String", "OpOutputTagProjectionEntry", p.tagProjections().entrySet().map{ e =>
        (normalizeTagName(e.getKey, ctx), genTagProjectionEntry(p.`type`(), e.getValue, ctx))}, ctx))},
      ${p.parenthesized().toString},
      ${i(if (p.polymorphicTails() == null) "null" else genList(p.polymorphicTails().map(gen(_, ctx)),ctx))},
      ${gen(p.location(), ctx)}
    );
    ref.resolve(${gen(opName, ctx)}, value);
  }
  return ref;
}"""/*@formatter:on*/
        )

      }

      s"$methodName()"

    } else {

      /*@formatter:off*/sn"""\
new OpOutputVarProjection(
  ${genTypeExpr(p.`type`(), ctx.gctx)},
  ${i(genLinkedMap("String", "OpOutputTagProjectionEntry", p.tagProjections().entrySet().map{ e =>
    (normalizeTagName(e.getKey, ctx), genTagProjectionEntry(p.`type`(), e.getValue, ctx))}, ctx))},
  ${p.parenthesized().toString},
  ${i(if (p.polymorphicTails() == null) "null" else genList(p.polymorphicTails().map(gen(_, ctx)),ctx))},
  ${gen(p.location(), ctx)}
)"""/*@formatter:on*/

    }

  }

  private def genTagProjectionEntry(
    t: TypeApi,
    tpe: OpOutputTagProjectionEntry,
    ctx: ObjectGenContext): String = {

    ctx.use(classOf[OpOutputTagProjectionEntry].getName)

    /*@formatter:off*/sn"""\
new OpOutputTagProjectionEntry(
  ${genTagExpr(t, tpe.tag().name(), ctx.gctx)},
  ${i(gen(tpe.projection(), ctx))},
  ${gen(tpe.location(), ctx)}
)"""/*@formatter:on*/
  }
}
