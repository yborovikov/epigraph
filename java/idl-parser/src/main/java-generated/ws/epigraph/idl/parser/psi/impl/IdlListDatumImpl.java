// This is a generated file. Not intended for manual editing.
package ws.epigraph.idl.parser.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static ws.epigraph.idl.lexer.IdlElementTypes.*;
import ws.epigraph.idl.parser.psi.*;

public class IdlListDatumImpl extends IdlDatumImpl implements IdlListDatum {

  public IdlListDatumImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull IdlVisitor visitor) {
    visitor.visitListDatum(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof IdlVisitor) accept((IdlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<IdlDataValue> getDataValueList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, IdlDataValue.class);
  }

  @Override
  @Nullable
  public IdlTypeRef getTypeRef() {
    return findChildByClass(IdlTypeRef.class);
  }

  @Override
  @NotNull
  public PsiElement getBracketLeft() {
    return findNotNullChildByType(I_BRACKET_LEFT);
  }

  @Override
  @Nullable
  public PsiElement getBracketRight() {
    return findChildByType(I_BRACKET_RIGHT);
  }

}