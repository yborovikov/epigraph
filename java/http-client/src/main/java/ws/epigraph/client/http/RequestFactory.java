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

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.impl.DebugUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.data.Data;
import ws.epigraph.lang.MessagesContext;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.ProjectionUtils;
import ws.epigraph.projections.StepsAndProjection;
import ws.epigraph.projections.gen.ProjectionReferenceName;
import ws.epigraph.projections.op.OpFieldProjection;
import ws.epigraph.projections.op.OpProjection;
import ws.epigraph.projections.req.ReqEntityProjection;
import ws.epigraph.projections.req.ReqFieldProjection;
import ws.epigraph.projections.req.ReqProjection;
import ws.epigraph.psi.*;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.schema.operations.*;
import ws.epigraph.service.operations.*;
import ws.epigraph.types.DataTypeApi;
import ws.epigraph.types.TypeApi;
import ws.epigraph.url.parser.UrlSubParserDefinitions;
import ws.epigraph.url.parser.psi.UrlReqComaEntityProjection;
import ws.epigraph.url.parser.psi.UrlReqEntityPath;
import ws.epigraph.url.parser.psi.UrlReqTrunkFieldProjection;
import ws.epigraph.url.parser.psi.UrlReqTrunkEntityProjection;
import ws.epigraph.url.projections.req.ReqPsiProcessingContext;
import ws.epigraph.url.projections.req.delete.ReqDeleteProjectionPsiParser;
import ws.epigraph.url.projections.req.input.ReqInputProjectionPsiParser;
import ws.epigraph.url.projections.req.output.ReqOutputProjectionPsiParser;
import ws.epigraph.url.projections.req.ReqProjectionPsiParser;
import ws.epigraph.url.projections.req.ReqReferenceContext;
import ws.epigraph.url.projections.req.path.ReqPartialPathParsingResult;
import ws.epigraph.url.projections.req.path.ReqPartialPathPsiParser;
import ws.epigraph.url.projections.req.path.ReqPathPsiParser;
import ws.epigraph.url.projections.req.path.ReqPathPsiProcessingContext;
import ws.epigraph.url.projections.req.update.ReqUpdateProjectionPsiParser;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class RequestFactory {
  private static final Function<ReqProjection<?,?>, ReqFieldProjection>
      ENTITY_TO_FIELD = p -> new ReqFieldProjection(p, p.location());

  private RequestFactory() {}

  /**
   * Constructs read request from a string
   *
   * @param resourceType         resource field type
   * @param operationDeclaration target operation declaration
   * @param requestString        request projection string
   * @param typesResolver        types resolver
   *
   * @return read request instance
   * @throws IllegalArgumentException if there was an error parsing {@code requestString}
   */
  public static @NotNull ReadOperationRequest constructReadRequest(
      @NotNull DataTypeApi resourceType,
      @NotNull ReadOperationDeclaration operationDeclaration,
      @NotNull String requestString,
      @NotNull TypesResolver typesResolver) throws IllegalArgumentException {

    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    UrlReqTrunkFieldProjection psi =
        EpigraphPsiUtil.parseText(
            requestString,
            UrlSubParserDefinitions.REQ_FIELD_PROJECTION,
            errorsAccumulator
        );

    throwErrors(psi, errorsAccumulator);

    final ReqFieldProjection reqPath;
    final StepsAndProjection<ReqFieldProjection> reqFieldProjection;

    PsiProcessingContext context = new DefaultPsiProcessingContext();
    ReqOutputProjectionPsiParser outputProjectionPsiParser = new ReqOutputProjectionPsiParser(context);

    OpFieldProjection opPath = operationDeclaration.path();

    try {
      if (opPath == null) {
        reqPath = null;
        ReqReferenceContext reqOutputReferenceContext =
            new ReqReferenceContext(ProjectionReferenceName.EMPTY, null, context);
        ReqPsiProcessingContext reqOutputPsiProcessingContext =
            new ReqPsiProcessingContext(context, reqOutputReferenceContext);
        StepsAndProjection<ReqFieldProjection> stepsAndProjection =
            outputProjectionPsiParser.parseTrunkFieldProjection(
                resourceType,
                false,  // ?
                operationDeclaration.outputProjection(),
                psi,
                typesResolver,
                reqOutputPsiProcessingContext
            );

        reqOutputReferenceContext.ensureAllReferencesResolved();

        reqFieldProjection = stepsAndProjection;
      } else {
        ReqPartialPathParsingResult<ReqFieldProjection> pathParsingResult = ReqPartialPathPsiParser.parseFieldPath(
            resourceType,
            opPath,
            psi,
            typesResolver,
            new ReqPathPsiProcessingContext(context)
        );

        reqPath = pathParsingResult.path();
        DataTypeApi pathTipType = ProjectionUtils.tipType(reqPath.projection());

        UrlReqTrunkEntityProjection trunkEntityProjectionPsi = pathParsingResult.trunkProjectionPsi();
        UrlReqComaEntityProjection comaEntityProjectionPsi = pathParsingResult.comaProjectionPsi();

        ReqReferenceContext reqOutputReferenceContext =
            new ReqReferenceContext(ProjectionReferenceName.EMPTY, null, context);
        ReqPsiProcessingContext reqOutputPsiProcessingContext =
            new ReqPsiProcessingContext(context, reqOutputReferenceContext);

        if (trunkEntityProjectionPsi != null) {
          StepsAndProjection<ReqProjection<?,?>> r = outputProjectionPsiParser.parseTrunkProjection(
              pathTipType,
              false,
              operationDeclaration.outputProjection().projection(),
              trunkEntityProjectionPsi,
              typesResolver,
              reqOutputPsiProcessingContext
          );

          reqFieldProjection = new StepsAndProjection<>(
              r.pathSteps(),
              new ReqFieldProjection(r.projection(), r.projection().location())
          );
        } else if (comaEntityProjectionPsi != null) {
          StepsAndProjection<ReqProjection<?,?>> r = outputProjectionPsiParser.parseComaProjection(
              pathTipType,
              false,
              operationDeclaration.outputProjection().projection(),
              comaEntityProjectionPsi,
              typesResolver,
              reqOutputPsiProcessingContext
          );
          reqFieldProjection = new StepsAndProjection<>(
              r.pathSteps(),
              new ReqFieldProjection(r.projection(), r.projection().location())
          );
        } else {
          ReqEntityProjection ep = new ReqEntityProjection(
              pathTipType.type(),
              false,
              Collections.emptyMap(),
              false,
              null,
              TextLocation.UNKNOWN
          );
          reqFieldProjection = new StepsAndProjection<>(0, new ReqFieldProjection(ep, ep.location()));
        }

        reqOutputReferenceContext.ensureAllReferencesResolved();
      }

      return new ReadOperationRequest(
          reqPath,
          reqFieldProjection
      );
    } catch (PsiProcessingException e) {
      context.setMessages(e.messages());
    }

    throw new IllegalArgumentException(dumpErrors(context.messages()));
  }

  /**
   * Constructs create request
   *
   * @param resourceType         resource field type
   * @param operationDeclaration target operation declaration
   * @param pathString           operation path string
   * @param inputRequestString   optional (nullable) request projection string
   * @param requestData          request data (body)
   * @param outputRequestString  output request projection string
   * @param typesResolver        types resolver
   *
   * @return create request instance
   * @throws IllegalArgumentException if there was an error parsing {@code requestString}
   */
  public static @NotNull CreateOperationRequest constructCreateRequest(
      @NotNull DataTypeApi resourceType,
      @NotNull CreateOperationDeclaration operationDeclaration,
      @Nullable String pathString,
      @Nullable String inputRequestString,
      @NotNull Data requestData,
      @NotNull String outputRequestString,
      @NotNull TypesResolver typesResolver) throws IllegalArgumentException {

    ReqFieldProjection reqFieldPath = null;

    if (pathString != null) {
      OpFieldProjection opPath = operationDeclaration.path();
      if (opPath == null)
        throw new IllegalArgumentException(
            String.format(
                "Request path specified while operation '%s' doesn't support it",
                operationDeclaration.nameOrDefaultName()
            )
        );

      ReqProjection<?,?> reqPath = parseReqPath(pathString, resourceType, opPath.projection(), typesResolver);
      reqFieldPath = new ReqFieldProjection(
          reqPath,
          TextLocation.UNKNOWN
      );

    }

    StepsAndProjection<ReqProjection<?,?>> outputStepsAndProjection = parseReqOutputProjection(
        outputRequestString,
        operationDeclaration.outputType().dataType(),
        operationDeclaration.outputProjection().projection(),
        typesResolver
    );

    StepsAndProjection<ReqProjection<?,?>> inputStepsAndProjection = null;
    if (inputRequestString != null) {
      inputStepsAndProjection = parseReqInputProjection(
          inputRequestString,
          true, // todo take from inputRequestString
          operationDeclaration.inputType().dataType(),
          operationDeclaration.inputProjection().projection(),
          typesResolver
      );
    }

    return new CreateOperationRequest(
        reqFieldPath,
        requestData,
        StepsAndProjection.wrapNullable(inputStepsAndProjection, ENTITY_TO_FIELD),
        outputStepsAndProjection.wrap(ENTITY_TO_FIELD)
    );
  }

  /**
   * Constructs update request
   *
   * @param resourceType         resource field type
   * @param operationDeclaration target operation declaration
   * @param pathString           operation path string
   * @param updateRequestString  optional (nullable) request projection string
   * @param requestData          request data (body)
   * @param outputRequestString  output request projection string
   * @param typesResolver        types resolver
   *
   * @return create request instance
   * @throws IllegalArgumentException if there was an error parsing {@code requestString}
   */
  public static @NotNull UpdateOperationRequest constructUpdateRequest(
      @NotNull DataTypeApi resourceType,
      @NotNull UpdateOperationDeclaration operationDeclaration,
      @Nullable String pathString,
      @Nullable String updateRequestString,
      @NotNull Data requestData,
      @NotNull String outputRequestString,
      @NotNull TypesResolver typesResolver) throws IllegalArgumentException {

    ReqFieldProjection reqFieldPath = null;

    if (pathString != null) {
      OpFieldProjection opPath = operationDeclaration.path();
      if (opPath == null)
        throw new IllegalArgumentException(
            String.format(
                "Request path specified while operation '%s' doesn't support it",
                operationDeclaration.nameOrDefaultName()
            )
        );

      ReqProjection<?,?> reqVarPath = parseReqPath(pathString, resourceType, opPath.projection(), typesResolver);
      reqFieldPath = new ReqFieldProjection(
          reqVarPath,
          TextLocation.UNKNOWN
      );
    }

    StepsAndProjection<ReqProjection<?,?>> outputStepsAndProjection = parseReqOutputProjection(
        outputRequestString,
        operationDeclaration.outputType().dataType(),
        operationDeclaration.outputProjection().projection(),
        typesResolver
    );

    StepsAndProjection<ReqProjection<?,?>> updateStepsAndProjection = null;
    if (updateRequestString != null) {
      // parse leading '+'
      boolean replace = updateRequestString.startsWith("+");

      updateStepsAndProjection = parseReqUpdateProjection(
          replace ? updateRequestString.substring(1) : updateRequestString,
          replace,
          operationDeclaration.inputType().dataType(),
          operationDeclaration.inputProjection().projection(),
          typesResolver
      );

    }

    return new UpdateOperationRequest(
        reqFieldPath,
        requestData,
        StepsAndProjection.wrapNullable(updateStepsAndProjection, ENTITY_TO_FIELD),
        outputStepsAndProjection.wrap(ENTITY_TO_FIELD)
    );
  }

  /**
   * Constructs delete request
   *
   * @param resourceType         resource field type
   * @param operationDeclaration target operation declaration
   * @param pathString           operation path string
   * @param deleteRequestString  optional (nullable) request projection string
   * @param outputRequestString  output request projection string
   * @param typesResolver        types resolver
   *
   * @return create request instance
   * @throws IllegalArgumentException if there was an error parsing {@code requestString}
   */
  public static @NotNull DeleteOperationRequest constructDeleteRequest(
      @NotNull DataTypeApi resourceType,
      @NotNull DeleteOperationDeclaration operationDeclaration,
      @Nullable String pathString,
      @NotNull String deleteRequestString,
      @NotNull String outputRequestString,
      @NotNull TypesResolver typesResolver) throws IllegalArgumentException {

    ReqFieldProjection reqFieldPath = null;

    if (pathString != null) {
      OpFieldProjection opPath = operationDeclaration.path();
      if (opPath == null)
        throw new IllegalArgumentException(
            String.format(
                "Request path specified while operation '%s' doesn't support it",
                operationDeclaration.nameOrDefaultName()
            )
        );

      ReqProjection<?,?> reqVarPath = parseReqPath(pathString, resourceType, opPath.projection(), typesResolver);
      reqFieldPath = new ReqFieldProjection(
          reqVarPath,
          TextLocation.UNKNOWN
      );
    }

    StepsAndProjection<ReqProjection<?,?>> outputStepsAndProjection = parseReqOutputProjection(
        outputRequestString,
        operationDeclaration.outputType().dataType(),
        operationDeclaration.outputProjection().projection(),
        typesResolver
    );

    ReqFieldProjection reqDeleteFieldProjection = new ReqFieldProjection(
        parseReqDeleteProjection(
            deleteRequestString,
            operationDeclaration.deleteProjection().projection().type().dataType(),
            operationDeclaration.deleteProjection().projection(),
            typesResolver
        ).projection(),
        TextLocation.UNKNOWN
    );

    return new DeleteOperationRequest(
        reqFieldPath,
        reqDeleteFieldProjection,
        outputStepsAndProjection.wrap(
            projection -> new ReqFieldProjection(projection, projection.location())
        )
    );
  }

  /**
   * Constructs custom operation request
   *
   * @param resourceType         resource field type
   * @param operationDeclaration target operation declaration
   * @param pathString           operation path string
   * @param inputRequestString   optional (nullable) request projection string
   * @param requestData          request data (body)
   * @param outputRequestString  output request projection string
   * @param typesResolver        types resolver
   *
   * @return create request instance
   * @throws IllegalArgumentException if there was an error parsing {@code requestString}
   */
  public static @NotNull CustomOperationRequest constructCustomRequest(
      @NotNull DataTypeApi resourceType,
      @NotNull CustomOperationDeclaration operationDeclaration,
      @Nullable String pathString,
      @Nullable String inputRequestString,
      @Nullable Data requestData,
      @NotNull String outputRequestString,
      @NotNull TypesResolver typesResolver) throws IllegalArgumentException {

    ReqFieldProjection reqFieldPath = null;

    if (pathString != null) {
      OpFieldProjection opPath = operationDeclaration.path();
      if (opPath == null)
        throw new IllegalArgumentException(
            String.format(
                "Request path specified while operation '%s' doesn't support it",
                operationDeclaration.nameOrDefaultName()
            )
        );

      ReqProjection<?,?> reqVarPath = parseReqPath(pathString, resourceType, opPath.projection(), typesResolver);
      reqFieldPath = new ReqFieldProjection(
          reqVarPath,
          TextLocation.UNKNOWN
      );
    }

    StepsAndProjection<ReqProjection<?,?>> outputStepsAndProjection = parseReqOutputProjection(
        outputRequestString,
        operationDeclaration.outputType().dataType(),
        operationDeclaration.outputProjection().projection(),
        typesResolver
    );

    StepsAndProjection<ReqProjection<?,?>> inputStepsAndProjection = null;
    if (inputRequestString != null) {
      OpFieldProjection opInputFieldProjection = operationDeclaration.inputProjection();
      if (opInputFieldProjection == null)
        throw new IllegalArgumentException(
            String.format(
                "Input projection specified while operation '%s' doesn't support it",
                operationDeclaration.nameOrDefaultName()
            )
        );

      TypeApi inputType = operationDeclaration.inputType();
      assert inputType != null;

      inputStepsAndProjection = parseReqInputProjection(
          inputRequestString,
          true, // todo take from inputRequestString
          inputType.dataType(),
          opInputFieldProjection.projection(),
          typesResolver
      );

    }

    return new CustomOperationRequest(
        reqFieldPath,
        requestData,
        StepsAndProjection.wrapNullable(inputStepsAndProjection, ENTITY_TO_FIELD),
        outputStepsAndProjection.wrap(ENTITY_TO_FIELD)
    );
  }


  private static @NotNull ReqProjection<?,?> parseReqPath(
      @NotNull String path,
      @NotNull DataTypeApi type,
      @NotNull OpProjection<?,?> op,
      @NotNull TypesResolver resolver) throws IllegalArgumentException {

    UrlReqEntityPath psi = getReqPathPsi(path);

    PsiProcessingContext context = new DefaultPsiProcessingContext();
    try {
      return ReqPathPsiParser.parsePath(
          op,
          type,
          psi,
          resolver,
          new ReqPathPsiProcessingContext(context)
      );
    } catch (PsiProcessingException e) {
      context.setMessages(e.messages());
    }

    throw new IllegalArgumentException(dumpErrors(context.messages()));
  }

  private static @NotNull UrlReqEntityPath getReqPathPsi(@NotNull String projectionString)
      throws IllegalArgumentException {

    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    UrlReqEntityPath psiVarPath = EpigraphPsiUtil.parseText(
        projectionString,
        UrlSubParserDefinitions.REQ_ENTITY_PATH,
        errorsAccumulator
    );

    throwErrors(psiVarPath, errorsAccumulator);

    return psiVarPath;
  }

  private static @NotNull StepsAndProjection<ReqProjection<?,?>> parseReqOutputProjection(
      @NotNull String projection,
      @NotNull DataTypeApi type,
      @NotNull OpProjection<?,?> op,
      @NotNull TypesResolver resolver) throws IllegalArgumentException {

    return parseReqProjection(ReqOutputProjectionPsiParser::new, false, projection, type, op, resolver);
  }

  private static @NotNull StepsAndProjection<ReqProjection<?,?>> parseReqInputProjection(
      @NotNull String projection,
      boolean required,
      @NotNull DataTypeApi type,
      @NotNull OpProjection<?,?> op,
      @NotNull TypesResolver resolver) throws IllegalArgumentException {

    return parseReqProjection(
        mc -> new ReqInputProjectionPsiParser(false, mc),
        required,
        projection,
        type,
        op,
        resolver
    );
  }

  private static @NotNull StepsAndProjection<ReqProjection<?,?>> parseReqUpdateProjection(
      @NotNull String projection,
      boolean replace,
      @NotNull DataTypeApi dataType,
      @NotNull OpProjection<?,?> op,
      @NotNull TypesResolver resolver) throws IllegalArgumentException {

    return parseReqProjection(
        mc -> new ReqUpdateProjectionPsiParser(false, mc),
        replace,
        projection,
        dataType,
        op,
        resolver
    );
  }

  private static @NotNull StepsAndProjection<ReqProjection<?,?>> parseReqDeleteProjection(
      @NotNull String projection,
      @NotNull DataTypeApi type,
      @NotNull OpProjection<?,?> op,
      @NotNull TypesResolver resolver) throws IllegalArgumentException {

    return parseReqProjection(ReqDeleteProjectionPsiParser::new, false, projection, type, op, resolver);
  }

  private static @NotNull StepsAndProjection<ReqProjection<?,?>> parseReqProjection(
      @NotNull Function<MessagesContext, ReqProjectionPsiParser> parserFactory,
      boolean flagged,
      @NotNull String projection,
      @NotNull DataTypeApi type,
      @NotNull OpProjection<?,?> op,
      @NotNull TypesResolver resolver) throws IllegalArgumentException {

    UrlReqTrunkEntityProjection psi = getReqOutputProjectionPsi(projection);

    PsiProcessingContext context = new DefaultPsiProcessingContext();
    ReqReferenceContext referenceContext =
        new ReqReferenceContext(ProjectionReferenceName.EMPTY, null, context);
    ReqPsiProcessingContext reqOutputPsiProcessingContext =
        new ReqPsiProcessingContext(context, referenceContext);

    try {
      ReqProjectionPsiParser parser = parserFactory.apply(context);
      @NotNull StepsAndProjection<ReqProjection<?,?>> res =
          parser.parseTrunkProjection(
              type,
              flagged,
              op,
              psi,
              resolver,
              reqOutputPsiProcessingContext
          );

      referenceContext.ensureAllReferencesResolved();
      throwErrors(context.messages());

      return res;

    } catch (PsiProcessingException e) {
      context.setMessages(e.messages());
    }


    throw new IllegalArgumentException(dumpErrors(context.messages()));
  }

  private static @NotNull UrlReqTrunkEntityProjection getReqOutputProjectionPsi(@NotNull String projection)
      throws IllegalArgumentException {

    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    UrlReqTrunkEntityProjection psi = EpigraphPsiUtil.parseText(
        projection,
        UrlSubParserDefinitions.REQ_ENTITY_PROJECTION,
        errorsAccumulator
    );

    throwErrors(psi, errorsAccumulator);

    return psi;
  }


  private static void throwErrors(
      @NotNull PsiElement psi,
      @NotNull EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator) throws IllegalArgumentException {

    String errors = dumpErrors(psi, errorsAccumulator);
    if (errors != null)
      throw new IllegalArgumentException(errors);
  }

  private static @Nullable String dumpErrors(
      @NotNull PsiElement psi,
      @NotNull EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator) {

    String errorsDump = dumpErrors(psiErrorsToPsiProcessingErrors(errorsAccumulator.errors()));

    if (errorsDump == null)
      return null;

    String psiDump = DebugUtil.psiToString(psi, true, false).trim();
    return "\n" + psi.getText() + "\n\n" + errorsDump + "\nPSI Dump:\n\n" + psiDump;
  }

  private static void throwErrors(@NotNull List<PsiProcessingMessage> errors) {
    String dump = dumpErrors(errors);
    if (dump != null)
      throw new IllegalArgumentException(dump);
  }

  private static @Nullable String dumpErrors(final List<PsiProcessingMessage> errors) {
    if (!errors.isEmpty()) {
      StringBuilder sb = new StringBuilder();
      for (final PsiProcessingMessage error : errors)
        sb.append(error.location()).append(": ").append(error.message()).append("\n");

      return sb.toString();
    }

    return null;
  }

  private static @NotNull List<PsiProcessingMessage> psiErrorsToPsiProcessingErrors(@NotNull List<PsiErrorElement> errors) {
    return errors.stream()
        .map(e -> new PsiProcessingMessage(PsiProcessingMessage.Level.ERROR, e.getErrorDescription(), e))
        .collect(Collectors.toList());
  }
}
