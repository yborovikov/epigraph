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

package ws.epigraph.client.http;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.nio.client.HttpAsyncClient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.data.Data;
import ws.epigraph.invocation.OperationInvocationContext;
import ws.epigraph.projections.op.input.OpInputFieldProjection;
import ws.epigraph.projections.req.input.ReqInputFieldProjection;
import ws.epigraph.schema.operations.CustomOperationDeclaration;
import ws.epigraph.service.operations.CustomOperationRequest;
import ws.epigraph.types.Type;
import ws.epigraph.types.TypeApi;
import ws.epigraph.util.HttpStatusCode;

import java.nio.charset.Charset;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class RemoteCustomOperationInvocation
    extends AbstractRemoteOperationInvocation<CustomOperationRequest, CustomOperationDeclaration> {

  public RemoteCustomOperationInvocation(
      final @NotNull HttpHost host,
      final @NotNull HttpAsyncClient httpClient,
      final @NotNull String resourceName,
      final @NotNull CustomOperationDeclaration operationDeclaration,
      final @NotNull ServerProtocol serverProtocol,
      final @NotNull Charset charset) {
    super(host, httpClient, resourceName, operationDeclaration, serverProtocol, charset, HttpStatusCode.OK);
  }

  @Override
  protected HttpRequest composeHttpRequest(
      final @NotNull CustomOperationRequest operationRequest,
      final @NotNull OperationInvocationContext operationInvocationContext) {

    ReqInputFieldProjection inputFieldProjection = operationRequest.inputProjection();

    String uri = UriComposer.composeCustomUri(
        resourceName,
        operationRequest.path(),
        inputFieldProjection,
        operationRequest.outputProjection()
    );

    return new HttpPost(uri);
  }

  @Override
  protected @Nullable HttpContentProducer requestContentProducer(
      @NotNull CustomOperationRequest request, @NotNull OperationInvocationContext operationInvocationContext) {

    ReqInputFieldProjection inputFieldProjection = request.inputProjection();
    Data data = request.data();

    OpInputFieldProjection opInputFieldProjection = operationDeclaration.inputProjection();
    if (data == null || opInputFieldProjection == null)
      return null;

    Type dataType = data.type();
    TypeApi projectionType = inputFieldProjection == null
                             ? opInputFieldProjection.varProjection().type()
                             : inputFieldProjection.varProjection().type();

    if (!projectionType.isAssignableFrom(dataType)) {
      throw new IllegalArgumentException(
          "Input projection type " + projectionType.name() + " is not assignable from data type " + dataType.name());
    }

    return serverProtocol.customRequestContentProducer(
        inputFieldProjection == null ? null : inputFieldProjection.varProjection(),
        opInputFieldProjection.varProjection(),
        data,
        operationInvocationContext
    );
  }

}
