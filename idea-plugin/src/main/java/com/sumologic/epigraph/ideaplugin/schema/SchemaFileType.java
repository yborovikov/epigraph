package com.sumologic.epigraph.ideaplugin.schema;

import com.intellij.icons.AllIcons;
import com.intellij.lang.Language;
import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Todo add doc
 *
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaFileType extends LanguageFileType {
  public static final SchemaFileType INSTANCE = new SchemaFileType();
  public static final String DEFAULT_EXTENSION = "es";

  protected SchemaFileType() {
    super(SchemaLanguage.INSTANCE); // TODO
  }

  protected SchemaFileType(@NotNull Language language) {
    super(language);
  }

  @NotNull
  @Override
  public String getName() {
    return "epi_schema";
  }

  @NotNull
  @Override
  public String getDescription() {
    return "Epigraph Schema";
  }

  @NotNull
  @Override
  public String getDefaultExtension() {
    return DEFAULT_EXTENSION;
  }

  @Nullable
  @Override
  public Icon getIcon() {
    return AllIcons.FileTypes.Properties;
  }
}