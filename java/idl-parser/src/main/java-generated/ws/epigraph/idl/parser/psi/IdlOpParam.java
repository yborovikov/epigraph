// This is a generated file. Not intended for manual editing.
package ws.epigraph.idl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface IdlOpParam extends PsiElement {

  @NotNull
  List<IdlAnnotation> getAnnotationList();

  @Nullable
  IdlDatum getDatum();

  @Nullable
  IdlOpInputModelProjection getOpInputModelProjection();

  @NotNull
  List<IdlOpParam> getOpParamList();

  @Nullable
  IdlQid getQid();

  @Nullable
  IdlTypeRef getTypeRef();

  @Nullable
  PsiElement getColon();

  @Nullable
  PsiElement getCurlyLeft();

  @Nullable
  PsiElement getCurlyRight();

  @Nullable
  PsiElement getEq();

  @Nullable
  PsiElement getPlus();

  @NotNull
  PsiElement getSemicolon();

}