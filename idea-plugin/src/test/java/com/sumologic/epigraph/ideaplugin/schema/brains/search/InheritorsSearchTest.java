package com.sumologic.epigraph.ideaplugin.schema.brains.search;

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import com.sumologic.epigraph.ideaplugin.schema.index.SchemaIndexUtil;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaTypeDef;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class InheritorsSearchTest extends LightCodeInsightFixtureTestCase {
  @Override
  protected String getTestDataPath() {
    return "src/test/resources/testData/brains/search";
  }

  public void testDirectInheritors() {
    myFixture.configureByFile("InheritorsSearch.es");
    SchemaTypeDef typeDef = findTypeDef("R1");
    Collection<SchemaTypeDef> directInheritors = SchemaDirectTypeInheritorsSearch.search(typeDef).findAll();
    assertEquals(1, directInheritors.size());
    SchemaTypeDef r2 = directInheritors.iterator().next();
    assertEquals("R2", r2.getName());
  }

  public void testInheritors() {
    myFixture.configureByFile("InheritorsSearch.es");
    SchemaTypeDef typeDef = findTypeDef("R1");
    Collection<SchemaTypeDef> inheritors = SchemaTypeInheritorsSearch.search(typeDef).findAll();
    checkResults(inheritors, "R2", "R3");
  }

  public void testSupplements() {
    myFixture.configureByFile("InheritorsSearch2.es");
    SchemaTypeDef r4 = findTypeDef("R4");
    Collection<SchemaTypeDef> inheritors = SchemaTypeInheritorsSearch.search(r4).findAll();
    checkResults(inheritors, "R1", "R2", "R3");

    SchemaTypeDef r1 = findTypeDef("R1");
    Collection<SchemaTypeDef> parents = SchemaDirectTypeParentsSearch.search(r1).findAll();
    checkResults(parents, "R3");
  }

  public void testDirectParents() {
    myFixture.configureByFile("InheritorsSearch.es");
    SchemaTypeDef typeDef = findTypeDef("R2");
    Collection<SchemaTypeDef> directInheritors = SchemaDirectTypeParentsSearch.search(typeDef).findAll();
    assertEquals(1, directInheritors.size());
    SchemaTypeDef r2 = directInheritors.iterator().next();
    assertEquals("R1", r2.getName());
  }

  private SchemaTypeDef findTypeDef(String name) {
    SchemaTypeDef typeDef = SchemaIndexUtil.findTypeDef(myFixture.getProject(), Collections.singleton("x"), name);
    assertNotNull(typeDef);
    return typeDef;
  }

  private void checkResults(Collection<SchemaTypeDef> result, String... expected) {
    Set<String> names = result.stream().map(SchemaTypeDef::getName).collect(Collectors.toSet());
    Set<String> expectedNames = new HashSet<>(Arrays.asList(expected));
    assertEquals(expectedNames, names);
  }
}
