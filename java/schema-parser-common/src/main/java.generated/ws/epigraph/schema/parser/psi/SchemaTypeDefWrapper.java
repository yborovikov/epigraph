// This is a generated file. Not intended for manual editing.
package ws.epigraph.schema.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import ws.epigraph.schema.parser.psi.stubs.SchemaTypeDefWrapperStub;

public interface SchemaTypeDefWrapper extends PsiElement, StubBasedPsiElement<SchemaTypeDefWrapperStub> {

  @Nullable
  SchemaEnumTypeDef getEnumTypeDef();

  @Nullable
  SchemaListTypeDef getListTypeDef();

  @Nullable
  SchemaMapTypeDef getMapTypeDef();

  @Nullable
  SchemaPrimitiveTypeDef getPrimitiveTypeDef();

  @Nullable
  SchemaRecordTypeDef getRecordTypeDef();

  @Nullable
  SchemaVarTypeDef getVarTypeDef();

  @NotNull
  SchemaTypeDef getElement();

  void delete();

  @NotNull
  String toString();

}