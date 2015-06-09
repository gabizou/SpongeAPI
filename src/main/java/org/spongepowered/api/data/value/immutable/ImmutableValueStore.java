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

import com.google.common.base.Optional;
import org.spongepowered.api.data.DataManipulator;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.merge.MergeStrategy;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.ValueContainer;

import javax.annotation.Nullable;

public interface ImmutableValueStore<I extends ImmutableValueStore<I, H>, H extends ValueContainer<?>> extends ValueContainer<I> {

    <T extends H> Optional<T> get(Class<T> holderClass);

    @Nullable
    <T extends H> T getOrNull(Class<T> holderClass);

    <T extends H> T getOrElse(Class<T> holderClass, T defaultHolder);

    <E> Optional<I> with(Key<? extends BaseValue<E, ?>> key, E value);

    /**
     * Gets an altered copy of this {@link ImmutableValueStore} with the given
     * {@link DataManipulator} modified data. If the data is not compatible for
     * any reason, {@link Optional#absent()} is returned.
     *
     * <p>This does not alter the current {@link ImmutableValueStore}.</p>
     *
     * @param valueHolders The new manipulator containing data
     * @return A new immutable value store with the given value holders
     */
    Optional<I> with(H... valueHolders);

    /**
     * Gets an altered copy of this {@link ImmutableValueStore} with the given
     * {@link DataManipulator} modified data. If the data is not compatible for
     * any reason, {@link Optional#absent()} is returned.
     *
     * <p>This does not alter the current {@link ImmutableValueStore}.</p>
     *
     * @param valueHolders The new manipulator containing data
     * @return A new immutable value store with the given value holders
     */
    Optional<I> with(Iterable<H> valueHolders);

    /**
     * Gets an altered copy of this {@link ImmutableValueStore} without the
     * given {@link DataManipulator}. If the data represented by the
     * manipulator can not exist without a "default state" of the
     * {@link DataManipulator}, the {@link DataManipulator} is reset to the
     * "default" state.
     *
     * @param valueHolders The value holders to ignore
     * @return A new immutable data holder without the given manipulator
     */
    Optional<I> without(H... valueHolders);

    /**
     * Gets an altered copy of this {@link ImmutableValueStore} without the
     * given {@link DataManipulator}. If the data represented by the
     * manipulator can not exist without a "default state" of the
     * {@link DataManipulator}, the {@link DataManipulator} is reset to the
     * "default" state.
     *
     * @param valueHolders The value holders to ignore
     * @return A new immutable data holder without the given manipulator
     */
    Optional<I> without(Iterable<H> valueHolders);

    boolean supports(Class<? extends H> holderClass);

    /**
     * Attempts to merge the {@link ImmutableValue}s from this
     * {@link ImmutableValueStore} and the given {@link ImmutableValueStore} to
     * produce a new instance of the merged result.
     *
     * @param that The other immutable value store to gather values from
     * @return The new immutable value store instance
     */
    I merge(I that);


    I merge(I that, MergeStrategy strategy);

}
