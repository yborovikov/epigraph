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

package ws.epigraph.java.service.projections.req

import ws.epigraph.java.JavaGenUtils
import ws.epigraph.java.NewlineStringInterpolator.NewlineHelper
import ws.epigraph.lang.Qn
import ws.epigraph.projections.gen.GenMapModelProjection
import ws.epigraph.projections.op.AbstractOpModelProjection
import ws.epigraph.types.DatumTypeApi

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
trait ReqMapModelProjectionGen extends ReqModelProjectionGen {
  override type OpProjectionType <: AbstractOpModelProjection[_, _, _ <: DatumTypeApi] with GenMapModelProjection[_, _, _, _, _ <: DatumTypeApi]

  protected val elementsNamespaceSuffix = "elements"

  protected def keyGen: ReqMapKeyProjectionGen

  protected def elementGen: ReqProjectionGen

  // -------

  override def children: Iterable[ReqProjectionGen] = super.children ++ Iterable(keyGen, elementGen)

  protected def generate(reqMapModelProjectionFqn: Qn, extra: CodeChunk): String = {
    val keyProjectionClass = keyGen.shortClassName
    val elementProjectionClass = elementGen.shortClassName

    val imports: Set[String] = Set(
      "org.jetbrains.annotations.NotNull",
      "org.jetbrains.annotations.Nullable",
      "java.util.List",
      "java.util.stream.Collectors",
      reqVarProjectionFqn.toString,
      reqModelProjectionQn.toString,
      reqMapModelProjectionFqn.toString,
      elementGen.fullClassName,
      keyGen.fullClassName
    ) ++ params.imports ++ meta.imports ++ extra.imports

    /*@formatter:off*/sn"""\
${JavaGenUtils.topLevelComment}
$packageStatement

${ReqProjectionGen.generateImports(imports)}

$classJavadoc\
public class $shortClassName {
  private final @NotNull ${reqMapModelProjectionFqn.last()} raw;

  public $shortClassName(@NotNull ${reqModelProjectionQn.last()}$reqModelProjectionParams raw) {
    this.raw = (${reqMapModelProjectionFqn.last()}) raw;
  }

  public $shortClassName(@NotNull ${reqVarProjectionFqn.last()} selfVar) {
    this(selfVar.singleTagProjection().projection());
  }

${extra.code}
  /**
   * @return key projections
   */
  public @Nullable List<$keyProjectionClass> keys() {
    return raw.keys() == null ? null : raw.keys().stream().map(key -> new $keyProjectionClass(key)).collect(Collectors.toList());
  }

  /**
   * @return items projection
   */
  public @NotNull $elementProjectionClass itemsProjection() {
    return new $elementProjectionClass(raw.itemsProjection());
  }
${params.code}\
${meta.code}\

  public @NotNull ${reqMapModelProjectionFqn.last()} _raw() { return raw; }
}"""/*@formatter:on*/
  }
}