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

import org.spongepowered.api.data.DataHolder;

import java.util.Comparator;

/**
 * Represents a value that may itself be {@link Comparable} or can be
 * compared using the {@link Comparator} such that the value is "bounded" by a
 * {@link #getMinValue()} and a {@link #getMaxValue()}. For a majority of
 * values, a {@link BoundedValue} is limited to being within it's destined
 * bounds. Any {@link BoundedValue} that is out of it's intended bounds will
 * throw an {@link IllegalStateException} if used or offered to a
 * {@link ValueContainer} or {@link DataHolder}.
 *
 * @param <E> The type of value that can be compared
 * @param <S> The owning {@link ValueContainer}
 */
public interface BoundedValue<E, S extends ValueContainer<S>> extends BaseValue<E, S> {

    E getMinValue();

    E getMaxValue();

    Comparator<E> getComparator();

}
