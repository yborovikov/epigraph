package com.sumologic.epigraph.ideaplugin.schema.highlighting;

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class AnnotatorTest extends LightCodeInsightFixtureTestCase {
  @Override
  protected String getTestDataPath() {
    return "src/test/resources/testData/annotator";
  }

  public void testInvalid1() {
    myFixture.configureByFile("Invalid1.es");
    myFixture.checkHighlighting(false, false, true);
  }
}