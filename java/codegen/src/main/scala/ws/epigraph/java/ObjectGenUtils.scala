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

package ws.epigraph.java

import ws.epigraph.compiler.{CDatumType, CTypeApiWrapper}
import ws.epigraph.java.NewlineStringInterpolator.{NewlineHelper, i}
import ws.epigraph.java.service.projections.req.CodeChunk
import ws.epigraph.types._

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
object ObjectGenUtils {
  val INDENT = 2
  // default indent
  val INDENT_SPACES: String = JavaGenUtils.spaces(INDENT)

  def genList(items: Seq[String], ctx: ObjectGenContext): String = {
    if (items.isEmpty) {
      ctx.use("java.util.Collections")
      "Collections.emptyList()"
    } else if (items.size == 1) {
      ctx.use("java.util.Collections")
      s"Collections.singletonList(${ items.head })"
    } else {
      ctx.use("java.util.Arrays")
      val indentedItems = items.map(JavaGenUtils.indent(_, INDENT))
      indentedItems.mkString("Arrays.asList(\n", ",\n", "\n)")
    }
  }

  def genVararg(items: Iterable[String], insertNewlines: Boolean, ctx: ObjectGenContext): String = {
    val separator = if (insertNewlines) ",\n" else ", "
    items.mkString(separator)
  }

  def genLinkedMap(
    keyType: String,
    valueType: String,
    entries: Iterable[(String, String)],
    ctx: ObjectGenContext): String = genMap("LinkedHashMap", keyType, valueType, entries, ctx)

  def genHashMap(
    keyType: String,
    valueType: String,
    entries: Iterable[(String, String)],
    ctx: ObjectGenContext): String = genMap("HashMap", keyType, valueType, entries, ctx)

  def genMap(
    mapClass: String,
    keyType: String,
    valueType: String,
    entries: Iterable[(String, String)],
    ctx: ObjectGenContext): String = {

    val map = ctx.use("java.util." + mapClass)

    if (entries.isEmpty) s"new $map<$keyType, $valueType>(0)"
    else if (entries.size == 1) {
      val util = ctx.use("ws.epigraph.util.Util")
      val (key, value) = entries.head
      /*@formatter:off*/sn"""\
$util.createSingleton$mapClass(
  $key,
  ${i(value)}
)"""/*@formatter:on*/
    } else {
      val util = ctx.use("ws.epigraph.util.Util")

      val entriesSeq = entries.toSeq // to allow iterating twice

      val keys = entriesSeq.map(_._1).mkString(s"new $keyType[]{", ", ", "}")
      val values = entriesSeq
        .map(e => JavaGenUtils.indent(e._2, INDENT))
        .mkString(s"new $valueType[]{\n", ",\n", "\n}")

      /*@formatter:off*/sn"""\
$util.create$mapClass(
  $keys,
  ${i(values)}
)"""/*@formatter:on*/
    }

  }

  // using lambdas to construct maps makes javac way too slow
//  def genMap(
//    mapClass: String,
//    keyType: String,
//    valueType: String,
//    entries: Iterable[(String, String)],
//    ctx: ServiceGenContext): String = {
//
//    ctx.addImport(s"java.util.$mapClass")
//
//    if (entries.isEmpty) s"new $mapClass<$keyType, $valueType>()"
//    else {
//      ctx.addImport("java.util.AbstractMap")
//      ctx.addImport("java.util.Map")
//      ctx.addImport("java.util.stream.Collectors")
//      ctx.addImport("java.util.stream.Stream")
//
//      val generatedEntries = entries.map{ case (k, v) => s"${INDENT_SPACES}new AbstractMap.SimpleEntry<>($k, $v)" }
//      generatedEntries.mkString(
//        s"Stream.<AbstractMap.Entry<$keyType, $valueType>>of(\n",
//        ",\n",
//        s"\n).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (x, y) -> y, $mapClass::new))"
//      )
//    }
//  }

  def genImports(ctx: ObjectGenContext): String = {
    ctx.imports.map{ i => s"import $i;" }.mkString("", "\n", "\n")
  }

  def genFields(ctx: ObjectGenContext): String = ctx.fields.toList.sorted.mkString("", "\n", "\n")

  def genMethods(ctx: ObjectGenContext): String = ctx.methods.foldLeft(CodeChunk.empty)(_ + _).code

  def genStatic(ctx: ObjectGenContext): String = ctx.static.foldLeft(CodeChunk.empty)(_ + _).code

  def genTypeClassRef(t: TypeApi, ctx: GenContext): String = {
    val w: CTypeApiWrapper = t.asInstanceOf[CTypeApiWrapper]
    val qn = ctx.generatedTypes.get(w.cType.name)
    if (qn == null) throw new NullPointerException(s"No type generated for '${ w.cType.name.name }'")
    qn.toString
  }

  def genDataRef(t: TypeApi, ctx: GenContext): String = { // use lqdrn2?
    val typeClass = genTypeClassRef(t, ctx)
    if (t.isInstanceOf[DatumTypeApi]) typeClass + ".Data"
    else typeClass
  }

  def genTypeExpr(t: TypeApi, ctx: GenContext): String = {
    genTypeClassRef(t, ctx) + ".Type.instance()"
  }

  def genTagExpr(t: TypeApi, tagName: String, ctx: GenContext): String =
    if (tagName == CDatumType.ImpliedDefaultTagName)
      genTypeExpr(t, ctx) + ".self()"
    else
      genTypeClassRef(t, ctx) + "." + JavaGenNames.tcn(tagName)

  def genFieldExpr(t: TypeApi, fieldName: String, ctx: GenContext): String =
    genTypeClassRef(t, ctx) + "." + JavaGenNames.fcn(fieldName)

  def genDataTypeExpr(dt: DataTypeApi, gctx: GenContext): String = dt.`type`() match {
    case a: DatumTypeApi => genTypeExpr(a, gctx) + ".dataType()"
    case u: EntityTypeApi =>
      val tagExpr = Option(dt.retroTag()).map(_.name()) match {
        case Some(tagName) => genTagExpr(u, tagName, gctx)
        case None => "null"
      }
      genTypeExpr(dt.`type`(), gctx) + ".dataType(" + tagExpr + ")"
  }

  def normalizeTagName(tagName: String, ctx: ObjectGenContext): String =
    if (tagName == CDatumType.ImpliedDefaultTagName) {
      ctx.use(classOf[DatumTypeApi].getName)
      "DatumTypeApi.MONO_TAG_NAME"
    } else "\"" + tagName + "\""
}
