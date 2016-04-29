package com.sumologic.epigraph.ideaPlugin.schema.highlighting;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import com.sumologic.epigraph.ideaPlugin.schema.lexer.SchemaFlexAdapter;
import com.sumologic.epigraph.ideaPlugin.schema.parser.SchemaParserDefinition;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static com.sumologic.epigraph.ideaPlugin.schema.lexer.SchemaElementTypes.*;


/**
 * Todo add doc
 *
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaSyntaxHighlighter extends SyntaxHighlighterBase {
  // TODO provide customizable color scheme?
  // see also http://www.jetbrains.org/intellij/sdk/docs/reference_guide/color_scheme_management.html
  // http://www.jetbrains.org/intellij/sdk/docs/reference_guide/custom_language_support/syntax_highlighting_and_error_highlighting.html (bottom)
  // http://www.jetbrains.org/intellij/sdk/docs/tutorials/custom_language_support/syntax_highlighter_and_color_settings_page.html

  public static final TextAttributesKey ID = DefaultLanguageHighlighterColors.IDENTIFIER;
  public static final TextAttributesKey COMMA = DefaultLanguageHighlighterColors.COMMA;
  public static final TextAttributesKey PLUS = DefaultLanguageHighlighterColors.OPERATION_SIGN; // ??
  public static final TextAttributesKey LINE_COMMENT = DefaultLanguageHighlighterColors.LINE_COMMENT;
  public static final TextAttributesKey BLOCK_COMMENT = DefaultLanguageHighlighterColors.BLOCK_COMMENT;
  public static final TextAttributesKey STRING = DefaultLanguageHighlighterColors.STRING;
  public static final TextAttributesKey CURLY_BR = DefaultLanguageHighlighterColors.BRACES;
  public static final TextAttributesKey BRACKETS = DefaultLanguageHighlighterColors.BRACKETS;
  public static final TextAttributesKey KEYWORDS = DefaultLanguageHighlighterColors.KEYWORD;

  public static final TextAttributesKey FIELD = DefaultLanguageHighlighterColors.INSTANCE_FIELD;
  public static final TextAttributesKey TAG = DefaultLanguageHighlighterColors.INSTANCE_FIELD;
  public static final TextAttributesKey MULTI_MEMBER = DefaultLanguageHighlighterColors.INSTANCE_FIELD;

  public static final TextAttributesKey DECL_TYPE_NAME = DefaultLanguageHighlighterColors.CLASS_NAME;
  public static final TextAttributesKey TYPE_REF = DefaultLanguageHighlighterColors.CLASS_REFERENCE;

  public static final TextAttributesKey PARAM_NAME = DefaultLanguageHighlighterColors.METADATA;

  private static final Map<IElementType, TextAttributesKey> keys;


  static {
    keys = new HashMap<>();
    keys.put(S_ID, ID);
    keys.put(S_COMMA, COMMA);
    keys.put(S_PLUS, PLUS);
    keys.put(S_COMMENT, LINE_COMMENT);
    keys.put(S_BLOCK_COMMENT, BLOCK_COMMENT);
    keys.put(S_STRING, STRING);
    add(BRACKETS, S_BRACKET_LEFT, S_BRACKET_RIGHT);
    add(CURLY_BR, SchemaParserDefinition.CURLY_BRACES.getTypes());
    add(KEYWORDS, SchemaParserDefinition.KEYWORDS.getTypes());
  }

  private static void add(TextAttributesKey key, IElementType... types) {
    for (IElementType type : types) {
      keys.put(type, key);
    }
  }

  @NotNull
  @Override
  public Lexer getHighlightingLexer() {
    return SchemaFlexAdapter.newInstance();
  }

  @NotNull
  @Override
  public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
    return pack(keys.get(tokenType), EMPTY);
  }
}