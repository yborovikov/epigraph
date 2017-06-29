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

package ws.epigraph.java.service.projections.req.update

import ws.epigraph.java.GenContext
import ws.epigraph.java.JavaGenNames.ln
import ws.epigraph.java.NewlineStringInterpolator.NewlineHelper
import ws.epigraph.java.service.projections.req._
import ws.epigraph.java.service.projections.req.update.ReqUpdateProjectionGen.{classNamePrefix, classNameSuffix}
import ws.epigraph.lang.Qn
import ws.epigraph.projections.op.input._
import ws.epigraph.types.{DatumTypeApi, TypeKind}

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
abstract class ReqUpdateModelProjectionGen(
  protected val baseNamespaceProvider: BaseNamespaceProvider,
  op: OpInputModelProjection[_, _, _ <: DatumTypeApi, _],
  baseNamespaceOpt: Option[Qn],
  _namespaceSuffix: Qn,
  protected val ctx: GenContext) extends ReqUpdateProjectionGen with ReqModelProjectionGen {

  override type OpProjectionType <: OpInputModelProjection[_, _, _ <: DatumTypeApi, _]

  override protected def baseNamespace: Qn = ReqProjectionGen.baseNamespace(
    referenceName,
    baseNamespaceOpt.getOrElse(super.baseNamespace)
  )

  override protected def namespaceSuffix: Qn = ReqProjectionGen.namespaceSuffix(referenceName, _namespaceSuffix)

  override val shortClassName: String = s"$classNamePrefix${ln(cType)}$classNameSuffix"

  override protected def reqVarProjectionFqn: Qn =
    Qn.fromDotSeparated("ws.epigraph.projections.req.update.ReqUpdateVarProjection")

  override protected def reqModelProjectionFqn: Qn =
  Qn.fromDotSeparated("ws.epigraph.projections.req.update.ReqUpdateModelProjection")

  override protected def reqModelProjectionParams: String = "<?, ?, ?>"

  protected lazy val replace: CodeChunk = CodeChunk(/*@formatter:off*/sn"""\
  /**
   * @return {@code true} if model must be replaced (updated), and {@code false} if it must be patched
   */
  public boolean replace() {
    return raw.replace();
  }
"""/*@formatter:on*/)

}

object ReqUpdateModelProjectionGen {
  def dataProjectionGen(
    baseNamespaceProvider: BaseNamespaceProvider,
    op: OpInputModelProjection[_, _, _ <: DatumTypeApi, _],
    baseNamespaceOpt: Option[Qn],
    namespaceSuffix: Qn,
    ctx: GenContext): ReqUpdateModelProjectionGen = op.`type`().kind() match {

    case TypeKind.RECORD =>
      new ReqUpdateRecordModelProjectionGen(
        baseNamespaceProvider,
        op.asInstanceOf[OpInputRecordModelProjection],
        baseNamespaceOpt,
        namespaceSuffix,
        ctx
      )
    case TypeKind.MAP =>
      new ReqUpdateMapModelProjectionGen(
        baseNamespaceProvider,
        op.asInstanceOf[OpInputMapModelProjection],
        baseNamespaceOpt,
        namespaceSuffix,
        ctx
      )
    case TypeKind.LIST =>
      new ReqUpdateListModelProjectionGen(
        baseNamespaceProvider,
        op.asInstanceOf[OpInputListModelProjection],
        baseNamespaceOpt,
        namespaceSuffix,
        ctx
      )
    case TypeKind.PRIMITIVE =>
      new ReqUpdatePrimitiveModelProjectionGen(
        baseNamespaceProvider,
        op.asInstanceOf[OpInputPrimitiveModelProjection],
        baseNamespaceOpt,
        namespaceSuffix,
        ctx
      )
    case x => throw new RuntimeException(s"Unsupported projection kind: $x")

  }
}
