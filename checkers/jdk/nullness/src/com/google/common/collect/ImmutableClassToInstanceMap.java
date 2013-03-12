/*
 * Copyright (C) 2009 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.common.collect;

import static com.google.common.collect.MutableClassToInstanceMap.cast;

import java.util.Map;

import checkers.nullness.quals.*;

/**
 * A class-to-instance map backed by an {@link ImmutableMap}. See also {@link
 * MutableClassToInstanceMap}.
 *
 * @author Kevin Bourrillion
 */
public final class ImmutableClassToInstanceMap<B> extends
    ForwardingMap<Class<? extends B>, B> implements ClassToInstanceMap<B> {
  /**
   * Returns a new builder. The generated builder is equivalent to the builder
   * created by the {@link Builder} constructor.
   */
  public static <B> Builder<B> builder() {
    return new Builder<B>();
  }

  /**
   * A builder for creating immutable class-to-instance maps. Example:
   * <pre>   {@code
   *
   *   static final ImmutableClassToInstanceMap<Handler> HANDLERS =
   *       new ImmutableClassToInstanceMap.Builder<Handler>()
   *           .put(FooHandler.class, new FooHandler())
   *           .put(BarHandler.class, new SubBarHandler())
   *           .put(Handler.class, new QuuxHandler())
   *           .build();}</pre>
   *
   * <p>After invoking {@link #build()} it is still possible to add more
   * entries and build again. Thus each map generated by this builder will be
   * a superset of any map generated before it.
   */
  public static final class Builder<B> {
    private final ImmutableMap.Builder<Class<? extends B>, B> mapBuilder
        = ImmutableMap.builder();

    /**
     * Associates {@code key} with {@code value} in the built map. Duplicate
     * keys are not allowed, and will cause {@link #build} to fail.
     */
    public <T extends B> Builder<B> put(Class<T> type, T value) {
      mapBuilder.put(type, value);
      return this;
    }

    /**
     * Associates all of {@code map's} keys and values in the built map.
     * Duplicate keys are not allowed, and will cause {@link #build} to fail.
     *
     * @throws NullPointerException if any key or value in {@code map} is null
     * @throws ClassCastException if any value is not an instance of the type
     *     specified by its key
     */
    public <T extends B> Builder<B> putAll(
        Map<? extends Class<? extends T>, ? extends T> map) {
      for (Entry<? extends Class<? extends T>, ? extends T> entry
          : map.entrySet()) {
        Class<? extends T> type = entry.getKey();
        T value = entry.getValue();
        mapBuilder.put(type, cast(type, value));
      }
      return this;
    }

    /**
     * Returns a new immutable class-to-instance map containing the entries
     * provided to this builder.
     *
     * @throws IllegalArgumentException if duplicate keys were added
     */
    public ImmutableClassToInstanceMap<B> build() {
      return new ImmutableClassToInstanceMap<B>(mapBuilder.build());
    }
  }

  /**
   * Returns an immutable map containing the same entries as {@code map}. If
   * {@code map} somehow contains entries with duplicate keys (for example, if
   * it is a {@code SortedMap} whose comparator is not <i>consistent with
   * equals</i>), the results of this method are undefined.
   *
   * <p><b>Note:</b> Despite what the method name suggests, if {@code map} is
   * an {@code ImmutableClassToInstanceMap}, no copy will actually be performed.
   *
   * @throws NullPointerException if any key or value in {@code map} is null
   * @throws ClassCastException if any value is not an instance of the type
   *     specified by its key
   */
  @SuppressWarnings("unchecked") // covariant casts safe (unmodifiable)
  public static <B, S extends B> ImmutableClassToInstanceMap<B> copyOf(
      Map<? extends Class<? extends S>, ? extends S> map) {
    if (map instanceof ImmutableClassToInstanceMap) {
      return (ImmutableClassToInstanceMap<B>) (Map) map;
    }
    return new Builder<B>().putAll(map).build();
  }

  private final ImmutableMap<Class<? extends B>, B> delegate;

  private ImmutableClassToInstanceMap(
      ImmutableMap<Class<? extends B>, B> delegate) {
    this.delegate = delegate;
  }

  @Override protected Map<Class<? extends B>, B> delegate() {
    return delegate;
  }

  @SuppressWarnings({"unchecked","nullness"}) // value could not get in if not a T
  public <T extends B> T getInstance(Class<T> type) {
    return (T) delegate.get(type);
  }

  /**
   * Guaranteed to throw an exception and leave the map unmodified.
   *
   * @throws UnsupportedOperationException always
   */
  public <T extends B> T putInstance(Class<T> type, T value) {
    throw new UnsupportedOperationException();
  }
}
