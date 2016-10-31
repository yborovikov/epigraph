package io.epigraph.lang;

import net.jcip.annotations.Immutable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Fqn is a collection of string segments
 *
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
@Immutable
public class Qn implements Comparable<Qn> {
  // todo this often acts as Fqn prefix/suffix. Rename to Qn ?

  public static final Qn EMPTY = new Qn();

  @NotNull
  public final String[] segments;

  public Qn(@NotNull String... segments) {
    this.segments = new String[segments.length];
    System.arraycopy(segments, 0, this.segments, 0, segments.length);

    // auto-canonicalize
    for (int i = 0; i < segments.length; i++)
      this.segments[i] = NamingConventions.unquote(this.segments[i]);
  }

  public Qn(@NotNull Collection<String> segments) {
    this(segments.toArray(new String[segments.size()]));
  }

  @NotNull
  public static Qn fromDotSeparated(@NotNull String fqn) {
    // todo will break if dot is inside back-ticks..
    return new Qn(fqn.split("\\."));
  }

  @Nullable
  public static Qn fromNullableDotSeparated(@Nullable String fqn) {
    return fqn == null ? null : fromDotSeparated(fqn);
  }

  public int size() {
    return segments.length;
  }

  public boolean isEmpty() {
    return size() == 0;
  }

  @Nullable
  public String first() {
    if (isEmpty()) return null;
    return segments[0];
  }

  @Nullable
  public String last() {
    if (isEmpty()) return null;
    return segments[size() - 1];
  }

  @NotNull
  public Qn removeLastSegment() {
    return removeTailSegments(1);
  }

  @NotNull
  public Qn removeFirstSegment() {
    return removeHeadSegments(1);
  }

  @NotNull
  public Qn removeHeadSegments(int n) {
    if (size() < n) throw new IllegalArgumentException("Can't remove " + n + " segments from '" + toString() + "'");
    if (size() == n) return EMPTY;

    String[] f = new String[size() - n];
    System.arraycopy(segments, n, f, 0, size() - n);
    return new Qn(f);
  }

  @NotNull
  public Qn removeTailSegments(int n) {
    if (size() < n) throw new IllegalArgumentException("Can't remove " + n + " segments from '" + toString() + "'");
    if (size() == n) return EMPTY;

    String[] f = new String[size() - n];
    System.arraycopy(segments, 0, f, 0, size() - n);
    return new Qn(f);
  }

  @NotNull
  public Qn append(@NotNull Qn suffix) {
    if (isEmpty()) return suffix;
    if (suffix.isEmpty()) return this;

    String[] f = new String[size() + suffix.size()];
    System.arraycopy(segments, 0, f, 0, size());
    System.arraycopy(suffix.segments, 0, f, size(), suffix.size());
    return new Qn(f);
  }

  @NotNull
  public Qn append(@NotNull String segment) {
    String[] f = new String[size() + 1];
    System.arraycopy(segments, 0, f, 0, size());
    f[size()] = NamingConventions.unquote(segment);
    return new Qn(f);
  }

  public boolean startsWith(@NotNull Qn prefix) {
    if (prefix.isEmpty()) return true;
    if (size() < prefix.size()) return false;

    for (int i = 0; i < prefix.size(); i++) {
      if (!segments[i].equals(prefix.segments[i])) return false;
    }

    return true;
  }

  public boolean endsWith(@NotNull Qn suffix) {
    if (suffix.isEmpty()) return true;
    int diff = size() - suffix.size();
    if (diff < 0) return false;

    for (int i = 0; i < suffix.size(); i++) {
      if (!segments[i + diff].equals(suffix.segments[i])) return false;
    }

    return true;
  }

  public String toString() {
    StringBuilder r = new StringBuilder();
    for (String segment : segments) {
      if (r.length() > 0) r.append('.');
      r.append(segment);
    }

    return r.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Qn fqn = (Qn) o;

    return Arrays.equals(segments, fqn.segments);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(segments);
  }

  @Nullable
  public static String toNullableString(@Nullable Qn fqn) {
    return fqn == null ? null : fqn.toString();
  }

  @Override
  public int compareTo(@NotNull Qn fqn) {
    return toString().compareTo(fqn.toString());
  }

  /**
   * Find all FQNs starting with {@code prefix} and remove prefix from them
   */
  public static Collection<Qn> getMatchingWithPrefixRemoved(@NotNull Collection<Qn> qns, @NotNull Qn prefix) {
    if (prefix.isEmpty()) return qns;
    return qns.stream()
              .filter(fqn -> fqn.startsWith(prefix))
              .map(fqn -> fqn.removeHeadSegments(prefix.size()))
//        .filter(fqn -> !fqn.isEmpty())
              .collect(Collectors.toSet());
  }
}