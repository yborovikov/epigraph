// This is a generated file. Not intended for manual editing.
package com.sumologic.epigraph.ideaPlugin.schema.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SchemaMapTypeDef extends SchemaTypeDef {

  @NotNull
  SchemaAnonMap getAnonMap();

  @Nullable
  SchemaExtendsDecl getExtendsDecl();

  @Nullable
  SchemaMapTypeBody getMapTypeBody();

  @Nullable
  SchemaMetaDecl getMetaDecl();

  @Nullable
  PsiElement getId();

}