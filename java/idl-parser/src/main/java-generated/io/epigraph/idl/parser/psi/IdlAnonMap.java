// This is a generated file. Not intended for manual editing.
package io.epigraph.idl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface IdlAnonMap extends IdlTypeRef {

  @Nullable
  IdlTypeRef getTypeRef();

  @Nullable
  IdlValueTypeRef getValueTypeRef();

  @Nullable
  PsiElement getBracketLeft();

  @Nullable
  PsiElement getBracketRight();

  @Nullable
  PsiElement getComma();

  @NotNull
  PsiElement getMap();

}