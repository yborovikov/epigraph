package io.epigraph.projections.generic;

import de.uka.ilkd.pp.DataLayouter;
import de.uka.ilkd.pp.PrettyPrintable;
import io.epigraph.types.Type;
import io.epigraph.util.pp.DataPrettyPrinter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashSet;
import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class GenericVarProjection<T extends GenericTagProjection<?>, S extends GenericVarProjection<T, S>>
    implements PrettyPrintable {
  @NotNull
  private final Type type;
  @NotNull
  private final LinkedHashSet<T> tagProjections;
  @Nullable
  private final LinkedHashSet<S> polymorphicTails;

  public GenericVarProjection(@NotNull Type type,
                              @NotNull LinkedHashSet<T> tagProjections,
                              @Nullable LinkedHashSet<S> polymorphicTails) {
    this.type = type;
    this.tagProjections = tagProjections;
    this.polymorphicTails = polymorphicTails;
  }


  @NotNull
  public Type type() { return type; }

  @NotNull
  public LinkedHashSet<T> tagProjections() { return tagProjections; }

  @Nullable
  public T tagProjection(@NotNull Type.Tag tag) {
    for (T tagProjection : tagProjections)
      if (tagProjection.tag().equals(tag)) return tagProjection;

    return null;
  }

  @Nullable
  public LinkedHashSet<S> polymorphicTails() { return polymorphicTails; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    GenericVarProjection that = (GenericVarProjection) o;
    return Objects.equals(type, that.type) &&
           Objects.equals(tagProjections, that.tagProjections) &&
           Objects.equals(polymorphicTails, that.polymorphicTails);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, tagProjections, polymorphicTails);
  }

  @Override
  public <Exc extends Exception> void prettyPrint(DataLayouter<Exc> l) throws Exc {
    // TODO treat single-branch/samo- vars in a special way?
    l.beginCInd().print("var ").print(type.name().toString()).print(" (");
    for (T tagProjection : tagProjections) {
      l.nl().print(tagProjection);
    }
    l.end().brk().print(")");

    if (polymorphicTails != null && !polymorphicTails.isEmpty()) {
      l.brk().beginCInd().print("~(");
      boolean first = true;
      for (GenericVarProjection tail : polymorphicTails) {
        if (first) first = false;
        else l.print(",");

        l.brk().print(tail);
      }
      l.end().brk().print(")");
    }
  }

  @Override
  public String toString() { return DataPrettyPrinter.prettyPrint(this); }
}