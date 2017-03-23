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

package ws.epigraph.java.service.projections.req.input

import ws.epigraph.java.GenContext
import ws.epigraph.java.JavaGenNames.ln
import ws.epigraph.java.service.projections.req.input.ReqInputProjectionGen.{classNamePrefix, classNameSuffix}
import ws.epigraph.java.service.projections.req.{OperationInfo, ReqModelProjectionGen, ReqProjectionGen}
import ws.epigraph.lang.Qn
import ws.epigraph.projections.op.input._
import ws.epigraph.types.{DatumTypeApi, TypeKind}

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
abstract class ReqInputModelProjectionGen(
  protected val name: Option[Qn],
  protected val operationInfo: OperationInfo,
  op: OpInputModelProjection[_, _, _ <: DatumTypeApi, _],
  _baseNamespace: Qn,
  _namespaceSuffix: Qn,
  protected val ctx: GenContext) extends ReqInputProjectionGen with ReqModelProjectionGen {

  override type OpProjectionType <: OpInputModelProjection[_, _, _ <: DatumTypeApi, _]

  override protected def baseNamespace: Qn = ReqProjectionGen.baseNamespace(name, _baseNamespace)

  override protected def namespaceSuffix: Qn = ReqProjectionGen.namespaceSuffix(name, _namespaceSuffix)

  override val shortClassName: String = s"$classNamePrefix${ln(cType)}$classNameSuffix"

  override protected def reqVarProjectionFqn: Qn =
    Qn.fromDotSeparated("ws.epigraph.projections.req.input.ReqInputVarProjection")

  override protected def reqModelProjectionFqn: Qn =
  Qn.fromDotSeparated("ws.epigraph.projections.req.input.ReqInputModelProjection")

  override protected def reqModelProjectionParams: String = "<?, ?, ?>"
}

object ReqInputModelProjectionGen {
  def dataProjectionGen(
    name: Option[Qn],
    operationInfo: OperationInfo,
    op: OpInputModelProjection[_, _, _ <: DatumTypeApi, _],
    baseNamespace: Qn,
    namespaceSuffix: Qn,
    ctx: GenContext): ReqInputModelProjectionGen = op.`type`().kind() match {

    case TypeKind.RECORD =>
      new ReqInputRecordModelProjectionGen(
        name,
        operationInfo,
        op.asInstanceOf[OpInputRecordModelProjection],
        baseNamespace,
        namespaceSuffix,
        ctx
      )
    case TypeKind.MAP =>
      new ReqInputMapModelProjectionGen(
        name,
        operationInfo,
        op.asInstanceOf[OpInputMapModelProjection],
        baseNamespace,
        namespaceSuffix,
        ctx
      )
    case TypeKind.LIST =>
      new ReqInputListModelProjectionGen(
        name,
        operationInfo,
        op.asInstanceOf[OpInputListModelProjection],
        baseNamespace,
        namespaceSuffix,
        ctx
      )
    case TypeKind.PRIMITIVE =>
      new ReqInputPrimitiveModelProjectionGen(
        name,
        operationInfo,
        op.asInstanceOf[OpInputPrimitiveModelProjection],
        baseNamespace,
        namespaceSuffix,
        ctx
      )
    case x => throw new RuntimeException(s"Unsupported projection kind: $x")

  }
}
