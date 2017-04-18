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
import org.jetbrains.annotations.Nullable;
import ws.epigraph.invocation.OperationInvocationError;
import ws.epigraph.psi.PsiProcessingError;
import ws.epigraph.schema.operations.OperationKind;

import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class RequestParsingInvocationError extends PsiProcessingInvocationError
    implements HtmlCapableOperationInvocationError {

  private final @NotNull String resourceName;
  private final @NotNull OperationKind operationKind;
  private final @Nullable String operationName;
  private final @NotNull String request;
  private final @NotNull List<PsiProcessingError> errors;

  public RequestParsingInvocationError(
      @NotNull String resourceName,
      @NotNull OperationKind operationKind,
      @Nullable String operationName,
      @NotNull String request,
      @NotNull List<PsiProcessingError> errors) {

    this.resourceName = resourceName;
    this.operationKind = operationKind;
    this.operationName = operationName;
    this.request = request;
    this.errors = errors;
  }

  @Override
  public @NotNull OperationInvocationError.Status status() { return Status.BAD_REQUEST; }

  @Override
  public @NotNull String message() {
    return String.format(
        "Failed to parse %s operation '%s' request in resource '%s'\n%s",
        operationKind, operationName == null ? "<default>" : operationName, resourceName,
        psiParsingErrorsReport(request, errors, false)
    );
  }

  @Override
  public @NotNull String htmlMessage() {
    return String.format(
        "Failed to parse %s operation '%s' request in resource '%s'\n%s",
        operationKind, operationName == null ? "<default>" : operationName, resourceName,
        psiParsingErrorsReport(request, errors, true)
    );
  }

}
