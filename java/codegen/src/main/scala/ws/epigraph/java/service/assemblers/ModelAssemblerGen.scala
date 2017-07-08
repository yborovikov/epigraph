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

package ws.epigraph.java.service.assemblers

import java.nio.file.Path

import ws.epigraph.compiler.CDatumType
import ws.epigraph.java.{JavaGen, JavaGenUtils}
import ws.epigraph.java.service.projections.req.output.ReqOutputModelProjectionGen
import ws.epigraph.java.JavaGenNames.{ln, lqn2}
import ws.epigraph.lang.Qn
import ws.epigraph.java.NewlineStringInterpolator.{NewlineHelper, i}

/**
 * Base trait for model assembler generators
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
trait ModelAssemblerGen extends JavaGen {
  protected type G <: ReqOutputModelProjectionGen

  protected def g: G

  protected val namespace: Qn = g.namespace

  protected val nsString: String = namespace.toString

  protected val cType: CDatumType = JavaGenUtils.toCType(g.op.`type`())

  val shortClassName: String = ln(cType) + "Assembler"

  override protected def relativeFilePath: Path = JavaGenUtils.fqnToPath(g.namespace).resolve(shortClassName + ".java")

  protected val t: String = lqn2(cType, nsString)

  case class TailParts(tailProjectionGen: ReqOutputModelProjectionGen) {
    def `type`: CDatumType = JavaGenUtils.toCType(tailProjectionGen.op.`type`())

    def typeString: String = lqn2(`type`, nsString)

    def assembler: String = JavaGenUtils.lo(ln(`type`)) + "Assembler"

    def assemblerType: String = s"Assembler<? super D, ? super ${ tailProjectionGen.fullClassName }, ? extends $typeString.Value>"

    def javadoc: String = s"$assembler {@code ${ ln(`type`) }} value assembler"
  }

  protected val tps: Seq[TailParts] = g.normalizedTailGenerators.values.map { tg =>
    TailParts(tg.asInstanceOf[ReqOutputModelProjectionGen])
  }.toSeq

  protected val hasTails: Boolean = g.normalizedTailGenerators.nonEmpty

  protected def defaultBuild: String

  protected lazy val nonTailsBuild: String = /*@formatter:off*/sn"""{
      ${i(defaultBuild)}
    }"""/*@formatter:on*/

  protected lazy val tailsBuild: String = /*@formatter:off*/sn"""
      return ${g.shortClassName}.dispatcher.dispatch(
          p,
          ${if (hasTails) "typeExtractor.apply(dto)" else s"$t.type"},
${if (tps.nonEmpty) tps.map { tp => s"tp -> ${tp.assembler}.assemble(dto, tp, ctx)" }.mkString("          ",",\n          ",",\n") else ""}\
          () -> {
            ${i(defaultBuild)}
          }
      );"""/*@formatter:on*/
}
