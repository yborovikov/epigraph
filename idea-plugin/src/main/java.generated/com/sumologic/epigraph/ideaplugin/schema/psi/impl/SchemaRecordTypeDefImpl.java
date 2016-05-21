// This is a generated file. Not intended for manual editing.
package com.sumologic.epigraph.ideaplugin.schema.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.sumologic.epigraph.ideaplugin.schema.lexer.SchemaElementTypes.*;
import com.sumologic.epigraph.ideaplugin.schema.psi.*;
import com.intellij.psi.stubs.IStubElementType;

public class SchemaRecordTypeDefImpl extends SchemaRecordTypeDefElementImplBase implements SchemaRecordTypeDef {

  public SchemaRecordTypeDefImpl(ASTNode node) {
    super(node);
  }

  public SchemaRecordTypeDefImpl(com.sumologic.epigraph.ideaplugin.schema.psi.stubs.SchemaRecordTypeDefStub stub, IStubElementType nodeType) {
    super(stub, nodeType);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitRecordTypeDef(this);
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
  @Nullable
  public SchemaRecordSupplementsDecl getRecordSupplementsDecl() {
    return findChildByClass(SchemaRecordSupplementsDecl.class);
  }

  @Override
  @Nullable
  public SchemaRecordTypeBody getRecordTypeBody() {
    return findChildByClass(SchemaRecordTypeBody.class);
  }

  @Override
  @Nullable
  public PsiElement getAbstract() {
    return findChildByType(S_ABSTRACT);
  }

  @Override
  @Nullable
  public PsiElement getPolymorphic() {
    return findChildByType(S_POLYMORPHIC);
  }

  @Override
  @NotNull
  public PsiElement getRecord() {
    return findNotNullChildByType(S_RECORD);
  }

  @Override
  @Nullable
  public PsiElement getId() {
    return findChildByType(S_ID);
  }

}
