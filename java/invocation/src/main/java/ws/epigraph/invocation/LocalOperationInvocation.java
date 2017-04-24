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

package ws.epigraph.invocation;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.epigraph.schema.operations.OperationDeclaration;
import ws.epigraph.service.operations.Operation;
import ws.epigraph.service.operations.OperationRequest;
import ws.epigraph.service.operations.OperationResponse;

import java.util.concurrent.CompletableFuture;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class LocalOperationInvocation<Req extends OperationRequest, Rsp extends OperationResponse>
    implements OperationInvocation<Req, Rsp> {

  private static final Logger LOG = LoggerFactory.getLogger(LocalOperationInvocation.class);

  private final @NotNull Operation<?, Req, Rsp> operation;

  public LocalOperationInvocation(final @NotNull Operation<?, Req, Rsp> operation) {this.operation = operation;}

  @Override
  public @NotNull CompletableFuture<OperationInvocationResult<Rsp>> invoke(final @NotNull Req request) {
    try {
      return operation.process(request).thenApply(OperationInvocationResult::success);
    } catch (Exception e) {
      final OperationDeclaration declaration = operation.declaration();
      String name = declaration.name() == null ? "" : " '" + declaration.name() + "'";
      LOG.debug("Error invoking " + declaration.kind() + " operation" + name, e);

      return CompletableFuture.completedFuture(
          OperationInvocationResult.failure(
              new OperationInvocationErrorImpl(
                  e.toString(),
                  OperationInvocationError.Status.INTERNAL_SERVER_ERROR
              )
          )
      );
    }
  }
}