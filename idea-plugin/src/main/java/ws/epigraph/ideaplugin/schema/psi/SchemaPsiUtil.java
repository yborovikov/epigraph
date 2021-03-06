/*
 * Copyright 2016 Sumo Logic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ws.epigraph.ideaplugin.schema.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class SchemaPsiUtil {
//  @Nullable
//  public static <T extends PsiElement> T findFirstParent(@NotNull PsiElement e, Class<T> cls) {
//    //noinspection unchecked
//    return (T) PsiTreeUtil.findFirstParent(e, cls::isInstance);
//  }
//
//  public static boolean hasParent(@NotNull PsiElement e, Class<?>... classes) {
//    return null != PsiTreeUtil.findFirstParent(e, psiElement -> {
//      for (Class<?> cls : classes) {
//        if (cls.isInstance(psiElement)) return true;
//      }
//      return false;
//    });
//  }

  @Contract("null, _ -> false")
  public static boolean hasPrevSibling(@Nullable PsiElement e, IElementType... elementTypes) {
    if (e == null) return false;
    for (PsiElement sibling = e.getPrevSibling(); sibling != null; sibling = sibling.getPrevSibling()) {
      for (IElementType elementType : elementTypes) {
        if (sibling.getNode().getElementType().equals(elementType)) return true;
      }
    }

    return false;
  }

  public static boolean hasNextSibling(@NotNull PsiElement e, IElementType... elementTypes) {
    return hasNextSibling(e, new ElementTypeQualifier(elementTypes));
  }

  public static boolean hasNextSibling(@NotNull PsiElement e, @NotNull ElementQualifier qualifier) {
    for (PsiElement sibling = e.getNextSibling(); sibling != null; sibling = sibling.getNextSibling()) {
      if (qualifier.qualifies(sibling)) return true;
    }

    return false;
  }

  public static boolean hasNextLeaf(@NotNull com.intellij.psi.PsiElement e, IElementType... elementTypes) {
    PsiElement leaf = PsiTreeUtil.nextLeaf(e);
    while (leaf != null) {
      for (IElementType elementType : elementTypes) {
        if (elementType.equals(leaf.getNode().getElementType())) return true;
      }
      leaf = PsiTreeUtil.nextLeaf(leaf);
    }
    return false;
  }

  @Nullable
  public static PsiElement prevNonWhitespaceSibling(@NotNull PsiElement e) {
    PsiElement res = e.getPrevSibling();
    while (res != null && res.getNode().getElementType() == TokenType.WHITE_SPACE) {
      res = res.getPrevSibling();
    }

    return res;
  }

  @Nullable
  public static PsiElement nextNonWhitespaceSibling(@NotNull PsiElement e) {
    PsiElement res = e.getNextSibling();
    while (res != null && res.getNode().getElementType() == TokenType.WHITE_SPACE) {
      res = res.getNextSibling();
    }

    return res;
  }

  public static boolean hasChildOfType(@NotNull PsiElement e, IElementType... elementTypes) {
    return hasChildMatching(e, new ElementTypeQualifier(elementTypes));
  }

  public static boolean hasChildMatching(@NotNull PsiElement e, @NotNull ElementQualifier qualifier) {
    PsiElement firstChild = e.getFirstChild();
    return firstChild != null && hasNextSibling(firstChild, qualifier);
  }

  @SafeVarargs
  @Nullable
  public static <T extends PsiElement> T getElementOrParentOfType(@Nullable final T element,
                                                                  @NotNull final Class<? extends T>... classes) {

    if (element == null) return null;
    //noinspection ConfusingArgumentToVarargsMethod
    if (PsiTreeUtil.instanceOf(element, classes)) return element;
    return PsiTreeUtil.getParentOfType(element, classes);
  }

  public interface ElementQualifier {
    boolean qualifies(PsiElement element);
  }

  public static class ElementTypeQualifier implements ElementQualifier {
    private final Set<IElementType> elementTypes;

    public ElementTypeQualifier(Set<IElementType> types) {
      elementTypes = types;
    }

    public ElementTypeQualifier(IElementType... types) {
      this(new HashSet<>(Arrays.asList(types)));
    }

    @Override
    public boolean qualifies(PsiElement element) {
      return element != null && element.getNode() != null && elementTypes.contains(element.getNode().getElementType());
    }
  }
}
