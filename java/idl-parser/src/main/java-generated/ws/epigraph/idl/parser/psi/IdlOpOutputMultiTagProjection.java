// This is a generated file. Not intended for manual editing.
package ws.epigraph.idl.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface IdlOpOutputMultiTagProjection extends PsiElement {

  @NotNull
  List<IdlOpOutputMultiTagProjectionItem> getOpOutputMultiTagProjectionItemList();

  @NotNull
  PsiElement getColon();

  @NotNull
  PsiElement getParenLeft();

  @Nullable
  PsiElement getParenRight();

}