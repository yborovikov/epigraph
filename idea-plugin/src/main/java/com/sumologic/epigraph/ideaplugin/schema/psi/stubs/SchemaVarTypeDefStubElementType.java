package com.sumologic.epigraph.ideaplugin.schema.psi.stubs;

import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaVarTypeDef;
import com.sumologic.epigraph.ideaplugin.schema.psi.impl.SchemaVarTypeDefImpl;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaVarTypeDefStubElementType extends SchemaTypeDefStubElementTypeBase<SchemaVarTypeDefStub, SchemaVarTypeDef> {
  public SchemaVarTypeDefStubElementType(@NotNull @NonNls String debugName) {
    super(debugName, "vartypedef");
  }

  @Override
  public SchemaVarTypeDef createPsi(@NotNull SchemaVarTypeDefStub stub) {
    return new SchemaVarTypeDefImpl(stub, this);
  }

  @Override
  public SchemaVarTypeDefStub createStub(@NotNull SchemaVarTypeDef typeDef, StubElement parentStub) {
    return new SchemaVarTypeDefStubImpl(
        parentStub,
        typeDef.getName(),
        typeDef.getNamespace(),
        getSerializedExtendsTypeRefs(typeDef));
  }

  @NotNull
  @Override
  protected SchemaVarTypeDefStub deserialize(
      @NotNull StubInputStream dataStream,
      StubElement parentStub,
      String name, String namespace,
      @Nullable final List<SerializedFqnTypeRef> extendsTypeRefs) throws IOException {
    return new SchemaVarTypeDefStubImpl(parentStub, name, namespace, extendsTypeRefs);
  }
}