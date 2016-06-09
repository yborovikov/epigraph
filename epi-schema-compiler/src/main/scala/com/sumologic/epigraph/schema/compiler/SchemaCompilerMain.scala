/* Created by yegor on 6/8/16. */

package com.sumologic.epigraph.schema.compiler

import java.io.File

import com.intellij.psi.impl.DebugUtil
import com.sumologic.epigraph.schema.parser.{Fqn, SchemaParserDefinition}
import com.sumologic.epigraph.schema.parser.psi.SchemaFile
import org.intellij.grammar.LightPsi

import scala.collection.mutable

object SchemaCompilerMain {

  val paths: Seq[String] = Seq(
    "epi-builtin-types/src/main/schema/epigraph/builtinTypes.es",
    "epi-schema/src/main/schema/epigraph/schema/names.es",
    "epi-schema/src/main/schema/epigraph/schema/types.es"
  )

  val spd: SchemaParserDefinition = new SchemaParserDefinition

  val supplements = mutable.LinearSeq()

  val types = mutable.Map[Fqn, Nothing]()


  def main(args: Array[String]) {
    val files: Seq[File] = paths.map(new File(_))
    val schemaFiles: Seq[SchemaFile] = files.map { file =>
      LightPsi.parseFile(file, spd) match {
        case sf: SchemaFile => sf
        case _ => throw new RuntimeException(file.getPath)
      }
    }
    schemaFiles foreach { sf =>
      println(sf.getName + ":")
      println(DebugUtil.psiToString(sf, true, true).trim())
      sf.getNamespaceDecl
    }

  }

}
