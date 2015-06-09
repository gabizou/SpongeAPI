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
package org.spongepowered.api.data.value.mutable;

import com.google.common.base.Optional;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.merge.MergeStrategy;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.ValueContainer;

import javax.annotation.Nullable;

/**
 * Represents a {@link ValueContainer} that contains a various bundle of
 * {@link ValueContainer}s of type {@link H} that can be manipulated
 * separately from this {@link CompositeValueStore}.
 *
 * @param <S> The type of composite store, for self referencing
 * @param <H> The type of {@link ValueContainer} to restrict accessing of
 *     values to
 */
public interface CompositeValueStore<S extends CompositeValueStore<S, H>, H extends ValueContainer<?>> extends ValueContainer<S> {

    /**
     * Attempts to get an instance of the given {@link ValueContainer} class with
     * any and all possible contained {@link Value}s copied to the respective
     * {@link Value} instances into the {@link ValueContainer}.
     *
     * @param containerClass The class of the value container
     * @param <T> The type of the class
     * @return The value container, if available and compatible
     */
    <T extends H> Optional<T> get(Class<T> containerClass);

    /**
     * Attempts to get an instance of the given {@link ValueContainer} class with
     * any and all possible contained {@link Value}s copied to the respective
     * {@link Value} instances into the {@link ValueContainer}. If the values
     * are not present, a new {@link ValueContainer}
     *
     * @param containerClass The class of the value holder
     * @param <T> The type of the class
     * @return The value holder, if available and compatible
     */
    <T extends H> Optional<T> getOrCreate(Class<T> containerClass);

    @Nullable
    <T extends H> T getOrNull(Class<T> containerClass);

    /**
     * Attemps to get an instance of the given {@link ValueContainer}
     * @param containerClass
     * @param <T>
     * @return
     */
    <T extends H> T getOrCreateNull(Class<T> containerClass);

    <T extends H> T getOrElse(Class<T> containerClass, T defaultHolder);

    <E> DataTransactionResult offer(Key<? extends BaseValue<E, ?>> key, E value);

    boolean supports(Class<? extends H> containerClass);

    DataTransactionResult offer(H... valueContainers);

    DataTransactionResult offer(Iterable<H> valueContainers);

    DataTransactionResult remove(H... valueContainers);

    DataTransactionResult remove(Iterable<H> valueContainers);

    DataTransactionResult undo(DataTransactionResult result);

    DataTransactionResult copyTo(S that);

    DataTransactionResult copyTo(S that, MergeStrategy strategy);

    DataTransactionResult copyFrom(S that);

    DataTransactionResult copyFrom(S that, MergeStrategy strategy);

}
