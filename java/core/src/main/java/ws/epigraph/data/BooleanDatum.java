/*
 * Copyright 2017 Sumo Logic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* Created by yegor on 9/6/16. */

package ws.epigraph.data;

import org.jetbrains.annotations.Nullable;
import ws.epigraph.types.BooleanType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Function;


public interface BooleanDatum extends PrimitiveDatum<Boolean> {

  @Override
  @NotNull BooleanType type();

  @Override
  @NotNull BooleanDatum.Raw _raw();

  @Override
  @NotNull BooleanDatum.Imm toImmutable();

  @Override
  @NotNull Boolean getVal();


  abstract class Impl extends PrimitiveDatum.Impl<Boolean, BooleanType> implements BooleanDatum {

    protected Impl(@NotNull BooleanType type) { super(type); }

  }


  interface Raw extends BooleanDatum, PrimitiveDatum.Raw<Boolean> {

    @Override
    @NotNull BooleanDatum.Imm.Raw toImmutable();

  }


  interface Static extends BooleanDatum, PrimitiveDatum.Static<Boolean> {

    @Override
    @NotNull BooleanDatum.Imm.Static toImmutable();

  }


  interface Imm extends BooleanDatum, PrimitiveDatum.Imm<Boolean> {

    @Override
    @NotNull BooleanDatum.Imm.Raw _raw();


    final class Raw extends BooleanDatum.Impl implements BooleanDatum.Imm, BooleanDatum.Raw, PrimitiveDatum.Imm.Raw<Boolean> {

      private final @NotNull Boolean val;

      private final @Nullable Datum.Imm meta;

      private final @NotNull Val.Imm.Raw value = new Val.Imm.Raw.DatumVal(this);

      private final int hashCode;

      public Raw(@NotNull BooleanDatum.Builder.Raw mutable) {
        super(mutable.type());
        val = mutable.getVal();
        Datum _meta = mutable.meta();
        meta = _meta == null ? null : _meta.toImmutable();
        hashCode = Objects.hash(type(), val);
      }

      @Override
      public @Nullable Datum.Imm meta() { return meta; }

      @Override
      public @NotNull BooleanDatum.Imm.Raw toImmutable() { return this; }

      @Override
      public @NotNull BooleanDatum.Imm.Raw _raw() { return this; }

      @Override
      public @NotNull Boolean getVal() { return val; }

      @Override
      public @NotNull Val.Imm.Raw asValue() { return value; }

      @Override
      public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BooleanDatum)) return false;
        if (o instanceof Immutable && hashCode != o.hashCode()) return false;
        BooleanDatum that = (BooleanDatum) o;
        return type().equals(that.type()) && val.equals(that._raw().getVal());
      }

      @Override
      public final int hashCode() { return hashCode; }

    }


    interface Static extends BooleanDatum.Imm, BooleanDatum.Static, PrimitiveDatum.Imm.Static<Boolean> {

      @Override
      @NotNull BooleanDatum.Imm.Static toImmutable();

      @Override
      @NotNull BooleanDatum.Imm.Raw _raw();


      abstract class Impl<MyImmDatum extends BooleanDatum.Imm.Static, MyImmVal extends Val.Imm.Static>
          extends BooleanDatum.Impl implements BooleanDatum.Imm.Static {

        private final @NotNull BooleanDatum.Imm.Raw raw;

        private final @NotNull MyImmVal value;

        protected Impl(
            @NotNull BooleanType type,
            @NotNull BooleanDatum.Imm.Raw raw,
            @NotNull Function<Val.Imm.@NotNull Raw, @NotNull MyImmVal> immValConstructor
        ) {
          super(type); // TODO take static type separately?
          this.raw = raw; // TODO validate raw is kosher?
          this.value = immValConstructor.apply(new Val.Imm.Raw.DatumVal(this));
        }

        @Override
        public @NotNull Boolean getVal() { return raw.getVal(); }

        @SuppressWarnings("unchecked")
        @Override
        public @NotNull MyImmDatum toImmutable() { return (MyImmDatum) this; }

        @Override
        public final @NotNull BooleanDatum.Imm.Raw _raw() { return raw; }

        @Override
        public @NotNull MyImmVal asValue() { return value; }

        @Override
        public final int hashCode() { return raw.hashCode(); }

        @Override
        public final boolean equals(Object obj) { return raw.equals(obj); }

      }


    }


  }


  abstract class Builder extends BooleanDatum.Impl implements PrimitiveDatum.Builder<Boolean> {

    protected Builder(@NotNull BooleanType type) { super(type); }

    @Override
    public abstract void setVal(@NotNull Boolean val);

    @Override
    public abstract @NotNull BooleanDatum.Builder.Raw _raw();


    public static final class Raw extends BooleanDatum.Builder implements BooleanDatum.Raw, PrimitiveDatum.Builder.Raw<Boolean> {

      private @NotNull Boolean val;

      private @Nullable Datum meta;

      private final @NotNull Val.Builder.Raw value = new Val.Builder.Raw.DatumVal(this);

      public Raw(@NotNull BooleanType type, @NotNull Boolean val) {
        super(type);
        if (val == null) throw new IllegalArgumentException();
        this.val = val;
      }

      @Override
      public @NotNull Boolean getVal() { return val; }

      @Override
      public void setVal(@NotNull Boolean val) {
        this.val = val;
      }

      @Override
      public @Nullable Datum meta() { return meta; }

      @Override
      public @NotNull Datum.@NotNull Builder setMeta(final @Nullable Datum meta) {
        this.meta = type().checkMeta(meta);
        return this;
      }

      @Override
      public @NotNull BooleanDatum.Imm.Raw toImmutable() { return new BooleanDatum.Imm.Raw(this); }

      @Override
      public @NotNull BooleanDatum.Builder.Raw _raw() { return this; }

      @Override
      public @NotNull Val.Builder.Raw asValue() { return value; }

      @Override
      public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BooleanDatum)) return false;
        BooleanDatum that = (BooleanDatum) o;
        return type().equals(that.type()) && val.equals(that._raw().getVal());
      }

      @Override
      public final int hashCode() { return Objects.hash(type(), val); }

    }


    public abstract static class Static<
        MyImmDatum extends BooleanDatum.Imm.Static,
        MyValBuilder extends Val.Builder.Static
        > extends BooleanDatum.Builder
        implements BooleanDatum.Static, PrimitiveDatum.Builder.Static<Boolean, MyImmDatum> {

      private final @NotNull BooleanDatum.Builder.Raw raw;

      private final @NotNull MyValBuilder value;

      private final @NotNull Function<BooleanDatum.Imm.Raw, MyImmDatum> immDatumConstructor;

      protected Static(
          @NotNull BooleanType.Static<MyImmDatum, ?, ?, ?, ?, ?> type,
          @NotNull BooleanDatum.Builder.Raw raw,
          @NotNull Function<BooleanDatum.Imm.Raw, MyImmDatum> immDatumConstructor,
          @NotNull Function<Val.Builder.@NotNull Raw, @NotNull MyValBuilder> builderValConstructor
      ) {
        super(type);
        // TODO check type equality
        this.raw = raw;
        this.value = builderValConstructor.apply(new Val.Builder.Raw.DatumVal(this));
        this.immDatumConstructor = immDatumConstructor;
      }

      @Override
      public final @NotNull Boolean getVal() { return raw.getVal(); }

      @Override
      public final void setVal(@NotNull Boolean val) { raw.setVal(val); }

      @Override
      public final @NotNull MyImmDatum toImmutable() { return immDatumConstructor.apply(_raw().toImmutable()); }

      @Override
      public final @NotNull BooleanDatum.Builder.Raw _raw() { return raw; }

      @Override
      public @NotNull MyValBuilder asValue() { return value; }

      @Override
      public final int hashCode() { return raw.hashCode(); }

      @Override
      public final boolean equals(Object obj) { return raw.equals(obj); }

    }


  }


}
