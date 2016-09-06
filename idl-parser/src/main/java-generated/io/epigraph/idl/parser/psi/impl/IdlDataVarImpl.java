// This is a generated file. Not intended for manual editing.
package io.epigraph.idl.parser.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static io.epigraph.idl.lexer.IdlElementTypes.*;
import io.epigraph.idl.parser.psi.*;

public class IdlDataVarImpl extends IdlDataValueImpl implements IdlDataVar {

  public IdlDataVarImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull IdlVisitor visitor) {
    visitor.visitDataVar(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof IdlVisitor) accept((IdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<IdlDataVarEntry> getDataVarEntryList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, IdlDataVarEntry.class);
  }

  @Override
  @NotNull
  public PsiElement getAngleLeft() {
    return findNotNullChildByType(I_ANGLE_LEFT);
  }

  @Override
  @Nullable
  public PsiElement getAngleRight() {
    return findChildByType(I_ANGLE_RIGHT);
  }

}
