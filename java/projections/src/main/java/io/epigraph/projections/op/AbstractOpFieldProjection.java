package io.epigraph.projections.op;

import io.epigraph.lang.TextLocation;
import io.epigraph.projections.Annotations;
import io.epigraph.projections.abs.AbstractFieldProjection;
import io.epigraph.projections.gen.GenModelProjection;
import io.epigraph.projections.gen.GenTagProjectionEntry;
import io.epigraph.projections.gen.GenVarProjection;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class AbstractOpFieldProjection<
    VP extends GenVarProjection<VP, TP, MP>,
    TP extends GenTagProjectionEntry<MP>,
    MP extends GenModelProjection</*MP*/?, ?>
    > extends AbstractFieldProjection<VP, TP, MP> {

  @NotNull
  private final OpParams params;

  protected AbstractOpFieldProjection(
      @NotNull OpParams params,
      @NotNull final Annotations annotations,
      @NotNull final VP projection,
      @NotNull final TextLocation location) {
    super(annotations, projection, location);
    this.params = params;
  }

  @NotNull
  public OpParams params() { return params; }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    final AbstractOpFieldProjection<?, ?, ?> that = (AbstractOpFieldProjection<?, ?, ?>) o;
    return Objects.equals(params, that.params);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), params);
  }
}