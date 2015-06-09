/*
 * This file is part of SpongeAPI, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.spongepowered.api.data.value.immutable;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;
import org.spongepowered.api.data.value.ValueContainer;
import org.spongepowered.api.data.value.mutable.MapValue;

import java.util.Map;
import java.util.function.Predicate;

public interface ImmutableMapValue<K, V, C extends ValueContainer<C>> extends ImmutableValue<Map<K, V>, C> {

    int size();

    ImmutableMapValue<K, V, C> with(K key, V value);

    ImmutableMapValue<K, V, C> withAll(Map<K, V> value);

    ImmutableMapValue<K, V, C> without(K key);

    ImmutableMapValue<K, V, C> withoutAll(Iterable<K> keys);

    ImmutableMapValue<K, V, C> withoutAll(Predicate<Map.Entry<K, V>> predicate);

    boolean containsKey(K key);

    boolean containsValue(V value);

    ImmutableSet<K> keySet();

    ImmutableSet<Map.Entry<K, V>> entrySet();

    ImmutableCollection<V> values();

    @Override
    MapValue<K, V, C> asMutable();
}
