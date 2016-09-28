package io.epigraph.projections.req;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqParams {
  @NotNull
  private final Map<String, ReqParam> params;

  public ReqParams(@NotNull Map<String, ReqParam> params) {this.params = params;}

  public ReqParams(ReqParam... params) {
    this.params = new HashMap<>();
    for (ReqParam param : params)
      this.params.put(param.name(), param);
  }

  public ReqParams(@NotNull Collection<ReqParam> params) {
    this.params = new HashMap<>();
    for (ReqParam param : params)
      this.params.put(param.name(), param);
  }

  public boolean hasParam(@NotNull String name) { return params.containsKey(name); }

  public boolean isEmpty() { return params.isEmpty(); }

  @Nullable
  public ReqParam get(@NotNull String key) { return params.get(key); }

  @NotNull
  public Map<String, ReqParam> params() { return params; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ReqParams reqParams = (ReqParams) o;
    return Objects.equals(params, reqParams.params);
  }

  @Override
  public int hashCode() {
    return Objects.hash(params);
  }
}
