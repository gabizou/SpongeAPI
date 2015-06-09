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

import org.spongepowered.api.data.value.ValueContainer;
import org.spongepowered.api.data.value.mutable.CollectionValue;

import java.util.Collection;
import java.util.function.Predicate;

/**
 * A {@link ImmutableValue} type that handles a {@link Collection} of elements
 * type {@link E}. All of the methods provided for modification return new
 * instances of the same type. Likewise, the underlying {@link ValueContainer}
 * is not modified.
 *
 * @param <E> The type of element
 * @param <C> The type of {@link Collection}
 * @param <I> The extended {@link ImmutableCollectionValue} for self referencing
 * @param <M> The mutable {@link CollectionValue} counterpart for {@link #asMutable()}
 * @param <H> The {@link ValueContainer}
 */
public interface ImmutableCollectionValue<E, C extends Collection<E>, I extends ImmutableCollectionValue<E, C, I, M, H>, M extends CollectionValue<E, C, M, I, H>, H extends ValueContainer<H>> extends ImmutableValue<C, H> {

    /**
     * Gets the size of the underlying collection of elements.
     *
     * @return The size
     */
    int size();

    @Override
    H with(C value);

    /**
     * Creates a new {@link ImmutableCollectionValue} with the given values
     * along with any pre-existing values within this value.
     *
     * @param values The values to add
     * @return A new instance
     */
    I with(E... values);

    I withAll(Iterable<E> values);

    I without(E value);

    I withoutAll(Iterable<E> values);

    I withoutAll(Predicate<E> predicate);

    boolean contains(E value);

    boolean containsAll(Iterable<E> iterable);

    C getAll();

    @Override
    I replace(C value);

    @Override
    M asMutable();
}
