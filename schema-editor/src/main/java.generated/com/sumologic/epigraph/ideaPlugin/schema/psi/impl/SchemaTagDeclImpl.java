// This is a generated file. Not intended for manual editing.
package com.sumologic.epigraph.ideaPlugin.schema.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.sumologic.epigraph.ideaPlugin.schema.lexer.SchemaElementTypes.*;
import com.sumologic.epigraph.ideaPlugin.schema.psi.*;

public class SchemaTagDeclImpl extends CustomParamHolderImpl implements SchemaTagDecl {

  public SchemaTagDeclImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitTagDecl(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SchemaVisitor) accept((SchemaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<SchemaCustomParam> getCustomParamList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, SchemaCustomParam.class);
  }

  @Override
  @Nullable
  public SchemaDefaultOverride getDefaultOverride() {
    return findChildByClass(SchemaDefaultOverride.class);
  }

  @Override
  @Nullable
  public SchemaTypeRef getTypeRef() {
    return findChildByClass(SchemaTypeRef.class);
  }

  @Override
  @NotNull
  public PsiElement getColon() {
    return findNotNullChildByType(S_COLON);
  }

  @Override
  @Nullable
  public PsiElement getCurlyLeft() {
    return findChildByType(S_CURLY_LEFT);
  }

  @Override
  @Nullable
  public PsiElement getCurlyRight() {
    return findChildByType(S_CURLY_RIGHT);
  }

  @Override
  @NotNull
  public PsiElement getId() {
    return findNotNullChildByType(S_ID);
  }

}