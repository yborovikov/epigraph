// This is a generated file. Not intended for manual editing.
package ws.epigraph.schema.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SchemaOpOutputFieldProjection extends PsiElement {

  @NotNull
  List<SchemaOpOutputFieldProjectionBodyPart> getOpOutputFieldProjectionBodyPartList();

  @NotNull
  SchemaOpOutputVarProjection getOpOutputVarProjection();

  @Nullable
  PsiElement getCurlyLeft();

  @Nullable
  PsiElement getCurlyRight();

}