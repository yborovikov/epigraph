// This is a generated file. Not intended for manual editing.
package io.epigraph.url.parser.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static io.epigraph.url.lexer.UrlElementTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import io.epigraph.url.parser.psi.*;
import io.epigraph.lang.Qn;

public class UrlQnImpl extends ASTWrapperPsiElement implements UrlQn {

  public UrlQnImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull UrlVisitor visitor) {
    visitor.visitQn(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof UrlVisitor) accept((UrlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<UrlQnSegment> getQnSegmentList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, UrlQnSegment.class);
  }

  @NotNull
  public Qn getQn() {
    return UrlPsiImplUtil.getQn(this);
  }

}