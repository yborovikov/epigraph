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

package ws.epigraph.ideaplugin.schema.features.actions.fixes;

import com.intellij.codeInsight.FileModificationService;
import com.intellij.codeInsight.daemon.impl.ShowAutoImportPass;
import com.intellij.codeInsight.hint.HintManager;
import com.intellij.codeInspection.HintAction;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.PopupChooserBuilder;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.ui.components.JBList;
import com.intellij.util.IncorrectOperationException;
import ws.epigraph.ideaplugin.schema.SchemaBundle;
import ws.epigraph.ideaplugin.schema.features.actions.ImportTypeAction;
import ws.epigraph.ideaplugin.schema.features.actions.SchemaNamespaceRenderer;
import ws.epigraph.ideaplugin.schema.index.SchemaIndexUtil;
import ws.epigraph.ideaplugin.schema.index.SchemaSearchScopeUtil;
import ws.epigraph.ideaplugin.schema.options.SchemaSettings;
import ws.epigraph.lang.Qn;
import ws.epigraph.schema.parser.psi.SchemaFile;
import ws.epigraph.schema.parser.psi.SchemaQnTypeRef;
import ws.epigraph.schema.parser.psi.SchemaTypeDef;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ImportTypeIntentionFix implements HintAction {
  // TODO(low) implement LocalQuickFix, see CreateNSDeclarationIntentionFix

  private final SchemaQnTypeRef typeRef;

  public ImportTypeIntentionFix(SchemaQnTypeRef typeRef) {
    this.typeRef = typeRef;
  }

  @Override
  public boolean showHint(@NotNull Editor editor) {
    if (!SchemaSettings.getInstance().SHOW_SCHEMA_ADD_IMPORT_HINTS) return false;
    if (typeRef.resolve() != null) return false;

    List<String> importOptions = calculateImportOptions();
    if (importOptions.isEmpty()) return false;

    final String message = ShowAutoImportPass.getMessage(importOptions.size() > 1, importOptions.get(0));
    final ImportTypeAction action = new ImportTypeAction((SchemaFile) typeRef.getContainingFile(), importOptions, editor);
    HintManager.getInstance().showQuestionHint(editor, message,
        typeRef.getTextOffset(),
        typeRef.getTextRange().getEndOffset(), action);

    return false;
  }

  @Nls
  @NotNull
  @Override
  public String getText() {
    return SchemaBundle.message("actions.import.namespace");
  }

  @Nls
  @NotNull
  @Override
  public String getFamilyName() {
    return getText();
  }

  @Override
  public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
    return typeRef != null && typeRef.isValid() && typeRef.resolve() == null; // TODO and there are any options to import
  }

  @SuppressWarnings("unchecked")
  @Override
  public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
    if (!FileModificationService.getInstance().prepareFileForWrite(file)) return;
    if (typeRef.resolve() != null) return;

    final List<String> importOptions = calculateImportOptions();
    if (importOptions.isEmpty()) return;

    if (importOptions.size() > 1) {
      final JList list = new JBList(importOptions);
      list.setCellRenderer(SchemaNamespaceRenderer.INSTANCE);

      Runnable runnable = () -> {
        final int index = list.getSelectedIndex();
        if (index < 0) return;
        PsiDocumentManager.getInstance(project).commitAllDocuments();

        CommandProcessor.getInstance().executeCommand(project, () ->
                ApplicationManager.getApplication().runWriteAction(() ->
                    ImportTypeAction.addImport((SchemaFile) file, importOptions.get(index))
                ),
            getText(), getFamilyName()
        );
      };

      new PopupChooserBuilder(list).
          setTitle(SchemaBundle.message("actions.select.namespace.to.import")).
          setItemChoosenCallback(runnable).
          createPopup().
          showInBestPositionFor(editor);
    } else {
      ImportTypeAction.addImport((SchemaFile) file, importOptions.get(0));
    }
  }

  @Override
  public boolean startInWriteAction() {
    return true;
  }

  ////

  private List<String> calculateImportOptions() {
    final Qn typeRefQn = typeRef.getQn().getQn();
    final int tailSegmentsToRemove = typeRefQn.size() == 1 ? 0 : typeRefQn.size() - 1;

    return SchemaIndexUtil.findTypeDefs(typeRef.getProject(), null, typeRefQn, SchemaSearchScopeUtil.getSearchScope(typeRef)).stream()
        .map(SchemaTypeDef::getQn)
        .filter(Objects::nonNull)
        .map(qn -> qn.removeTailSegments(tailSegmentsToRemove).toString())
        .sorted()
        .distinct()
        .collect(Collectors.toList());
  }
}
