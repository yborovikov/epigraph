// This is a generated file. Not intended for manual editing.
package io.epigraph.idl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface IdlReqOutputComaKeyProjection extends PsiElement {

  @NotNull
  IdlDatum getDatum();

  @NotNull
  List<IdlReqAnnotation> getReqAnnotationList();

  @NotNull
  List<IdlReqParam> getReqParamList();

}
