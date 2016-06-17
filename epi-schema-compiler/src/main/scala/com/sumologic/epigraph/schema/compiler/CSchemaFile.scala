/* Created by yegor on 6/9/16. */

package com.sumologic.epigraph.schema.compiler

import java.util.concurrent.ConcurrentLinkedQueue

import com.intellij.psi.PsiElement
import com.sumologic.epigraph.schema.parser.Fqn
import com.sumologic.epigraph.schema.parser.psi._
import net.jcip.annotations.ThreadSafe
import org.jetbrains.annotations.Nullable

import scala.collection.JavaConversions._

class CSchemaFile(val psi: SchemaFile)(implicit val ctx: CContext) {

  val filename: String = psi.getName

  val lnu: LineNumberUtil = new LineNumberUtil(psi.getText, ctx.tabWidth) // TODO also get tab width from file itself?

  @ThreadSafe
  val typerefs: ConcurrentLinkedQueue[CTypeRef] = new java.util.concurrent.ConcurrentLinkedQueue

  val namespace: CNamespace = new CNamespace(psi.getNamespaceDecl)

  val imports: Map[String, CImport] = psi.getImportStatements.map(new CImport(_)).map { ci =>
    (ci.alias, ci)
  }(collection.breakOut) // TODO deal with dupes (foo.Baz and bar.Baz)

  val importedAliases: Map[String, Fqn] = ctx.implicitImports ++ imports.map { case (alias, ci) => (alias, ci.fqn) }

  @Nullable
  private val defs: SchemaDefs = psi.getDefs

  val types: Seq[CTypeDef] = if (defs == null) Nil else defs.getTypeDefWrapperList.map(CTypeDef.apply(this, _))

  val supplements: Seq[CSupplement] = if (defs == null) Nil else defs.getSupplementDefList.map(new CSupplement(this, _))

  def qualifyLocalTypeRef(sftr: SchemaFqnTypeRef): CTypeFqn = {
    val alias = sftr.getFqn.getFqn.first
    val parentNamespace = importedAliases.get(alias) match {
      case Some(fqn) => fqn.removeLastSegment() // typeref starting with imported alias
      case None => sftr.getFqn.getFqn.size match {
        case 1 => namespace.fqn // single-segment typeref to a type in schema document namespace
        case _ => Fqn.EMPTY // fully qualified typeref
      }
    }
    new CTypeFqn(this, parentNamespace, sftr)
  }

  def position(psi: PsiElement): CErrorPosition = {
    // TODO check element is ours?
    lnu.pos(psi)
  }

  def location(psi: PsiElement): String = {
    val cep = position(psi)
    filename + ":" + cep.line + ":" + cep.column
  }

}

class CNamespace(val psi: SchemaNamespaceDecl)(implicit val ctx: CContext) {

  val fqn: Fqn = psi.getFqn2
  // TODO expose custom attributes

}

class CImport(@Nullable val psi: SchemaImportStatement)(implicit val ctx: CContext) {

  val fqn: Fqn = psi.getFqn.getFqn

  val alias: String = fqn.last

}

