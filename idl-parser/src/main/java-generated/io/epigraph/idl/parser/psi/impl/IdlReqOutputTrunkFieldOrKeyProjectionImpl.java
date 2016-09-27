// This is a generated file. Not intended for manual editing.
package io.epigraph.idl.parser.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static io.epigraph.idl.lexer.IdlElementTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import io.epigraph.idl.parser.psi.*;

public class IdlReqOutputTrunkFieldOrKeyProjectionImpl extends ASTWrapperPsiElement implements IdlReqOutputTrunkFieldOrKeyProjection {

  public IdlReqOutputTrunkFieldOrKeyProjectionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull IdlVisitor visitor) {
    visitor.visitReqOutputTrunkFieldOrKeyProjection(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof IdlVisitor) accept((IdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public IdlDatum getDatum() {
    return findNotNullChildByClass(IdlDatum.class);
  }

  @Override
  @NotNull
  public List<IdlReqAnnotation> getReqAnnotationList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, IdlReqAnnotation.class);
  }

  @Override
  @NotNull
  public IdlReqOutputTrunkVarProjection getReqOutputTrunkVarProjection() {
    return findNotNullChildByClass(IdlReqOutputTrunkVarProjection.class);
  }

  @Override
  @NotNull
  public List<IdlReqParam> getReqParamList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, IdlReqParam.class);
  }

  @Override
  @Nullable
  public PsiElement getPlus() {
    return findChildByType(I_PLUS);
  }

}
