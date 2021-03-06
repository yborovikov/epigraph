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
import ws.epigraph.schema.ResourcesSchema;
import ws.epigraph.schema.SchemasPsiProcessingContext;
import ws.epigraph.schema.parser.ResourcesSchemaPsiParser;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingMessage;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.schema.parser.SchemaParserDefinition;
import ws.epigraph.schema.parser.psi.SchemaFile;
import ws.epigraph.service.operations.Operation;
import ws.epigraph.url.parser.UrlSubParserDefinitions;
import ws.epigraph.url.parser.psi.UrlUrl;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.fail;
import static ws.epigraph.test.TestUtil.failIfHasErrors;
import static ws.epigraph.test.TestUtil.runPsiParser;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class RoutingTestUtil {

  private RoutingTestUtil() {}

  static @NotNull ResourcesSchema parseIdl(@NotNull String text, @NotNull TypesResolver resolver) {
    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    @NotNull SchemaFile psiFile =
        (SchemaFile) EpigraphPsiUtil.parseFile(
            "test.epigraph",
            text,
            SchemaParserDefinition.INSTANCE,
            errorsAccumulator
        );

    failIfHasErrors(psiFile, errorsAccumulator);

    return runPsiParser(true, context -> {
      SchemasPsiProcessingContext schemasPsiProcessingContext = new SchemasPsiProcessingContext();
      ResourcesSchema schema = ResourcesSchemaPsiParser.parseResourcesSchema(
          psiFile,
          resolver,
          schemasPsiProcessingContext
      );
      schemasPsiProcessingContext.ensureAllReferencesResolved();
      context.setMessages(schemasPsiProcessingContext.messages());
      return schema;
    });
  }

  static void failIfSearchFailure(final OperationSearchResult<? extends Operation<?, ?, ?>> oss) {
    if (oss instanceof OperationSearchFailure) {
      StringBuilder msg = new StringBuilder("Operation matching failed.\n");

      OperationSearchFailure<? extends Operation<?, ?, ?>> failure =
          (OperationSearchFailure<? extends Operation<?, ?, ?>>) oss;
      for (final Map.Entry<? extends Operation<?, ?, ?>, List<PsiProcessingMessage>> entry : failure.errors()
          .entrySet()) {
        final Operation<?, ?, ?> op = entry.getKey();
        msg.append("\nOperation defined at ").append(op.declaration().location()).append(" errors:\n");
        for (final PsiProcessingMessage error : entry.getValue()) {
          msg.append(error).append("\n");
        }
      }

      fail(msg.toString());
    }
  }

  static @NotNull UrlUrl parseNonReadUrl(@NotNull String url) {
    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    UrlUrl urlPsi = EpigraphPsiUtil.parseText(
        url,
        UrlSubParserDefinitions.URL,
        errorsAccumulator
    );

    failIfHasErrors(urlPsi, errorsAccumulator);

    return urlPsi;
  }
}
