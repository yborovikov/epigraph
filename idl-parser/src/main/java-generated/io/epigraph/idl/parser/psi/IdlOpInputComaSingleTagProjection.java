// This is a generated file. Not intended for manual editing.
package io.epigraph.idl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface IdlOpInputComaSingleTagProjection extends PsiElement {

  @Nullable
  IdlOpInputComaModelProjection getOpInputComaModelProjection();

  @NotNull
  List<IdlOpInputModelProperty> getOpInputModelPropertyList();

  @Nullable
  IdlTagName getTagName();

  @Nullable
  PsiElement getColon();

  @Nullable
  PsiElement getCurlyLeft();

  @Nullable
  PsiElement getCurlyRight();

  @Nullable
  PsiElement getPlus();

}
