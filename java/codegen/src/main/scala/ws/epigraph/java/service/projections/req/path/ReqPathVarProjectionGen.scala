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

package ws.epigraph.java.service.projections.req.path

import ws.epigraph.java.GenContext
import ws.epigraph.java.JavaGenNames.{jn, ln}
import ws.epigraph.java.service.projections.req.path.ReqPathProjectionGen.{classNamePrefix, classNameSuffix}
import ws.epigraph.java.service.projections.req.{OperationInfo, ReqProjectionGen, ReqVarProjectionGen}
import ws.epigraph.lang.Qn
import ws.epigraph.projections.op.path._
import ws.epigraph.types.TypeKind

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class ReqPathVarProjectionGen(
  protected val operationInfo: OperationInfo,
  protected val op: OpVarPath,
  override protected val namespaceSuffix: Qn,
  protected val ctx: GenContext) extends ReqPathProjectionGen with ReqVarProjectionGen {

  override type OpProjectionType = OpVarPath
  override type OpTagProjectionEntryType = OpTagPath

  override val shortClassName: String = s"$classNamePrefix${ln(cType)}$classNameSuffix"

  override protected def tailGenerator(
    op: OpVarPath,
    normalized: Boolean): ReqProjectionGen = throw new RuntimeException("paths have no tails")

  override protected def tagGenerator(tpe: OpTagPath): ReqPathProjectionGen =
    ReqPathModelProjectionGen.dataProjectionGen(
      operationInfo,
      tpe.projection(),
      namespaceSuffix.append(jn(tpe.tag().name()).toLowerCase),
      ctx
    )

  override protected def generate: String = generate(
    Qn.fromDotSeparated("ws.epigraph.projections.req.path.ReqVarPath"),
    Qn.fromDotSeparated("ws.epigraph.projections.req.path.ReqTagPath")
  )
}

object ReqPathVarProjectionGen {
  def dataProjectionGen(
    operationInfo: OperationInfo,
    op: OpVarPath,
    namespaceSuffix: Qn,
    ctx: GenContext): ReqPathProjectionGen = op.`type`().kind() match {

    case TypeKind.UNION =>
      new ReqPathVarProjectionGen(operationInfo, op, namespaceSuffix, ctx)
    case TypeKind.RECORD =>
      new ReqPathRecordModelProjectionGen(
        operationInfo,
        op.singleTagProjection().projection().asInstanceOf[OpRecordModelPath],
        namespaceSuffix,
        ctx
      )
    case TypeKind.MAP =>
      new ReqPathMapModelProjectionGen(
        operationInfo,
        op.singleTagProjection().projection().asInstanceOf[OpMapModelPath],
        namespaceSuffix,
        ctx
      )
    case TypeKind.PRIMITIVE =>
      new ReqPathPrimitiveModelProjectionGen(
        operationInfo,
        op.singleTagProjection().projection().asInstanceOf[OpPrimitiveModelPath],
        namespaceSuffix,
        ctx
      )
    case x => throw new RuntimeException(s"Unknown path kind: $x")

  }
}
