// This is a generated file. Not intended for manual editing.
package io.epigraph.idl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface IdlOpOutputMapModelProjection extends IdlOpOutputModelProjection {

  @NotNull
  IdlOpOutputKeyProjection getOpOutputKeyProjection();

  @Nullable
  IdlOpOutputVarProjection getOpOutputVarProjection();

  @Nullable
  PsiElement getStar();

}
