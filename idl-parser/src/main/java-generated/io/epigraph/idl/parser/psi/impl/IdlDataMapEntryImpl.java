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

public class IdlDataMapEntryImpl extends ASTWrapperPsiElement implements IdlDataMapEntry {

  public IdlDataMapEntryImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull IdlVisitor visitor) {
    visitor.visitDataMapEntry(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof IdlVisitor) accept((IdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public IdlDataPrimitive getDataPrimitive() {
    return findChildByClass(IdlDataPrimitive.class);
  }

  @Override
  @NotNull
  public List<IdlDataValue> getDataValueList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, IdlDataValue.class);
  }

  @Override
  @NotNull
  public List<IdlFqnTypeRef> getFqnTypeRefList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, IdlFqnTypeRef.class);
  }

  @Override
  @NotNull
  public PsiElement getColon() {
    return findNotNullChildByType(I_COLON);
  }

  @Override
  @Nullable
  public PsiElement getComma() {
    return findChildByType(I_COMMA);
  }

  @Override
  @Nullable
  public PsiElement getNull() {
    return findChildByType(I_NULL);
  }

}