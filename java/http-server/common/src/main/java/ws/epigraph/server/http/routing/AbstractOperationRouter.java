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

package ws.epigraph.server.http.routing;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.psi.DefaultPsiProcessingContext;
import ws.epigraph.psi.PsiProcessingContext;
import ws.epigraph.schema.operations.OperationDeclaration;
import ws.epigraph.psi.PsiProcessingMessage;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.service.Resource;
import ws.epigraph.service.operations.Operation;
import ws.epigraph.types.DataTypeApi;
import ws.epigraph.url.RequestUrl;
import ws.epigraph.url.parser.psi.UrlUrl;

import java.util.*;

/**
 * Generic operations router, responsible for picking operation based on the URL.
 * <p/>
 * Algorithm description:
 * <p/>
 * If operation name is provided (by using {@code Epigraph-Operation} HTTP header), then operation is looked up by name.
 * <p/>
 * Else operations are sorted by op path length in descending order and are tried one by one. The first operation
 * such that URL can be parsed against it's declaration (for instance path, input projection, output projection) wins.
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 * @see "operations.epigraph"
 */
public abstract class AbstractOperationRouter<
    U extends UrlUrl,
    D extends OperationDeclaration,
    O extends Operation<D, ?, ?>>
    implements OperationRouter<U, O> {

  @Override
  public @NotNull OperationSearchResult<O> findOperation(
      final @Nullable String operationName,
      final @NotNull U urlPsi,
      final @NotNull Resource resource,
      final @NotNull TypesResolver resolver)
      throws PsiProcessingException {

    final @NotNull DataTypeApi resourceFieldType = resource.declaration().fieldType();

    if (operationName == null) {
      final Map<O, List<PsiProcessingMessage>> matchingErrors = new HashMap<>();

      for (final O operation : operations(resource)) {
        @NotNull OperationSearchResult<O> matchingResult =
            matchOperation(operation, resourceFieldType, urlPsi, resolver);

        if (matchingResult instanceof OperationSearchSuccess)
          return matchingResult;

        if (matchingResult instanceof OperationSearchFailure) {
          matchingErrors.put(
              operation,
              ((OperationSearchFailure<O>) matchingResult).errors().get(operation)
          );
        }

      }

      return matchingErrors.isEmpty() ?
             OperationNotFound.instance() :
             new OperationSearchFailure<>(matchingErrors);
    } else {
      final @Nullable O operation = namedOperation(operationName, resource);
      return matchOperation(operation, resourceFieldType, urlPsi, resolver);
    }
  }

  protected abstract @Nullable O namedOperation(@Nullable String name, @NotNull Resource resource);

  protected abstract @NotNull Collection<? extends O> operations(@NotNull Resource resource);

  private @NotNull OperationSearchResult<O> matchOperation(
      @Nullable O operation,
      @NotNull DataTypeApi resourceType,
      @NotNull U urlPsi,
      @NotNull TypesResolver resolver
  ) {

    if (operation == null)
      return OperationNotFound.instance();
    else {
      PsiProcessingContext context = new DefaultPsiProcessingContext();

      RequestUrl request = null;

      try {
        request = parseUrl(
            resourceType,
            operation.declaration(),
            urlPsi,
            resolver,
            context
        );
      } catch (PsiProcessingException e) {
        context.setMessages(e.messages());
      }

      if (request != null)
        validateMatchingRequest(request, context);

      if (context.messages().isEmpty()) {
        assert request != null;
        return new OperationSearchSuccess<>(
            operation,
            request
        );
      } else
        return new OperationSearchFailure<>(
            Collections.singletonMap(operation, context.messages())
        );
    }
  }

  protected void validateMatchingRequest(@NotNull RequestUrl request, @NotNull PsiProcessingContext context) {}

  protected abstract @NotNull RequestUrl parseUrl(
      @NotNull DataTypeApi resourceType,
      @NotNull D opDecl,
      @NotNull U urlPsi,
      @NotNull TypesResolver resolver,
      @NotNull PsiProcessingContext context) throws PsiProcessingException;

}
