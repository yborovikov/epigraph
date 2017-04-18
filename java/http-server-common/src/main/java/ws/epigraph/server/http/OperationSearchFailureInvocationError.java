/*
 * Copyright 2017 Sumo Logic
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

package ws.epigraph.server.http;

import org.jetbrains.annotations.NotNull;
import ws.epigraph.invocation.OperationInvocationError;
import ws.epigraph.psi.PsiProcessingError;
import ws.epigraph.schema.operations.OperationDeclaration;
import ws.epigraph.server.http.routing.OperationSearchFailure;
import ws.epigraph.service.operations.Operation;

import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OperationSearchFailureInvocationError extends PsiProcessingInvocationError
    implements HtmlCapableOperationInvocationError {

  private final @NotNull String resourceName;
  private final @NotNull String requestText;
  private final @NotNull OperationSearchFailure<?> failure;

  public OperationSearchFailureInvocationError(
      final @NotNull String resourceName,
      final @NotNull String text,
      final @NotNull OperationSearchFailure<?> failure) {
    this.resourceName = resourceName;
    requestText = text;
    this.failure = failure;
  }

  @Override
  public @NotNull OperationInvocationError.Status status() { return Status.BAD_REQUEST; }

  @Override
  public @NotNull String message() {
    return message(false);
  }

  @Override
  public @NotNull String htmlMessage() {
    return message(true);
  }

  private @NotNull String message(boolean isHtml) {
    StringBuilder sb = new StringBuilder();
    final Map<? extends Operation<?, ?, ?>, List<PsiProcessingError>> failedOperations = failure.errors();

    if (failedOperations.size() == 1) {
      final Map.Entry<? extends Operation<?, ?, ?>, List<PsiProcessingError>> entry =
          failedOperations.entrySet().iterator().next();

      final Operation<?, ?, ?> operation = entry.getKey();
      final List<PsiProcessingError> operationErrors = entry.getValue();
      final OperationDeclaration operationDeclaration = operation.declaration();
      final RequestParsingInvocationError parsingInvocationError = new RequestParsingInvocationError(
          resourceName,
          operationDeclaration.kind(),
          operationDeclaration.name(),
          requestText,
          operationErrors
      );
      return isHtml ? parsingInvocationError.htmlMessage() : parsingInvocationError.message();

    } else {
      sb.append("Operation matching failure");

      nl(sb, 2, isHtml);

      boolean first = true;
      for (final Map.Entry<? extends Operation<?, ?, ?>, List<PsiProcessingError>> e : failedOperations.entrySet()) {
        if (first) first = false;
        else sep(sb, isHtml);

        final Operation<?, ?, ?> operation = e.getKey();
        final List<PsiProcessingError> errors = e.getValue();

        final OperationDeclaration operationDeclaration = operation.declaration();
        sb.append(operationDeclaration.kind()).append(" ");
        String name = operationDeclaration.name() == null ? "" : " '" + operationDeclaration.name() + "'";
        sb.append("operation").append(name).append(" declared at ").append(operationDeclaration.location());
        sb.append(" was not picked because of the following errors:");

        nl(sb, 1, isHtml);
        psiParsingErrorsReport(requestText, errors, isHtml);
      }

    }

    return sb.toString();
  }

  private static void nl(StringBuilder sb, int n, boolean isHtml) {
    for (int i = 0; i < n; i++) {
      sb.append(isHtml ? "<br/>" : "\n");
    }
  }
}
