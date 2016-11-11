// This is a generated file. Not intended for manual editing.
package ws.epigraph.idl.parser.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static ws.epigraph.idl.lexer.IdlElementTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import ws.epigraph.idl.parser.psi.*;

public class IdlOperationDeleteProjectionImpl extends ASTWrapperPsiElement implements IdlOperationDeleteProjection {

  public IdlOperationDeleteProjectionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull IdlVisitor visitor) {
    visitor.visitOperationDeleteProjection(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof IdlVisitor) accept((IdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public IdlOpDeleteFieldProjection getOpDeleteFieldProjection() {
    return findChildByClass(IdlOpDeleteFieldProjection.class);
  }

  @Override
  @NotNull
  public PsiElement getDeleteProjection() {
    return findNotNullChildByType(I_DELETE_PROJECTION);
  }

}
