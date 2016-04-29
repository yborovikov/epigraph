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

public class SchemaPrimitiveTypeDefImpl extends SchemaTypeDefImpl implements SchemaPrimitiveTypeDef {

  public SchemaPrimitiveTypeDefImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitPrimitiveTypeDef(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SchemaVisitor) accept((SchemaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public SchemaExtendsDecl getExtendsDecl() {
    return findChildByClass(SchemaExtendsDecl.class);
  }

  @Override
  @Nullable
  public SchemaMetaDecl getMetaDecl() {
    return findChildByClass(SchemaMetaDecl.class);
  }

  @Override
  @NotNull
  public SchemaPrimitiveKind getPrimitiveKind() {
    return findNotNullChildByClass(SchemaPrimitiveKind.class);
  }

  @Override
  @Nullable
  public SchemaPrimitiveTypeBody getPrimitiveTypeBody() {
    return findChildByClass(SchemaPrimitiveTypeBody.class);
  }

  @Override
  @Nullable
  public PsiElement getId() {
    return findChildByType(S_ID);
  }

}