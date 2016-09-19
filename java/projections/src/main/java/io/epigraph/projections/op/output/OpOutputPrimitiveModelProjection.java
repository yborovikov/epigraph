package io.epigraph.projections.op.output;

import io.epigraph.types.PrimitiveType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpOutputPrimitiveModelProjection extends OpOutputModelProjection<PrimitiveType> {
  public OpOutputPrimitiveModelProjection(@NotNull PrimitiveType model,
                                          boolean includeInDefault,
                                          @Nullable OpParams params) {
    super(model, includeInDefault, params);
  }
}
