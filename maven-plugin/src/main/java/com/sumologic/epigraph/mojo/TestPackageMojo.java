/* Created by yegor on 7/18/16. */

package com.sumologic.epigraph.mojo;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;

/**
 * Package Epigraph test source (.esc) files
 */
@Mojo(name = "test-package", defaultPhase = LifecyclePhase.PACKAGE, requiresProject = true, threadSafe = true)
public class TestPackageMojo extends BasePackageMojo {

  /** Directory containing test epigraph sources that should be packaged into the JAR. */
  @Parameter(defaultValue = "${project.basedir}/src/test/epigraph"/*${project.build.testOutputDirectory}"?*/, required = true)
  private File testClassesDirectory;

  /** Classifier used for test-package artifact. */
  @Parameter(defaultValue = "epigraph-test-sources", required = true)
  private String classifier;

  @Override
  protected File getClassesDirectory() { return testClassesDirectory; }

  @Override
  protected String getClassifier() { return classifier; }

  @Override
  protected String getType() { return null/*"epigraph-test-schema"*/; }

}
