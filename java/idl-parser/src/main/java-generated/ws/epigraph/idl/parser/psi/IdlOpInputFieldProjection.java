// This is a generated file. Not intended for manual editing.
package ws.epigraph.idl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface IdlOpInputFieldProjection extends PsiElement {

  @NotNull
  List<IdlOpInputFieldProjectionBodyPart> getOpInputFieldProjectionBodyPartList();

  @NotNull
  IdlOpInputVarProjection getOpInputVarProjection();

  @Nullable
  PsiElement getCurlyLeft();

  @Nullable
  PsiElement getCurlyRight();

}
