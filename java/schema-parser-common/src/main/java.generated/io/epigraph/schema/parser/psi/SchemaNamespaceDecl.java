// This is a generated file. Not intended for manual editing.
package io.epigraph.schema.parser.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import io.epigraph.schema.parser.psi.stubs.SchemaNamespaceDeclStub;
import io.epigraph.lang.Qn;

public interface SchemaNamespaceDecl extends PsiElement, StubBasedPsiElement<SchemaNamespaceDeclStub> {

  @NotNull
  List<SchemaAnnotation> getAnnotationList();

  @Nullable
  SchemaQn getQn();

  @Nullable
  PsiElement getCurlyLeft();

  @Nullable
  PsiElement getCurlyRight();

  @NotNull
  PsiElement getNamespace();

  @Nullable
  Qn getFqn();

  @NotNull
  String toString();

}