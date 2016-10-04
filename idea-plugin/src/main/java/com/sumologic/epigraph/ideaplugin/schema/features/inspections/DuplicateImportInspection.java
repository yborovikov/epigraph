package com.sumologic.epigraph.ideaplugin.schema.features.inspections;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.util.containers.MultiMap;
import com.sumologic.epigraph.ideaplugin.schema.brains.ImportsManager;
import com.sumologic.epigraph.ideaplugin.schema.features.actions.fixes.OptimizeImportsQuickFix;
import io.epigraph.lang.Qn;
import io.epigraph.schema.parser.psi.SchemaImportStatement;
import io.epigraph.schema.parser.psi.SchemaImports;
import io.epigraph.schema.parser.psi.SchemaVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class DuplicateImportInspection extends LocalInspectionTool {
  @NotNull
  @Override
  public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
    return new SchemaVisitor() {
      @Override
      public void visitImports(@NotNull SchemaImports schemaImports) {
        super.visitImports(schemaImports);

        List<SchemaImportStatement> imports = schemaImports.getImportStatementList();

        MultiMap<Qn, SchemaImportStatement> importsByQn = ImportsManager.getImportsByQn(imports);

        for (Map.Entry<Qn, Collection<SchemaImportStatement>> entry : importsByQn.entrySet()) {
          entry.getValue().stream()
              .filter(is -> entry.getValue().size() > 1)
              .forEach(is -> holder.registerProblem(is,
                  InspectionBundle.message("import.duplicate.problem.descriptor"),
                  OptimizeImportsQuickFix.INSTANCE));
        }
      }
    };
  }
}
