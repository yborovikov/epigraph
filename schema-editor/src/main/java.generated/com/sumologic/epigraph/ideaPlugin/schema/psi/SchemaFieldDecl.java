// This is a generated file. Not intended for manual editing.
package com.sumologic.epigraph.ideaPlugin.schema.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SchemaFieldDecl extends CustomParamsHolder {

  @NotNull
  List<SchemaCustomParam> getCustomParamList();

  @Nullable
  SchemaDefaultOverride getDefaultOverride();

  @Nullable
  SchemaTypeRef getTypeRef();

  @NotNull
  PsiElement getColon();

  @Nullable
  PsiElement getCurlyLeft();

  @Nullable
  PsiElement getCurlyRight();

  @NotNull
  PsiElement getId();

}