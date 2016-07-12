/* Created by yegor on 7/11/16. */

package com.sumologic.epigraph.scala

import com.sumologic.epigraph.schema.compiler.{CPrimitiveTypeDef, CTypeKind}

object PrimitiveGen extends ScalaGen {

  override type From = CPrimitiveTypeDef

  override def generate(t: CPrimitiveTypeDef): String =
    s"""
/*
 * Standard header
 */

package ${scalaFqn(t.name.fqn.removeLastSegment())}

import com.sumologic.epigraph.xp.data._
import com.sumologic.epigraph.xp.data.immutable._
import com.sumologic.epigraph.xp.data.mutable._
import com.sumologic.epigraph.xp.types._

trait ${baseName(t)} extends${withParents(t, baseName)} ${Kind(t)}Datum[${baseName(t)}]

trait ${immName(t)} extends${withParents(t, immName)} ${baseName(t)} with Imm${Kind(t)}Datum[${baseName(t)}]

trait ${mutName(t)} extends${withParents(t, mutName)} ${baseName(t)} with Mut${Kind(t)}Datum[${baseName(t)}]

trait ${bldName(t)} extends ${baseName(t)} with ${Kind(t)}DatumBuilder[${baseName(t)}]

object ${objName(t)} extends ${Kind(t)}Type[${baseName(t)}](
  namespace \\ "${baseName(t)}",
  Seq(${parentNames(t, objName)})
) {

  override def createMutable: ${mutName(t)} = new ${mutImplName(t)}

  private class ${mutImplName(t)} extends ${mutName(t)} {
    override def dataType: ${objName(t)}.type = ${objName(t)}
  }

  override def createBuilder: ${bldName(t)} = new ${bldImplName(t)}

  private class ${bldImplName(t)} extends ${bldName(t)} {
    override def dataType: ${objName(t)}.type = ${objName(t)}
  }

}
  """.trim

  def Kind(t: CPrimitiveTypeDef): String = Kinds.getOrElse(t.kind, throw new UnsupportedOperationException(t.kind.name))

  val Kinds: Map[CTypeKind, String] = Map(
    CTypeKind.STRING -> "String",
    CTypeKind.INTEGER -> "Integer",
    CTypeKind.LONG -> "Long",
    CTypeKind.DOUBLE -> "Double",
    CTypeKind.BOOLEAN -> "Boolean"
  )

}
/*
  trait BarId extends FooId with IntegerDatum[BarId]


  trait ImmBarId extends ImmFooId with BarId with ImmIntegerDatum[BarId]


  trait MutBarId extends MutFooId with BarId with MutIntegerDatum[BarId]


  trait BarIdBuilder extends BarId with IntegerDatumBuilder[BarId]


  object BarId extends IntegerType[BarId](ns \ "BarId", Seq(FooId)) {

    override def createImmutable(native: Int): ImmBarId = new ImmBarIdImpl(native)


    private class ImmBarIdImpl(native: Int) extends ImmIntegerDatumImpl(native) with ImmBarId


    override def createMutable(native: Int): MutBarId = new MutBarIdImpl(native)


    private class MutBarIdImpl(native: Int) extends MutIntegerDatumImpl(native) with MutBarId


    override def createBuilder: BarIdBuilder = createBuilder(NativeDefault)

    override def createBuilder(native: Native): BarIdBuilder = new BarIdBuilderImpl(native)


    private class BarIdBuilderImpl(native: Native) extends IntegerDatumBuilderImpl(native) with BarIdBuilder


  }

 */