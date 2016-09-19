package io.epigraph.projections.op.input;

import com.intellij.psi.PsiElement;
import io.epigraph.data.ListDatum;
import io.epigraph.data.PrimitiveDatum;
import io.epigraph.data.RecordDatum;
import io.epigraph.gdata.*;
import io.epigraph.idl.gdata.IdlGDataPsiParser;
import io.epigraph.idl.parser.psi.*;
import io.epigraph.lang.Fqn;
import io.epigraph.psi.PsiProcessingException;
import io.epigraph.types.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.LinkedHashSet;
import java.util.List;

import static io.epigraph.projections.ProjectionPsiParserUtil.getTag;
import static io.epigraph.projections.ProjectionPsiParserUtil.getType;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpInputProjectionsPsiParser {
  // todo custom parameters support

  public static OpInputVarProjection parseVarProjection(@NotNull DataType dataType,
                                                        @NotNull IdlOpInputVarProjection psi,
                                                        @NotNull TypesResolver typesResolver)
      throws PsiProcessingException {

    final Type type = dataType.type;
    final LinkedHashSet<OpInputTagProjection> tagProjections = new LinkedHashSet<>();

    @Nullable IdlOpInputSingleTagProjection singleTagProjectionPsi = psi.getOpInputSingleTagProjection();
    if (singleTagProjectionPsi != null) {
      final OpInputModelProjection<?, ?> parsedModelProjection;
      final Type.Tag tag = getTag(
          type,
          singleTagProjectionPsi.getOpTagName(),
          dataType.defaultTag,
          singleTagProjectionPsi
      );

      @Nullable IdlOpInputModelProjection modelProjection = singleTagProjectionPsi.getOpInputModelProjection();
      assert modelProjection != null; // todo when it can be null?

      parsedModelProjection = parseModelProjection(
          tag.type,
          singleTagProjectionPsi.getPlus() != null,
          getModelDefaultValue(singleTagProjectionPsi.getOpInputModelPropertyList()),
          modelProjection, typesResolver
      );

      tagProjections.add(new OpInputTagProjection(tag, parsedModelProjection));
    } else {
      @Nullable IdlOpInputMultiTagProjection multiTagProjection = psi.getOpInputMultiTagProjection();
      assert multiTagProjection != null;
      // parse list of tags
      @NotNull List<IdlOpInputMultiTagProjectionItem> tagProjectionPsiList =
          multiTagProjection.getOpInputMultiTagProjectionItemList();

      for (IdlOpInputMultiTagProjectionItem tagProjectionPsi : tagProjectionPsiList) {
        final Type.Tag tag = getTag(type, tagProjectionPsi.getOpTagName(), dataType.defaultTag, tagProjectionPsi);

        final OpInputModelProjection<?, ?> parsedModelProjection;

        @NotNull DatumType tagType = tag.type;
        @Nullable IdlOpInputModelProjection modelProjection = tagProjectionPsi.getOpInputModelProjection();
        assert modelProjection != null; // todo when it can be null?

        parsedModelProjection = parseModelProjection(
            tagType,
            tagProjectionPsi.getPlus() != null,
            getModelDefaultValue(tagProjectionPsi.getOpInputModelPropertyList()),
            modelProjection, typesResolver
        );

        tagProjections.add(new OpInputTagProjection(tag, parsedModelProjection));
      }
    }

    // parse tails
    final LinkedHashSet<OpInputVarProjection> tails;
    @Nullable IdlOpInputVarPolymorphicTail psiTail = psi.getOpInputVarPolymorphicTail();
    if (psiTail != null) {
      tails = new LinkedHashSet<>();

      @Nullable IdlOpInputVarSingleTail singleTail = psiTail.getOpInputVarSingleTail();
      if (singleTail != null) {
        @NotNull IdlFqnTypeRef tailTypeRef = singleTail.getFqnTypeRef();
        @NotNull IdlOpInputVarProjection psiTailProjection = singleTail.getOpInputVarProjection();
        @NotNull OpInputVarProjection tailProjection =
            buildTailProjection(dataType, tailTypeRef, psiTailProjection, typesResolver, singleTail);
        tails.add(tailProjection);
      } else {
        @Nullable IdlOpInputVarMultiTail multiTail = psiTail.getOpInputVarMultiTail();
        assert multiTail != null;
        for (IdlOpInputVarMultiTailItem tailItem : multiTail.getOpInputVarMultiTailItemList()) {
          @NotNull IdlFqnTypeRef tailTypeRef = tailItem.getFqnTypeRef();
          @NotNull IdlOpInputVarProjection psiTailProjection = tailItem.getOpInputVarProjection();
          @NotNull OpInputVarProjection tailProjection =
              buildTailProjection(dataType, tailTypeRef, psiTailProjection, typesResolver, tailItem);
          tails.add(tailProjection);
        }
      }

    } else tails = null;

    return new OpInputVarProjection(type, tagProjections, tails);
  }

  @Nullable
  private static GDataVarValue getModelDefaultValue(@NotNull List<IdlOpInputModelProperty> modelProperties)
      throws PsiProcessingException {

    GDataVarValue result = null;
    for (IdlOpInputModelProperty property : modelProperties) {
      @Nullable IdlOpInputDefaultValue defaultValuePsi = property.getOpInputDefaultValue();
      if (defaultValuePsi != null) {
        if (result != null)
          throw new PsiProcessingException("Default value should only be specified once", defaultValuePsi);

        @Nullable IdlVarValue varValuePsi = defaultValuePsi.getVarValue();
        if (varValuePsi != null)
          result = IdlGDataPsiParser.parseVarValue(varValuePsi);
      }
    }

    return result;
  }

  @NotNull
  private static OpInputVarProjection buildTailProjection(@NotNull DataType dataType,
                                                          IdlFqnTypeRef tailTypeRef,
                                                          IdlOpInputVarProjection psiTailProjection,
                                                          @NotNull TypesResolver typesResolver,
                                                          PsiElement location)
      throws PsiProcessingException {

    @NotNull Fqn typeFqn = tailTypeRef.getFqn().getFqn();
    @NotNull Type tailType = getType(typesResolver, typeFqn, location);
    return parseVarProjection(
        new DataType(dataType.polymorphic, tailType, dataType.defaultTag),
        psiTailProjection,
        typesResolver
    );
  }

  @NotNull
  private static OpInputVarProjection createDefaultVarProjection(@NotNull Type type,
                                                                 @NotNull Type.Tag tag,
                                                                 boolean required,
                                                                 @NotNull PsiElement location)
      throws PsiProcessingException {
    return new OpInputVarProjection(
        type,
        new OpInputTagProjection(
            tag,
            createDefaultModelProjection(tag.type, required, location)
        )
    );
  }

  @NotNull
  private static OpInputVarProjection createDefaultVarProjection(@NotNull DatumType type,
                                                                 boolean required,
                                                                 @NotNull PsiElement location)
      throws PsiProcessingException {
    return createDefaultVarProjection(type, type.self, required, location);
  }

  @NotNull
  private static OpInputVarProjection createDefaultVarProjection(@NotNull DataType type,
                                                                 boolean required,
                                                                 @NotNull PsiElement location)
      throws PsiProcessingException {

    @Nullable Type.Tag defaultTag = type.defaultTag;
    if (defaultTag == null)
      throw new PsiProcessingException(
          String.format("Can't build default projection for '%s', default tag not specified", type.name), location
      );

    return createDefaultVarProjection(type.type, defaultTag, required, location);
  }

  @NotNull
  public static OpInputModelProjection<?, ?> parseModelProjection(@NotNull DatumType type,
                                                                  boolean required,
                                                                  @Nullable GDataVarValue defaultValue,
                                                                  @NotNull IdlOpInputModelProjection psi,
                                                                  @NotNull TypesResolver typesResolver)
      throws PsiProcessingException {

    switch (type.kind()) {
      case RECORD:
        @Nullable IdlOpInputRecordModelProjection recordModelProjectionPsi = psi.getOpInputRecordModelProjection();
        if (recordModelProjectionPsi == null)
          return createDefaultModelProjection(type, required, psi);
        ensureModelKind(psi, TypeKind.RECORD);
        GDataRecord defaultRecordData = coerceDefault(defaultValue, GDataRecord.class, psi);

        return parseRecordModelProjection(
            (RecordType) type,
            required,
            defaultRecordData,
            recordModelProjectionPsi,
            typesResolver
        );
      case LIST:
        @Nullable IdlOpInputListModelProjection listModelProjectionPsi = psi.getOpInputListModelProjection();
        if (listModelProjectionPsi == null)
          return createDefaultModelProjection(type, required, psi);
        ensureModelKind(psi, TypeKind.LIST);
        GDataList defaultListData = coerceDefault(defaultValue, GDataList.class, psi);

        return parseListModelProjection(
            (ListType) type,
            required,
            defaultListData,
            listModelProjectionPsi,
            typesResolver
        );
      case MAP:
        @Nullable IdlOpInputMapModelProjection mapModelProjectionPsi = psi.getOpInputMapModelProjection();
        if (mapModelProjectionPsi == null)
          return createDefaultModelProjection(type, required, psi);
        ensureModelKind(psi, TypeKind.MAP);
        throw new PsiProcessingException("Unsupported type kind: " + type.kind(), psi);
      case ENUM:
        throw new PsiProcessingException("Unsupported type kind: " + type.kind(), psi);
      case PRIMITIVE:
        GDataPrimitive defaultPrimitiveData = coerceDefault(defaultValue, GDataPrimitive.class, psi);
        return parsePrimitiveModelProjection(
            (PrimitiveType) type,
            required,
            defaultPrimitiveData,
            psi,
            typesResolver
        );
      case UNION:
        throw new PsiProcessingException("Unsupported type kind: " + type.kind(), psi);
      default:
        throw new PsiProcessingException("Unknown type kind: " + type.kind(), psi);
    }
  }

  private static void ensureModelKind(@NotNull IdlOpInputModelProjection psi, @NotNull TypeKind expectedKind)
      throws PsiProcessingException {

    @Nullable TypeKind actualKind = findProjectionKind(psi);
    if (!expectedKind.equals(actualKind))
      throw new PsiProcessingException(MessageFormat.format("Unexpected projection kind ''{0}'', expected ''{1}''",
                                                            actualKind,
                                                            expectedKind
      ), psi);
  }

  @Nullable
  private static TypeKind findProjectionKind(@NotNull IdlOpInputModelProjection psi) {
    if (psi.getOpInputRecordModelProjection() != null) return TypeKind.RECORD;
    if (psi.getOpInputMapModelProjection() != null) return TypeKind.MAP;
    if (psi.getOpInputListModelProjection() != null) return TypeKind.LIST;
    return null;
  }

  @NotNull
  private static OpInputModelProjection<?, ?> createDefaultModelProjection(@NotNull DatumType type,
                                                                           boolean required,
                                                                           @NotNull PsiElement location)
      throws PsiProcessingException {

    switch (type.kind()) {
      case RECORD:
        return new OpInputRecordModelProjection((RecordType) type,
                                                required,
                                                null,
                                                null
        );
      case LIST:
        ListType listType = (ListType) type;
        @NotNull DataType elementType = listType.elementType();
        Type.@Nullable Tag defaultTag = elementType.defaultTag;

        if (defaultTag == null)
          throw new PsiProcessingException(String.format(
              "Can't create default projection for list type '%s, as it's element type '%s' doesn't have a default tag",
              type.name(),
              elementType.name
          ), location);

        final OpInputVarProjection itemVarProjection = createDefaultVarProjection(
            elementType.type,
            defaultTag,
            required,
            location
        );

        return new OpInputListModelProjection(listType,
                                              required,
                                              null,
                                              itemVarProjection
        );
      case MAP:
        // todo
        throw new PsiProcessingException("Unsupported type kind: " + type.kind(), location);
      case UNION:
        throw new PsiProcessingException("Was expecting to get datum model kind, got: " + type.kind(), location);
      case ENUM:
        // todo
        throw new PsiProcessingException("Unsupported type kind: " + type.kind(), location);
      case PRIMITIVE:
        return new OpInputPrimitiveModelProjection((PrimitiveType) type, required, null);
      default:
        throw new PsiProcessingException("Unknown type kind: " + type.kind(), location);
    }
  }

  @NotNull
  public static OpInputRecordModelProjection parseRecordModelProjection(@NotNull RecordType type,
                                                                        boolean required,
                                                                        @Nullable GDataRecord defaultValue,
                                                                        @NotNull IdlOpInputRecordModelProjection psi,
                                                                        @NotNull TypesResolver resolver)
      throws PsiProcessingException {

    RecordDatum defaultDatum = null;
    if (defaultValue != null) {
      try {
        defaultDatum = GDataToData.transform(type, defaultValue, resolver);
      } catch (GDataToData.ProcessingException e) {
        throw new PsiProcessingException(e, psi);
      }
    }

    LinkedHashSet<OpInputFieldProjection> fieldProjections = new LinkedHashSet<>();
    @NotNull List<IdlOpInputFieldProjection> psiFieldProjections = psi.getOpInputFieldProjectionList();

    for (IdlOpInputFieldProjection fieldProjectionPsi : psiFieldProjections) {
      final String fieldName = fieldProjectionPsi.getQid().getCanonicalName();
      RecordType.Field field = type.fieldsMap().get(fieldName);
      if (field == null)
        throw new PsiProcessingException(
            String.format("Can't field projection for '%s', field '%s' not found", type.name(), fieldName),
            fieldProjectionPsi
        );

      final boolean fieldRequired = fieldProjectionPsi.getPlus() != null;

      for (IdlOpInputFieldProjectionBodyPart fieldBodyPart : fieldProjectionPsi.getOpInputFieldProjectionBodyPartList()) {
        @NotNull IdlCustomParam customParam = fieldBodyPart.getCustomParam();
        if (customParam != null) {
          // todo
        }
      }


      OpInputVarProjection varProjection;

      @Nullable IdlOpInputVarProjection psiVarProjection = fieldProjectionPsi.getOpInputVarProjection();
      if (psiVarProjection == null) {
        @NotNull DataType fieldDataType = field.dataType();
        @Nullable Type.Tag defaultFieldTag = fieldDataType.defaultTag;
        if (defaultFieldTag == null)
          throw new PsiProcessingException(String.format(
              "Can't construct default projection for field '%s', as it's type '%s' has no default tag",
              fieldName,
              fieldDataType.name
          ), fieldProjectionPsi);

        varProjection =
            createDefaultVarProjection(fieldDataType.type, defaultFieldTag, required, fieldProjectionPsi);
      } else {
        varProjection = parseVarProjection(field.dataType(), psiVarProjection, resolver);
      }

      fieldProjections.add(new OpInputFieldProjection(field, varProjection, fieldRequired));
    }

    final LinkedHashSet<OpInputRecordModelProjection> tail;

    return new OpInputRecordModelProjection(type, required, defaultDatum, fieldProjections);
  }

  @NotNull
  public static OpInputListModelProjection parseListModelProjection(@NotNull ListType type,
                                                                    boolean required,
                                                                    @Nullable GDataList defaultValue,
                                                                    @NotNull IdlOpInputListModelProjection psi,
                                                                    @NotNull TypesResolver resolver)
      throws PsiProcessingException {

    ListDatum defaultDatum = null;
    if (defaultValue != null) {
      try {
        defaultDatum = GDataToData.transform(type, defaultValue, resolver);
      } catch (GDataToData.ProcessingException e) {
        throw new PsiProcessingException(e, psi);
      }
    }

    OpInputVarProjection itemsProjection;
    @Nullable IdlOpInputVarProjection opInputVarProjectionPsi = psi.getOpInputVarProjection();
    if (opInputVarProjectionPsi == null)
      itemsProjection = createDefaultVarProjection(type, true, psi);
    else
      itemsProjection = parseVarProjection(type.elementType(), opInputVarProjectionPsi, resolver);


    return new OpInputListModelProjection(
        type,
        required,
        defaultDatum,
        itemsProjection
    );
  }

  @NotNull
  public static OpInputPrimitiveModelProjection parsePrimitiveModelProjection(@NotNull PrimitiveType type,
                                                                              boolean required,
                                                                              @Nullable GDataPrimitive defaultValue,
                                                                              @NotNull PsiElement location,
                                                                              @NotNull TypesResolver resolver)
      throws PsiProcessingException {

    PrimitiveDatum<?> defaultDatum = null;
    if (defaultValue != null) {
      try {
        defaultDatum = GDataToData.transform(type, defaultValue, resolver);
      } catch (GDataToData.ProcessingException e) {
        throw new PsiProcessingException(e, location);
      }
    }
    return new OpInputPrimitiveModelProjection(type, required, defaultDatum);
  }

  @Nullable
  private static <D extends GDataVarValue> D coerceDefault(@Nullable GDataVarValue defaultValue,
                                                           Class<D> cls,
                                                           @NotNull PsiElement location)
      throws PsiProcessingException {

    if (defaultValue == null) return null;
    if (defaultValue instanceof GDataNull) return null;
    if (defaultValue.getClass().equals(cls)) //noinspection unchecked
      return (D) defaultValue;
    throw new PsiProcessingException(
        String.format("Invalid default value '%s', expected to get '%s'", defaultValue, cls.getName()),
        location
    );
  }
}