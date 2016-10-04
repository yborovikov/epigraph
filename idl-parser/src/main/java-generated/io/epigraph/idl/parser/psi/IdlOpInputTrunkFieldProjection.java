// This is a generated file. Not intended for manual editing.
package io.epigraph.idl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface IdlOpInputTrunkFieldProjection extends PsiElement {

  @NotNull
  List<IdlOpInputFieldProjectionBodyPart> getOpInputFieldProjectionBodyPartList();

  @Nullable
  IdlOpInputTrunkVarProjection getOpInputTrunkVarProjection();

  @NotNull
  IdlQid getQid();

  @Nullable
  PsiElement getCurlyLeft();

  @Nullable
  PsiElement getCurlyRight();

  @Nullable
  PsiElement getPlus();

}