// This is a generated file. Not intended for manual editing.
package com.sumologic.epigraph.ideaPlugin.schema.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.PsiReference;

public interface SchemaFqnSegment extends PsiNameIdentifierOwner {

  @NotNull
  PsiElement getId();

  @Nullable
  String getName();

  PsiElement setName(String name);

  @Nullable
  PsiElement getNameIdentifier();

  @Nullable
  SchemaFqnTypeRef getFqnTypeRef();

  boolean isLast();

  @Nullable
  PsiReference getReference();

}