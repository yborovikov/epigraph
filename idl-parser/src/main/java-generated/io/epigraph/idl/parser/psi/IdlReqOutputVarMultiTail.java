// This is a generated file. Not intended for manual editing.
package io.epigraph.idl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface IdlReqOutputVarMultiTail extends PsiElement {

  @NotNull
  List<IdlReqOutputVarMultiTailItem> getReqOutputVarMultiTailItemList();

  @NotNull
  PsiElement getParenLeft();

  @Nullable
  PsiElement getParenRight();

  @NotNull
  PsiElement getTilda();

}