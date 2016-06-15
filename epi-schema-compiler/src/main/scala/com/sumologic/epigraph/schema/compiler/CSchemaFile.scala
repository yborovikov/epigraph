/* Created by yegor on 6/9/16. */

package com.sumologic.epigraph.schema.compiler

import com.sumologic.epigraph.schema.parser.Fqn
import com.sumologic.epigraph.schema.parser.psi._
import org.jetbrains.annotations.Nullable

import scala.collection.JavaConversions._

class CSchemaFile(val psi: SchemaFile)(implicit val ctx: CContext) {

  val filename: String = psi.getName // TODO capture full path/name in SchemaFile

  val lnu: LineNumberUtil = new LineNumberUtil(psi.getText, ctx.tabWidth)

  val namespace: CNamespace = new CNamespace(psi.getNamespaceDecl)

  val imports: Map[String, CImport] = psi.getImportStatements.map(new CImport(_)).map { ci =>
    (ci.alias, ci)
  }(collection.breakOut) // TODO deal with dupes (foo.Baz and bar.Baz); pre-populate with implicit imports

  @Nullable
  private val defs: SchemaDefs = psi.getDefs

  val types: Seq[CType] = if (defs == null) Nil else defs.getTypeDefWrapperList.map(CType.apply(this, _))

  val supplements: Seq[CSupplement] = if (defs == null) Nil else defs.getSupplementDefList.map(new CSupplement(this, _))

  def resolveLocalTypeRef(sftr: SchemaFqnTypeRef): CTypeFqn = {
    val alias = sftr.getFqn.getFqn.first
    val parentNamespace = imports.get(alias) match {
      case Some(ci) => ci.fqn.removeLastSegment()
      case None => Fqn.EMPTY
    }
    new CTypeFqn(this, parentNamespace, sftr)
  }

  class CNamespace(val psi: SchemaNamespaceDecl)(implicit val ctx: CContext) {

    val fqn: Fqn = psi.getFqn2
    // TODO expose custom attributes

  }

  class CImports(@Nullable val psi: SchemaImports)(implicit val ctx: CContext) {
    // explicit imports
    // implicit imports
    // file namespace?
  }

  class CImport(@Nullable val psi: SchemaImportStatement)(implicit val ctx: CContext) {

    val fqn: Fqn = psi.getFqn.getFqn

    val alias: String = fqn.last

  }

}
