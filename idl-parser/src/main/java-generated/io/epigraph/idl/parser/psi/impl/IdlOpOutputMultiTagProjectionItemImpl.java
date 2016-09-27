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

public class IdlOpOutputMultiTagProjectionItemImpl extends ASTWrapperPsiElement implements IdlOpOutputMultiTagProjectionItem {

  public IdlOpOutputMultiTagProjectionItemImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull IdlVisitor visitor) {
    visitor.visitOpOutputMultiTagProjectionItem(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof IdlVisitor) accept((IdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public IdlOpOutputModelProjection getOpOutputModelProjection() {
    return findChildByClass(IdlOpOutputModelProjection.class);
  }

  @Override
  @NotNull
  public List<IdlOpOutputModelProperty> getOpOutputModelPropertyList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, IdlOpOutputModelProperty.class);
  }

  @Override
  @NotNull
  public IdlTagName getTagName() {
    return findNotNullChildByClass(IdlTagName.class);
  }

  @Override
  @Nullable
  public PsiElement getCurlyLeft() {
    return findChildByType(I_CURLY_LEFT);
  }

  @Override
  @Nullable
  public PsiElement getCurlyRight() {
    return findChildByType(I_CURLY_RIGHT);
  }

  @Override
  @Nullable
  public PsiElement getPlus() {
    return findChildByType(I_PLUS);
  }

}
