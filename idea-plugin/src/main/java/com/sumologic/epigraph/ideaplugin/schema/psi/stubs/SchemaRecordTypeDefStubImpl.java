package com.sumologic.epigraph.ideaplugin.schema.psi.stubs;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import com.sumologic.epigraph.ideaplugin.schema.lexer.SchemaElementTypes;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaRecordTypeDef;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaRecordTypeDefStubImpl extends SchemaTypeDefStubBaseImpl<SchemaRecordTypeDef> implements SchemaRecordTypeDefStub {
  SchemaRecordTypeDefStubImpl(StubElement parent,
                              String name,
                              String namespace,
                              @Nullable final List<SerializedFqnTypeRef> extendsTypeRefs) {
    super(parent, (IStubElementType) SchemaElementTypes.S_RECORD_TYPE_DEF, name, namespace, extendsTypeRefs);
  }
}