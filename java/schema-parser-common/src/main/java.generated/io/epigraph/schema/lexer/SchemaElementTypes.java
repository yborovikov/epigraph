// This is a generated file. Not intended for manual editing.
package io.epigraph.schema.lexer;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import io.epigraph.schema.parser.psi.stubs.SchemaEnumTypeDefStubElementType;
import io.epigraph.schema.parser.psi.stubs.SchemaListTypeDefStubElementType;
import io.epigraph.schema.parser.psi.stubs.SchemaMapTypeDefStubElementType;
import io.epigraph.schema.parser.psi.stubs.SchemaNamespaceDeclStubElementType;
import io.epigraph.schema.parser.psi.stubs.SchemaPrimitiveTypeDefStubElementType;
import io.epigraph.schema.parser.psi.stubs.SchemaRecordTypeDefStubElementType;
import io.epigraph.schema.parser.psi.stubs.SchemaSupplementDefStubElementType;
import io.epigraph.schema.parser.psi.stubs.SchemaTypeDefWrapperStubElementType;
import io.epigraph.schema.parser.psi.stubs.SchemaVarTypeDefStubElementType;
import io.epigraph.schema.parser.psi.impl.*;

public interface SchemaElementTypes {

  IElementType S_ANNOTATION = new SchemaElementType("S_ANNOTATION");
  IElementType S_ANON_LIST = new SchemaElementType("S_ANON_LIST");
  IElementType S_ANON_MAP = new SchemaElementType("S_ANON_MAP");
  IElementType S_DATA = new SchemaElementType("S_DATA");
  IElementType S_DATA_ENTRY = new SchemaElementType("S_DATA_ENTRY");
  IElementType S_DATA_VALUE = new SchemaElementType("S_DATA_VALUE");
  IElementType S_DATUM = new SchemaElementType("S_DATUM");
  IElementType S_DEFAULT_OVERRIDE = new SchemaElementType("S_DEFAULT_OVERRIDE");
  IElementType S_DEFS = new SchemaElementType("S_DEFS");
  IElementType S_ENUM_DATUM = new SchemaElementType("S_ENUM_DATUM");
  IElementType S_ENUM_MEMBER_DECL = new SchemaElementType("S_ENUM_MEMBER_DECL");
  IElementType S_ENUM_TYPE_BODY = new SchemaElementType("S_ENUM_TYPE_BODY");
  IElementType S_ENUM_TYPE_DEF = new SchemaEnumTypeDefStubElementType("S_ENUM_TYPE_DEF");
  IElementType S_EXTENDS_DECL = new SchemaElementType("S_EXTENDS_DECL");
  IElementType S_FIELD_DECL = new SchemaElementType("S_FIELD_DECL");
  IElementType S_IMPORTS = new SchemaElementType("S_IMPORTS");
  IElementType S_IMPORT_STATEMENT = new SchemaElementType("S_IMPORT_STATEMENT");
  IElementType S_LIST_DATUM = new SchemaElementType("S_LIST_DATUM");
  IElementType S_LIST_TYPE_BODY = new SchemaElementType("S_LIST_TYPE_BODY");
  IElementType S_LIST_TYPE_DEF = new SchemaListTypeDefStubElementType("S_LIST_TYPE_DEF");
  IElementType S_MAP_DATUM = new SchemaElementType("S_MAP_DATUM");
  IElementType S_MAP_DATUM_ENTRY = new SchemaElementType("S_MAP_DATUM_ENTRY");
  IElementType S_MAP_TYPE_BODY = new SchemaElementType("S_MAP_TYPE_BODY");
  IElementType S_MAP_TYPE_DEF = new SchemaMapTypeDefStubElementType("S_MAP_TYPE_DEF");
  IElementType S_META_DECL = new SchemaElementType("S_META_DECL");
  IElementType S_NAMESPACE_DECL = new SchemaNamespaceDeclStubElementType("S_NAMESPACE_DECL");
  IElementType S_NULL_DATUM = new SchemaElementType("S_NULL_DATUM");
  IElementType S_PRIMITIVE_DATUM = new SchemaElementType("S_PRIMITIVE_DATUM");
  IElementType S_PRIMITIVE_TYPE_BODY = new SchemaElementType("S_PRIMITIVE_TYPE_BODY");
  IElementType S_PRIMITIVE_TYPE_DEF = new SchemaPrimitiveTypeDefStubElementType("S_PRIMITIVE_TYPE_DEF");
  IElementType S_QID = new SchemaElementType("S_QID");
  IElementType S_QN = new SchemaElementType("S_QN");
  IElementType S_QN_SEGMENT = new SchemaElementType("S_QN_SEGMENT");
  IElementType S_QN_TYPE_REF = new SchemaElementType("S_QN_TYPE_REF");
  IElementType S_RECORD_DATUM = new SchemaElementType("S_RECORD_DATUM");
  IElementType S_RECORD_DATUM_ENTRY = new SchemaElementType("S_RECORD_DATUM_ENTRY");
  IElementType S_RECORD_TYPE_BODY = new SchemaElementType("S_RECORD_TYPE_BODY");
  IElementType S_RECORD_TYPE_DEF = new SchemaRecordTypeDefStubElementType("S_RECORD_TYPE_DEF");
  IElementType S_SUPPLEMENTS_DECL = new SchemaElementType("S_SUPPLEMENTS_DECL");
  IElementType S_SUPPLEMENT_DEF = new SchemaSupplementDefStubElementType("S_SUPPLEMENT_DEF");
  IElementType S_TYPE_DEF_WRAPPER = new SchemaTypeDefWrapperStubElementType("S_TYPE_DEF_WRAPPER");
  IElementType S_TYPE_REF = new SchemaElementType("S_TYPE_REF");
  IElementType S_VALUE_TYPE_REF = new SchemaElementType("S_VALUE_TYPE_REF");
  IElementType S_VAR_TAG_DECL = new SchemaElementType("S_VAR_TAG_DECL");
  IElementType S_VAR_TAG_REF = new SchemaElementType("S_VAR_TAG_REF");
  IElementType S_VAR_TYPE_BODY = new SchemaElementType("S_VAR_TYPE_BODY");
  IElementType S_VAR_TYPE_DEF = new SchemaVarTypeDefStubElementType("S_VAR_TYPE_DEF");

  IElementType S_ABSTRACT = new SchemaElementType("abstract");
  IElementType S_ANGLE_LEFT = new SchemaElementType("<");
  IElementType S_ANGLE_RIGHT = new SchemaElementType(">");
  IElementType S_AT = new SchemaElementType("@");
  IElementType S_BLOCK_COMMENT = new SchemaElementType("block_comment");
  IElementType S_BOOLEAN = new SchemaElementType("boolean");
  IElementType S_BOOLEAN_T = new SchemaElementType("boolean");
  IElementType S_BRACKET_LEFT = new SchemaElementType("[");
  IElementType S_BRACKET_RIGHT = new SchemaElementType("]");
  IElementType S_COLON = new SchemaElementType(":");
  IElementType S_COMMA = new SchemaElementType(",");
  IElementType S_COMMENT = new SchemaElementType("comment");
  IElementType S_CURLY_LEFT = new SchemaElementType("{");
  IElementType S_CURLY_RIGHT = new SchemaElementType("}");
  IElementType S_DEFAULT = new SchemaElementType("default");
  IElementType S_DOT = new SchemaElementType(".");
  IElementType S_DOUBLE_T = new SchemaElementType("double");
  IElementType S_ENUM = new SchemaElementType("enum");
  IElementType S_EQ = new SchemaElementType("=");
  IElementType S_EXTENDS = new SchemaElementType("extends");
  IElementType S_HASH = new SchemaElementType("#");
  IElementType S_ID = new SchemaElementType("id");
  IElementType S_IMPORT = new SchemaElementType("import");
  IElementType S_INTEGER_T = new SchemaElementType("integer");
  IElementType S_LIST = new SchemaElementType("list");
  IElementType S_LONG_T = new SchemaElementType("long");
  IElementType S_MAP = new SchemaElementType("map");
  IElementType S_META = new SchemaElementType("meta");
  IElementType S_NAMESPACE = new SchemaElementType("namespace");
  IElementType S_NODEFAULT = new SchemaElementType("nodefault");
  IElementType S_NULL = new SchemaElementType("null");
  IElementType S_NUMBER = new SchemaElementType("number");
  IElementType S_OVERRIDE = new SchemaElementType("override");
  IElementType S_PAREN_LEFT = new SchemaElementType("(");
  IElementType S_PAREN_RIGHT = new SchemaElementType(")");
  IElementType S_RECORD = new SchemaElementType("record");
  IElementType S_SLASH = new SchemaElementType("/");
  IElementType S_STRING = new SchemaElementType("string");
  IElementType S_STRING_T = new SchemaElementType("string");
  IElementType S_SUPPLEMENT = new SchemaElementType("supplement");
  IElementType S_SUPPLEMENTS = new SchemaElementType("supplements");
  IElementType S_VARTYPE = new SchemaElementType("vartype");
  IElementType S_WITH = new SchemaElementType("with");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
       if (type == S_ANNOTATION) {
        return new SchemaAnnotationImpl(node);
      }
      else if (type == S_ANON_LIST) {
        return new SchemaAnonListImpl(node);
      }
      else if (type == S_ANON_MAP) {
        return new SchemaAnonMapImpl(node);
      }
      else if (type == S_DATA) {
        return new SchemaDataImpl(node);
      }
      else if (type == S_DATA_ENTRY) {
        return new SchemaDataEntryImpl(node);
      }
      else if (type == S_DATA_VALUE) {
        return new SchemaDataValueImpl(node);
      }
      else if (type == S_DATUM) {
        return new SchemaDatumImpl(node);
      }
      else if (type == S_DEFAULT_OVERRIDE) {
        return new SchemaDefaultOverrideImpl(node);
      }
      else if (type == S_DEFS) {
        return new SchemaDefsImpl(node);
      }
      else if (type == S_ENUM_DATUM) {
        return new SchemaEnumDatumImpl(node);
      }
      else if (type == S_ENUM_MEMBER_DECL) {
        return new SchemaEnumMemberDeclImpl(node);
      }
      else if (type == S_ENUM_TYPE_BODY) {
        return new SchemaEnumTypeBodyImpl(node);
      }
      else if (type == S_ENUM_TYPE_DEF) {
        return new SchemaEnumTypeDefImpl(node);
      }
      else if (type == S_EXTENDS_DECL) {
        return new SchemaExtendsDeclImpl(node);
      }
      else if (type == S_FIELD_DECL) {
        return new SchemaFieldDeclImpl(node);
      }
      else if (type == S_IMPORTS) {
        return new SchemaImportsImpl(node);
      }
      else if (type == S_IMPORT_STATEMENT) {
        return new SchemaImportStatementImpl(node);
      }
      else if (type == S_LIST_DATUM) {
        return new SchemaListDatumImpl(node);
      }
      else if (type == S_LIST_TYPE_BODY) {
        return new SchemaListTypeBodyImpl(node);
      }
      else if (type == S_LIST_TYPE_DEF) {
        return new SchemaListTypeDefImpl(node);
      }
      else if (type == S_MAP_DATUM) {
        return new SchemaMapDatumImpl(node);
      }
      else if (type == S_MAP_DATUM_ENTRY) {
        return new SchemaMapDatumEntryImpl(node);
      }
      else if (type == S_MAP_TYPE_BODY) {
        return new SchemaMapTypeBodyImpl(node);
      }
      else if (type == S_MAP_TYPE_DEF) {
        return new SchemaMapTypeDefImpl(node);
      }
      else if (type == S_META_DECL) {
        return new SchemaMetaDeclImpl(node);
      }
      else if (type == S_NAMESPACE_DECL) {
        return new SchemaNamespaceDeclImpl(node);
      }
      else if (type == S_NULL_DATUM) {
        return new SchemaNullDatumImpl(node);
      }
      else if (type == S_PRIMITIVE_DATUM) {
        return new SchemaPrimitiveDatumImpl(node);
      }
      else if (type == S_PRIMITIVE_TYPE_BODY) {
        return new SchemaPrimitiveTypeBodyImpl(node);
      }
      else if (type == S_PRIMITIVE_TYPE_DEF) {
        return new SchemaPrimitiveTypeDefImpl(node);
      }
      else if (type == S_QID) {
        return new SchemaQidImpl(node);
      }
      else if (type == S_QN) {
        return new SchemaQnImpl(node);
      }
      else if (type == S_QN_SEGMENT) {
        return new SchemaQnSegmentImpl(node);
      }
      else if (type == S_QN_TYPE_REF) {
        return new SchemaQnTypeRefImpl(node);
      }
      else if (type == S_RECORD_DATUM) {
        return new SchemaRecordDatumImpl(node);
      }
      else if (type == S_RECORD_DATUM_ENTRY) {
        return new SchemaRecordDatumEntryImpl(node);
      }
      else if (type == S_RECORD_TYPE_BODY) {
        return new SchemaRecordTypeBodyImpl(node);
      }
      else if (type == S_RECORD_TYPE_DEF) {
        return new SchemaRecordTypeDefImpl(node);
      }
      else if (type == S_SUPPLEMENTS_DECL) {
        return new SchemaSupplementsDeclImpl(node);
      }
      else if (type == S_SUPPLEMENT_DEF) {
        return new SchemaSupplementDefImpl(node);
      }
      else if (type == S_TYPE_DEF_WRAPPER) {
        return new SchemaTypeDefWrapperImpl(node);
      }
      else if (type == S_TYPE_REF) {
        return new SchemaTypeRefImpl(node);
      }
      else if (type == S_VALUE_TYPE_REF) {
        return new SchemaValueTypeRefImpl(node);
      }
      else if (type == S_VAR_TAG_DECL) {
        return new SchemaVarTagDeclImpl(node);
      }
      else if (type == S_VAR_TAG_REF) {
        return new SchemaVarTagRefImpl(node);
      }
      else if (type == S_VAR_TYPE_BODY) {
        return new SchemaVarTypeBodyImpl(node);
      }
      else if (type == S_VAR_TYPE_DEF) {
        return new SchemaVarTypeDefImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}