// This is a generated file. Not intended for manual editing.
package com.sumologic.epigraph.ideaplugin.schema.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SchemaRecordTypeDef extends SchemaTypeDef {

  @Nullable
  SchemaExtendsDecl getExtendsDecl();

  @Nullable
  SchemaMetaDecl getMetaDecl();

  @Nullable
  SchemaRecordSupplementsDecl getRecordSupplementsDecl();

  @Nullable
  SchemaRecordTypeBody getRecordTypeBody();

  @NotNull
  PsiElement getRecord();

  @Nullable
  PsiElement getId();

}