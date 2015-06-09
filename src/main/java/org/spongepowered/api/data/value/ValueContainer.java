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
package org.spongepowered.api.data.value;

import com.google.common.base.Optional;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.immutable.ImmutableValueStore;
import org.spongepowered.api.data.value.mutable.CompositeValueStore;

import javax.annotation.Nullable;
import java.util.Set;

/**
 * A ValueContainer is a holder of a particular set of {@link BaseValue}s. While
 * there exists a {@link CompositeValueStore} and {@link ImmutableValueStore},
 * the emphasis of {@link ValueContainer} is that it only contains "data". It
 * is not known whether a {@code ValueContainer} is mutable or immutable.
 *
 * Being that
 *
 * @param <C> The type of container for fluency
 */
public interface ValueContainer<C extends ValueContainer<C>> {

    <E> Optional<E> get(Key<? extends BaseValue<E, ?>> key);
    
    @Nullable
    <E> E getOrNull(Key<? extends BaseValue<E, ?>> key);

    <E> E getOrElse(Key<? extends BaseValue<E, ?>> key, E defaultValue);
    
    <E, V extends BaseValue<E, ?>> Optional<V> getValue(Key<V> key);

    boolean supports(Key<?> key);

    C copy();

    Set<Key<?>> getKeys();

    Set<BaseValue<?, C>> getValues();

}
