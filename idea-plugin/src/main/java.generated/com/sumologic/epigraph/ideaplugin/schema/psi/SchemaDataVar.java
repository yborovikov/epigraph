// This is a generated file. Not intended for manual editing.
package com.sumologic.epigraph.ideaplugin.schema.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SchemaDataVar extends SchemaDataValue {

  @NotNull
  List<SchemaDataVarEntry> getDataVarEntryList();

  @NotNull
  PsiElement getAngleLeft();

  @Nullable
  PsiElement getAngleRight();

}