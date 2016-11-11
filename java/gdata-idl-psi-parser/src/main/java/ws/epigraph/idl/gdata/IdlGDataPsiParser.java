/*
 * Copyright 2016 Sumo Logic
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

package ws.epigraph.idl.gdata;

import ws.epigraph.gdata.*;
import ws.epigraph.idl.TypeRefs;
import ws.epigraph.idl.parser.psi.*;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingError;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.TypeRef;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class IdlGDataPsiParser {
  @NotNull
  public static GDataValue parseValue(@NotNull IdlDataValue psi, @NotNull List<PsiProcessingError> errors)
      throws PsiProcessingException {
    if (psi.getData() != null) return parseData(psi.getData(), errors);
    else if (psi.getDatum() != null) return parseDatum(psi.getDatum(), errors);
    else throw new PsiProcessingException("Neither data nor datum is set", psi, errors);
  }

  @NotNull
  public static GData parseData(@NotNull IdlData psi, @NotNull List<PsiProcessingError> errors)
      throws PsiProcessingException {
    @Nullable IdlTypeRef typeRef = psi.getTypeRef();

    LinkedHashMap<String, GDatum> tags = new LinkedHashMap<>();
    for (IdlDataEntry entry : psi.getDataEntryList()) {
      @Nullable IdlDatum value = entry.getDatum();
      if (value != null)
        tags.put(entry.getQid().getCanonicalName(), parseDatum(value, errors));
      else
        throw new PsiProcessingException(
            String.format("Got 'null' value for tag '%s'", entry.getQid().getCanonicalName()), psi, errors
        );
    }

    return new GData(getTypeRef(typeRef), tags, EpigraphPsiUtil.getLocation(psi));
  }

  @NotNull
  public static GDatum parseDatum(@NotNull IdlDatum psi, @NotNull List<PsiProcessingError> errors)
      throws PsiProcessingException {

    if (psi instanceof IdlRecordDatum)
      return parseRecord((IdlRecordDatum) psi, errors);
    else if (psi instanceof IdlMapDatum)
      return parseMap((IdlMapDatum) psi, errors);
    else if (psi instanceof IdlListDatum)
      return parseList((IdlListDatum) psi, errors);
    else if (psi instanceof IdlEnumDatum)
      return parseEnum((IdlEnumDatum) psi);
    else if (psi instanceof IdlPrimitiveDatum)
      return parsePrimitive((IdlPrimitiveDatum) psi, errors);
    else if (psi instanceof IdlNullDatum)
      return parseNull((IdlNullDatum) psi);
    else throw new PsiProcessingException("Unknown value element", psi, errors);
  }

  @NotNull
  public static GRecordDatum parseRecord(@NotNull IdlRecordDatum psi, @NotNull List<PsiProcessingError> errors)
      throws PsiProcessingException {
    @Nullable IdlTypeRef typeRef = psi.getTypeRef();

    LinkedHashMap<String, GDataValue> fields = new LinkedHashMap<>();
    for (IdlRecordDatumEntry entry : psi.getRecordDatumEntryList()) {
      @Nullable IdlDataValue value = entry.getDataValue();
      if (value != null)
        fields.put(entry.getQid().getCanonicalName(), parseValue(value, errors));
      else
        errors.add(new PsiProcessingError(
            String.format("Got 'null' value for field '%s'", entry.getQid().getCanonicalName()), psi
        ));
    }

    return new GRecordDatum(getTypeRef(typeRef), fields, EpigraphPsiUtil.getLocation(psi));
  }

  @NotNull
  public static GMapDatum parseMap(@NotNull IdlMapDatum psi, @NotNull List<PsiProcessingError> errors)
      throws PsiProcessingException {
    @Nullable IdlTypeRef typeRef = psi.getTypeRef();

    LinkedHashMap<GDatum, GDataValue> map = new LinkedHashMap<>();
    for (IdlMapDatumEntry entry : psi.getMapDatumEntryList()) {
      @Nullable IdlDataValue dataValue = entry.getDataValue();
      if (dataValue != null)
        map.put(parseDatum(entry.getDatum(), errors), parseValue(dataValue, errors));
      else
        errors.add(new PsiProcessingError(
            String.format("Got 'null' value for key '%s'", entry.getDataValue().getText()), psi
        ));
    }

    return new GMapDatum(getTypeRef(typeRef), map, EpigraphPsiUtil.getLocation(psi));
  }

  @NotNull
  public static GListDatum parseList(@NotNull IdlListDatum psi, @NotNull List<PsiProcessingError> errors)
      throws PsiProcessingException {

    @Nullable IdlTypeRef typeRef = psi.getTypeRef();

    final List<GDataValue> items = new ArrayList<>();

    for (IdlDataValue value : psi.getDataValueList())
      items.add(parseValue(value, errors));

    return new GListDatum(getTypeRef(typeRef), items, EpigraphPsiUtil.getLocation(psi));
  }

  @NotNull
  public static GDataEnum parseEnum(@NotNull IdlEnumDatum psi) {
    return new GDataEnum(psi.getQid().getCanonicalName(), EpigraphPsiUtil.getLocation(psi));
  }

  @NotNull
  public static GPrimitiveDatum parsePrimitive(@NotNull IdlPrimitiveDatum psi, @NotNull List<PsiProcessingError> errors)
      throws PsiProcessingException {

    @Nullable IdlTypeRef typeRef = psi.getTypeRef();

    final Object value;
    if (psi.getString() != null) {
      String text = psi.getString().getText();
      value = text.substring(1, text.length() - 1);
    } else if (psi.getBoolean() != null) {
      value = Boolean.valueOf(psi.getBoolean().getText());
    } else if (psi.getNumber() != null) {
      // todo make it stable and always parse as Double?
      String text = psi.getNumber().getText();
      if (text.contains(".")) value = Double.valueOf(text);
      else value = Long.valueOf(text);
    } else
      throw new PsiProcessingException(
          String.format("Don't know how to handle primitive '%s'", psi.getText()),
          psi,
          errors
      );

    return new GPrimitiveDatum(getTypeRef(typeRef), value, EpigraphPsiUtil.getLocation(psi));
  }

  @NotNull
  public static GNullDatum parseNull(@NotNull IdlNullDatum psi) throws PsiProcessingException {
    @Nullable IdlTypeRef typeRef = psi.getTypeRef();
    return new GNullDatum(getTypeRef(typeRef), EpigraphPsiUtil.getLocation(psi));
  }

  @Nullable
  private static TypeRef getTypeRef(IdlTypeRef typeRef) throws PsiProcessingException {
    return typeRef == null ? null : TypeRefs.fromPsi(typeRef);
  }
}
