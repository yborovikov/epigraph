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

public class IdlOpParamImpl extends ASTWrapperPsiElement implements IdlOpParam {

  public IdlOpParamImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull IdlVisitor visitor) {
    visitor.visitOpParam(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof IdlVisitor) accept((IdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public IdlFqnTypeRef getFqnTypeRef() {
    return findNotNullChildByClass(IdlFqnTypeRef.class);
  }

  @Override
  @NotNull
  public IdlOpInputModelProjection getOpInputModelProjection() {
    return findNotNullChildByClass(IdlOpInputModelProjection.class);
  }

  @Override
  @NotNull
  public IdlQid getQid() {
    return findNotNullChildByClass(IdlQid.class);
  }

  @Override
  @Nullable
  public IdlVarValue getVarValue() {
    return findChildByClass(IdlVarValue.class);
  }

  @Override
  @NotNull
  public PsiElement getColon() {
    return findNotNullChildByType(I_COLON);
  }

  @Override
  @Nullable
  public PsiElement getEq() {
    return findChildByType(I_EQ);
  }

  @Override
  @Nullable
  public PsiElement getPlus() {
    return findChildByType(I_PLUS);
  }

  @Override
  @NotNull
  public PsiElement getSemicolon() {
    return findNotNullChildByType(I_SEMICOLON);
  }

}
