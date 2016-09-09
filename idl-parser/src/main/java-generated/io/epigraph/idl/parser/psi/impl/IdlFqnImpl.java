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
import io.epigraph.lang.Fqn;

public class IdlFqnImpl extends ASTWrapperPsiElement implements IdlFqn {

  public IdlFqnImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull IdlVisitor visitor) {
    visitor.visitFqn(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof IdlVisitor) accept((IdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<IdlFqnSegment> getFqnSegmentList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, IdlFqnSegment.class);
  }

  @NotNull
  public Fqn getFqn() {
    return IdlPsiImplUtil.getFqn(this);
  }

}
