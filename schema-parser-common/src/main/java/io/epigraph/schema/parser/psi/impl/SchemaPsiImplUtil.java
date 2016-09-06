package io.epigraph.schema.parser.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.TokenType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import io.epigraph.lang.Fqn;
import io.epigraph.lang.NamingConventions;
import io.epigraph.schema.parser.psi.*;
import io.epigraph.schema.parser.psi.stubs.SchemaNamespaceDeclStub;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static io.epigraph.schema.lexer.SchemaElementTypes.S_WITH;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaPsiImplUtil {
//  private static final SchemaTypeDef[] EMPTY_TYPE_DEFS = new SchemaTypeDef[0];

  // namespace --------------------------------------------

  @Contract(pure = true)
  @Nullable
  public static Fqn getFqn2(SchemaNamespaceDecl namespaceDecl) {
    SchemaNamespaceDeclStub stub = namespaceDecl.getStub();
    if (stub != null) return stub.getFqn();

    SchemaFqn schemaFqn = namespaceDecl.getFqn();
    return schemaFqn == null ? null : getFqn(schemaFqn);
  }

  // qid --------------------------------------------

  @Contract(pure = true)
  @NotNull
  public static PsiElement setName(SchemaQid qid, String name) {
    PsiElement oldId = qid.getId();
    PsiElement newId = SchemaElementFactory.createId(qid.getProject(), name);
    oldId.replace(newId);
    return qid;
  }

  @Contract(pure = true)
  @NotNull
  public static String getName(SchemaQid qid) {
    return qid.getId().getText();
  }

  @Contract(pure = true)
  @NotNull
  public static String getCanonicalName(SchemaQid qid) {
    String name = getName(qid);
    return NamingConventions.unquote(name);
  }

  // fqn --------------------------------------------

  @Contract(pure = true)
  @NotNull
  public static Fqn getFqn(SchemaFqn e) {
    List<SchemaFqnSegment> fqnSegmentList = e.getFqnSegmentList();
    String[] segments = new String[fqnSegmentList.size()];
    int idx = 0;

    for (SchemaFqnSegment segment : fqnSegmentList) {
      segments[idx++] = segment.getQid().getCanonicalName();
    }

    return new Fqn(segments);
  }

  // typedef wrapper --------------------------------------------

  public static void delete(@NotNull SchemaTypeDefWrapper schemaTypeDef) throws IncorrectOperationException {
    final ASTNode parentNode = schemaTypeDef.getParent().getNode();
    assert parentNode != null;

    ASTNode node = schemaTypeDef.getNode();
    ASTNode prev = node.getTreePrev();
    ASTNode next = node.getTreeNext();
    parentNode.removeChild(node);
    if ((prev == null || prev.getElementType() == TokenType.WHITE_SPACE) && next != null &&
        next.getElementType() == TokenType.WHITE_SPACE) {
      parentNode.removeChild(next);
    }
  }

  @Contract(pure = true)
  @NotNull
  public static SchemaTypeDef getElement(SchemaTypeDefWrapper typeDef) {
    SchemaTypeDef e = typeDef.getVarTypeDef();
    if (e != null) return e;
    e = typeDef.getRecordTypeDef();
    if (e != null) return e;
    e = typeDef.getMapTypeDef();
    if (e != null) return e;
    e = typeDef.getListTypeDef();
    if (e != null) return e;
    e = typeDef.getEnumTypeDef();
    if (e != null) return e;
    e = typeDef.getPrimitiveTypeDef();
    if (e != null) return e;

    throw new IllegalStateException("Unknown type def: " + typeDef);
  }

  // record --------------------------------------------

  @Contract(pure = true)
  @NotNull
  public static List<SchemaTypeDef> supplemented(@NotNull SchemaRecordTypeDef recordTypeDef) {
    return SchemaPsiImplUtilExt.supplemented(recordTypeDef);
  }

  // primitive --------------------------------------------
  @Contract(pure = true)
  @NotNull
  public static PrimitiveTypeKind getPrimitiveTypeKind(@NotNull SchemaPrimitiveTypeDef primitiveTypeDef) {
    if (primitiveTypeDef.getStringT() != null) return PrimitiveTypeKind.STRING;
    if (primitiveTypeDef.getLongT() != null) return PrimitiveTypeKind.LONG;
    if (primitiveTypeDef.getIntegerT() != null) return PrimitiveTypeKind.INTEGER;
    if (primitiveTypeDef.getBooleanT() != null) return PrimitiveTypeKind.BOOLEAN;
    if (primitiveTypeDef.getDoubleT() != null) return PrimitiveTypeKind.DOUBLE;

    throw new IllegalStateException("Primitive type kind not found: " + primitiveTypeDef);
  }

  // var --------------------------------------------

  @Contract(pure = true)
  @NotNull
  public static List<SchemaTypeDef> supplemented(@NotNull SchemaVarTypeDef varTypeDef) {
    return SchemaPsiImplUtilExt.supplemented(varTypeDef);
  }

//  @Nullable
//  public static String getName(@NotNull SchemaSupplementDef schemaSupplementDef) {
//
//  }

  // fqn type ref --------------------------------------------

  // not exposed through PSI
  @Contract(pure = true)
  @Nullable
  public static PsiReference getReference(@NotNull SchemaFqnTypeRef typeRef) {
    List<SchemaFqnSegment> fqnSegmentList = typeRef.getFqn().getFqnSegmentList();
    if (fqnSegmentList.isEmpty()) return null;
    return fqnSegmentList.get(fqnSegmentList.size() - 1).getReference();
  }

  @Contract(pure = true)
  @Nullable
  public static SchemaTypeDef resolve(@NotNull SchemaFqnTypeRef typeRef) {
    PsiReference reference = getReference(typeRef);
    if (reference == null) return null;
    PsiElement element = reference.resolve();
    if (element instanceof SchemaTypeDef) return (SchemaTypeDef) element;
    return null;
  }


  // supplement --------------------------------------------

  // can't use SchemaSupplementDef::getFqnTypeRefList as it will include both source and all supplemented

  @Contract(pure = true)
  @Nullable
  public static SchemaFqnTypeRef sourceRef(@NotNull SchemaSupplementDef supplementDef) {
    PsiElement with = supplementDef.getWith();
    if (with == null) return null;
    return PsiTreeUtil.getNextSiblingOfType(with, SchemaFqnTypeRef.class);
  }

  @Contract(pure = true)
  @NotNull
  public static List<SchemaFqnTypeRef> supplementedRefs(@NotNull SchemaSupplementDef supplementDef) {
    /*
    PsiElement with = supplementDef.getWith();
    if (with == null) return Collections.emptyList();

    SchemaFqnTypeRef ref = PsiTreeUtil.getPrevSiblingOfType(with, SchemaFqnTypeRef.class);
    if (ref == null) return Collections.emptyList();

    List<SchemaFqnTypeRef> result = new ArrayList<>();
    while (ref != null) {
      result.add(ref);
      ref = PsiTreeUtil.getPrevSiblingOfType(ref, SchemaFqnTypeRef.class);
    }

    return result;
    */

    List<SchemaFqnTypeRef> result = new ArrayList<>();

    for (PsiElement element = supplementDef.getSupplement();
         element != null && element.getNode().getElementType() != S_WITH;
         element = element.getNextSibling()) {

      if (element instanceof SchemaFqnTypeRef) result.add((SchemaFqnTypeRef) element);
    }

    return result;
  }

  @Contract(pure = true)
  @Nullable
  public static SchemaTypeDef source(@NotNull SchemaSupplementDef supplementDef) {
    return SchemaPsiImplUtilExt.source(supplementDef);
  }

  @Contract(pure = true)
  @NotNull
  public static List<SchemaTypeDef> supplemented(@NotNull SchemaSupplementDef supplementDef) {
    return SchemaPsiImplUtilExt.supplemented(supplementDef);
  }

  @Contract(pure = true)
  @NotNull
  public static ItemPresentation getPresentation(@NotNull SchemaSupplementDef supplementDef) {
    return SchemaPsiImplUtilExt.getPresentation(supplementDef);
  }

//  public static PsiElement setName(SchemaFqnTypeRef fqnTypeRef, String name) {
//    SchemaFqn oldFqn = fqnTypeRef.getFqn();
//    SchemaFqn newFqn = SchemaElementFactory.createFqn(fqnTypeRef.getProject(), name);
//    oldFqn.replace(newFqn);
//    return fqnTypeRef;
//  }

  // segment --------------------------------------------

  /**
   * @return FQN of this segment. If it's a part of a larger FQN, then all segments up to
   * (including) this one are returned.
   */
  @Contract(pure = true)
  @NotNull
  public static Fqn getFqn(SchemaFqnSegment e) {
    SchemaFqn schemaFqn = (SchemaFqn) e.getParent();
    assert schemaFqn != null;

    List<SchemaFqnSegment> fqnSegmentList = schemaFqn.getFqnSegmentList();
    List<String> segments = new ArrayList<>(fqnSegmentList.size());

    for (SchemaFqnSegment segment : fqnSegmentList) {
      segments.add(segment.getName());
      if (segment == e) break;
    }

    return new Fqn(segments);
  }

  @Contract(pure = true)
  @Nullable
  public static SchemaFqn getSchemaFqn(SchemaFqnSegment segment) {
    PsiElement fqn = segment.getParent();
    if (fqn instanceof SchemaFqn) {
      return (SchemaFqn) fqn;
    }

    return null;
  }

  @Contract(pure = true)
  @Nullable
  public static SchemaFqnTypeRef getSchemaFqnTypeRef(SchemaFqnSegment segment) {
    PsiElement fqn = segment.getParent();
    if (fqn instanceof SchemaFqn) {
      PsiElement fqnParent = fqn.getParent();
      if (fqnParent instanceof SchemaFqnTypeRef) {
        return (SchemaFqnTypeRef) fqnParent;
      }
    }

    return null;
  }

  @Contract(pure = true)
  @Nullable
  public static String getName(SchemaFqnSegment segment) {
    return getNameIdentifier(segment).getText();
  }

  @Contract(pure = true)
  @NotNull
  public static PsiElement setName(SchemaFqnSegment segment, String name) {
    segment.getQid().setName(name);
    return segment;
  }

  @Contract(pure = true)
  @NotNull
  public static PsiElement getNameIdentifier(SchemaFqnSegment segment) {
    return segment.getQid().getId();
  }

  @Contract(pure = true)
  public static boolean isLast(SchemaFqnSegment segment) {
    PsiElement parent = segment.getParent();
    if (parent instanceof SchemaFqn) {
      SchemaFqn schemaFqn = (SchemaFqn) parent;
      List<SchemaFqnSegment> segmentList = schemaFqn.getFqnSegmentList();
      return segment == getLast(segmentList);
    }
    return false;
  }

  @Contract(pure = true)
  @Nullable
  public static PsiReference getReference(SchemaFqnSegment segment) {
    return SchemaReferenceFactory.getFqnReference(segment);
  }


  // member decls --------------------------------------------
  // field decl

  @Contract(pure = true)
  @Nullable
  public static String getName(SchemaFieldDecl fieldDecl) {
    return getNameIdentifier(fieldDecl).getText();
  }

  public static PsiElement setName(SchemaFieldDecl fieldDecl, String name) {
//    if (NamingConventions.validateFieldName(name) != null) name = NamingConventions.enquote(name);
    fieldDecl.getQid().setName(name);
    return fieldDecl;
  }

  @Contract(pure = true)
  @NotNull
  public static PsiElement getNameIdentifier(SchemaFieldDecl fieldDecl) {
    return fieldDecl.getQid().getId();
  }

  public static int getTextOffset(@NotNull SchemaFieldDecl fieldDecl) {
    PsiElement nameIdentifier = fieldDecl.getNameIdentifier();
    return nameIdentifier.getTextOffset();
  }

  @Contract(pure = true)
  @NotNull
  public static ItemPresentation getPresentation(@NotNull SchemaFieldDecl fieldDecl) {
    return SchemaPsiImplUtilExt.getPresentation(fieldDecl);
  }

  @NotNull
  public static SchemaRecordTypeDef getRecordTypeDef(@NotNull SchemaFieldDecl fieldDecl) {
    SchemaRecordTypeDef recordTypeDef = PsiTreeUtil.getParentOfType(fieldDecl, SchemaRecordTypeDef.class);
    assert recordTypeDef != null;
    return recordTypeDef;
  }

  // varTypeMember decl

  @Contract(pure = true)
  @Nullable
  public static String getName(SchemaVarTagDecl varTagDecl) {
    return getNameIdentifier(varTagDecl).getText();
  }

  public static PsiElement setName(SchemaVarTagDecl varTagDecl, String name) {
//    if (NamingConventions.validateTagName(name) != null) name = NamingConventions.enquote(name);
    varTagDecl.getQid().setName(name);
    return varTagDecl;
  }

  @Contract(pure = true)
  @NotNull
  public static PsiElement getNameIdentifier(SchemaVarTagDecl varTagDecl) {
    return varTagDecl.getQid().getId();
  }

  public static int getTextOffset(@NotNull SchemaVarTagDecl varTagDecl) {
    PsiElement nameIdentifier = varTagDecl.getNameIdentifier();
    return nameIdentifier.getTextOffset();
  }

  @Contract(pure = true)
  @NotNull
  public static ItemPresentation getPresentation(@NotNull SchemaVarTagDecl varTagDecl) {
    return SchemaPsiImplUtilExt.getPresentation(varTagDecl);
  }

  @NotNull
  public static SchemaVarTypeDef getVarTypeDef(@NotNull SchemaVarTagDecl varTagDecl) {
    SchemaVarTypeDef varTypeDef = PsiTreeUtil.getParentOfType(varTagDecl, SchemaVarTypeDef.class);
    assert varTypeDef != null;
    return varTypeDef;
  }

  // vartype default ref

  @Contract(pure = true)
  @Nullable
  public static PsiReference getReference(@NotNull SchemaVarTagRef varTagRef) {
    return SchemaReferenceFactory.getVarTagReference(varTagRef);
  }

  @Contract(pure = true)
  @Nullable
  public static PsiElement getNameIdentifier(@NotNull SchemaVarTagRef varTagRef) {
    return varTagRef.getQid().getId();
  }

  public static PsiElement setName(SchemaVarTagRef varTagRef, String name) {
//    if (NamingConventions.validateTagName(name) != null) name = NamingConventions.enquote(name);
    varTagRef.getQid().setName(name);
    return varTagRef;
  }

  // enumMember decl

  @Contract(pure = true)
  @Nullable
  public static String getName(SchemaEnumMemberDecl enumMemberDecl) {
    return getNameIdentifier(enumMemberDecl).getText();
  }

  public static PsiElement setName(SchemaEnumMemberDecl enumMemberDecl, String name) {
    enumMemberDecl.getQid().setName(name);
    return enumMemberDecl;
  }

  @Contract(pure = true)
  @NotNull
  public static PsiElement getNameIdentifier(SchemaEnumMemberDecl enumMemberDecl) {
    return enumMemberDecl.getQid().getId();
  }

  // custom param

  @Contract(pure = true)
  @Nullable
  public static String getName(SchemaCustomParam customParam) {
    return getNameIdentifier(customParam).getText();
  }

  public static PsiElement setName(SchemaCustomParam customParam, String name) {
    customParam.getQid().setName(name);
    return customParam;
  }

  @Contract(pure = true)
  @NotNull
  public static PsiElement getNameIdentifier(SchemaCustomParam customParam) {
    return customParam.getQid().getId();
  }

  // common toNullableString for all stub-based elements --------------------------------------------

  @Contract(pure = true)
  @NotNull
  public static String toString(PsiElement element) {
    return element.getClass().getSimpleName() + "(" + element.getNode().getElementType().toString() + ")";
  }

  /////////////

  @Contract(value = "null -> null", pure = true)
  private static <T> T getLast(List<T> list) {
    if (list == null || list.isEmpty()) return null;
    return list.get(list.size() - 1);
  }
}