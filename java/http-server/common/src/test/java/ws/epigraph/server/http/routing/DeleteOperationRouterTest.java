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
import org.junit.Test;
import ws.epigraph.gdata.GDataValue;
import ws.epigraph.gdata.GPrimitiveDatum;
import ws.epigraph.schema.ResourcesSchema;
import ws.epigraph.schema.ResourceDeclaration;
import ws.epigraph.schema.operations.DeleteOperationDeclaration;
import ws.epigraph.schema.operations.OperationDeclaration;
import ws.epigraph.projections.StepsAndProjection;
import ws.epigraph.projections.req.delete.ReqDeleteFieldProjection;
import ws.epigraph.projections.req.output.ReqOutputFieldProjection;
import ws.epigraph.projections.req.path.ReqFieldPath;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.SimpleTypesResolver;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.service.Resource;
import ws.epigraph.service.ServiceInitializationException;
import ws.epigraph.service.operations.DeleteOperation;
import ws.epigraph.service.operations.DeleteOperationRequest;
import ws.epigraph.service.operations.ReadOperationResponse;
import ws.epigraph.test.TestUtil;
import ws.epigraph.tests.*;
import ws.epigraph.url.DeleteRequestUrl;
import ws.epigraph.url.parser.UrlSubParserDefinitions;
import ws.epigraph.url.parser.psi.UrlDeleteUrl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.*;
import static ws.epigraph.server.http.routing.RoutingTestUtil.failIfSearchFailure;
import static ws.epigraph.server.http.routing.RoutingTestUtil.parseIdl;
import static ws.epigraph.test.TestUtil.failIfHasErrors;
import static ws.epigraph.test.TestUtil.lines;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class DeleteOperationRouterTest {
  private final TypesResolver resolver = new SimpleTypesResolver(
      PersonId.type,
      Person.type,
      User.type,
      UserId.type,
      UserRecord.type,
      String_Person_Map.type,
      epigraph.String.type,
      epigraph.Boolean.type
  );

  private final String idlText = lines(
      "namespace test",
      "import ws.epigraph.tests.Person",
      "import ws.epigraph.tests.UserRecord",
      "resource users : map[String,Person] {",
      "  delete {",
      "    id = \"pathless.1\"",
      "    deleteProjection []( :`record` (id, firstName) )",
      "    outputProjection [required]( :`record` (id, firstName) )",
      "  }",
      "  delete pathless2 {",
      "    id = \"pathless.2\"",
      "    deleteProjection []( :`record` (id, firstName, lastName) )",
      "    outputProjection [required]( :`record` (id, firstName, lastName) )",
      "  }",
      "  delete path1 {",
      "    id = \"path.1\"",
      "    path /.",
      "    deleteProjection :`record` (id, firstName, bestFriend :`record` (id, firstName) )",
      "    outputProjection :`record` (id, firstName, bestFriend :`record` (id, firstName) )",
      "  }",
      "  delete path2 {",
      "    id = \"path.2\"",
      "    path /.:`record`/bestFriend",
      "    deleteProjection :`record` (id, firstName)",
      "    outputProjection :`record` (id, firstName)",
      "  }",
      "}"
  );

  private final Resource resource;

  {
    try {
      ResourcesSchema schema = parseIdl(idlText, resolver);
      ResourceDeclaration resourceDeclaration = schema.resources().get("users");
      assertNotNull(resourceDeclaration);

      final List<OpImpl> deleteOps = new ArrayList<>();

      for (final OperationDeclaration operationDeclaration : resourceDeclaration.operations())
        deleteOps.add(new OpImpl((DeleteOperationDeclaration) operationDeclaration));

      resource = new Resource(
          resourceDeclaration,
          Collections.emptyList(),
          Collections.emptyList(),
          Collections.emptyList(),
          deleteOps,
          Collections.emptyList()

      );
    } catch (ServiceInitializationException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public void testPathless() throws PsiProcessingException {
    testRouting(
        "/users<[](:record(id))>[1](:record(firstName))",
        "pathless.1",
        null,
        "[]( :record ( id ) )",
        1,
        "[ \"1\" ]( :record ( firstName ) )"
    );
  }

  @Test
  public void testPathless2() throws PsiProcessingException {
    testRouting(
        "/users<[](:record(id))>[1](:record(id,lastName))",
        "pathless.2",
        null,
        "[]( :record ( id ) )",
        1,
        "[ \"1\" ]( :record ( id, lastName ) )"
    );
  }

  @Test
  public void testPathless3() throws PsiProcessingException {
    testRouting(
        "/users<[](:record(id))>/1:record(id,lastName)",
        "pathless.2",
        null,
        "[]( :record ( id ) )",
        3,
        "/ \"1\" :record ( id, lastName )"
    );
  }

  @Test
  public void testPath1WithDelete() throws PsiProcessingException {
    testRouting("/users/1<:record(id)>:record(id)", "path.1", "/ \"1\"", ":record ( id )", 1, ":record ( id )");
  }


  @Test
  public void testPath2() throws PsiProcessingException {
    testRouting(
        "/users/1:record/bestFriend<:record(id)>:record(id)",
        "path.2",
        "/ \"1\" :record / bestFriend",
        ":record ( id )",
        1,
        ":record ( id )"
    );
  }

  private void testRouting(
      @NotNull String url,
      @NotNull String expectedId,
      @Nullable String expectedPath,
      @Nullable String expectedDeleteProjection,
      int expectedOutputSteps,
      @NotNull String expectedOutputProjection) throws PsiProcessingException {

    final OperationSearchSuccess<? extends DeleteOperation<?>, DeleteRequestUrl> s = getTargetOpId(url);
    final OpImpl op = (OpImpl) s.operation();
    assertEquals(expectedId, op.getId());

    final @NotNull DeleteRequestUrl deleteRequestUrl = s.requestUrl();
    final ReqFieldPath path = deleteRequestUrl.path();

    if (expectedPath == null)
      assertNull(path);
    else {
      assertNotNull(path);
      assertEquals(expectedPath, TestUtil.printReqVarPath(path.varProjection()));
    }

    final @Nullable ReqDeleteFieldProjection deleteProjection = deleteRequestUrl.deleteProjection();
    if (expectedDeleteProjection == null)
      assertNull(deleteProjection);
    else {
      assertNotNull(deleteProjection);
      assertEquals(expectedDeleteProjection, TestUtil.printReqDeleteVarProjection(deleteProjection.varProjection()));
    }

    final StepsAndProjection<ReqOutputFieldProjection> stepsAndProjection = deleteRequestUrl.outputProjection();

    assertEquals(expectedOutputSteps, stepsAndProjection.pathSteps());
    assertEquals(
        expectedOutputProjection,
        TestUtil.printReqOutputVarProjection(stepsAndProjection.projection().varProjection(), expectedOutputSteps)
    );
  }

  @SuppressWarnings("unchecked")
  private OperationSearchSuccess<? extends DeleteOperation<?>, DeleteRequestUrl> getTargetOpId(final @NotNull String url)
      throws PsiProcessingException {
    final @NotNull OperationSearchResult<DeleteOperation<?>> oss = DeleteOperationRouter.INSTANCE.findOperation(
        null,
        parseDeleteUrl(url),
        resource, resolver
    );

    failIfSearchFailure(oss);
    assertTrue(oss instanceof OperationSearchSuccess);
    return (OperationSearchSuccess<? extends DeleteOperation<?>, DeleteRequestUrl>) oss;
  }

  private class OpImpl extends DeleteOperation<PersonId_Person_Map.Data> {

    protected OpImpl(final DeleteOperationDeclaration declaration) {
      super(declaration);
    }

    public @Nullable String getId() {
      final @Nullable GDataValue value = declaration().annotations().get("id");
      if (value instanceof GPrimitiveDatum)
        return ((GPrimitiveDatum) value).value().toString();
      return null;
    }

    @Override
    public @NotNull CompletableFuture<ReadOperationResponse<PersonId_Person_Map.Data>> process(
        final @NotNull DeleteOperationRequest request) {
      throw new RuntimeException("unreachable");
    }
  }

  private static @NotNull UrlDeleteUrl parseDeleteUrl(@NotNull String url) {
    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    UrlDeleteUrl urlPsi = EpigraphPsiUtil.parseText(
        url,
        UrlSubParserDefinitions.DELETE_URL,
        errorsAccumulator
    );

    failIfHasErrors(urlPsi, errorsAccumulator);

    return urlPsi;
  }
}