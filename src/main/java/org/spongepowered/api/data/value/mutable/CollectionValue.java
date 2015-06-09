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

import com.google.common.base.Predicate;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.value.ValueContainer;
import org.spongepowered.api.data.value.immutable.ImmutableCollectionValue;

import java.util.Collection;

/**
 * Represents a {@link Value} that is backed by a {@link Collection} of
 * elements. Utility of this value ensures that the backed {@link Collection}
 * is not in use by other objects as the value is copied from the original
 * {@link CompositeValueStore} or {@link DataHolder}. The advantage of the
 * {@link CollectionValue} is that fluency is achieved by being able to chain
 * method calls for {@link #add(Object)} {@link #addAll(Iterable)} etc. The
 * advantage of a {@link CollectionValue} is that no {@code null} values can
 * exist.
 *
 * @param <E> The type of elements contained in the collection
 * @param <C> The type of collection, for chaining
 * @param <V> The type of {@link CollectionValue}
 * @param <I> The immutable type of {@link ImmutableCollectionValue}
 * @param <S> The containing {@link ValueContainer}
 */
public interface CollectionValue<E, C extends Collection<E>, V extends CollectionValue<E, C, V, I, S>, I extends ImmutableCollectionValue<E, C, I, V, S>, S extends ValueContainer<S>> extends
                                                                                                                                                                                       Value<C, S>, Iterable<E> {

    /**
     * Gets the current size of the {@link Collection} contained within this
     * {@link CollectionValue}.
     *
     * @return The current size
     */
    int size();

    /**
     * Adds the given {@link E} value and returns this instance for chaining.
     *
     * @param value The value to add
     * @return This value, for chaining
     */
    V add(E value);

    /**
     * Adds all of the {@link Iterable} values and returns this instance for
     * chaining.
     *
     * @param values The values to add
     * @return This value, for chaining
     */
    V addAll(Iterable<E> values);

    /**
     * Removes the given {@link E} value and returns this instance for
     * chaining.
     *
     * @param value The value to remove
     * @return This value, for chaining
     */
    V remove(E value);

    /**
     * Removes the given values contained in the provided {@link Iterable}.
     *
     * @param values The values to remove
     * @return This value, for chaining
     */
    V removeAll(Iterable<E> values);

    /**
     * Removes any element currently contained in the backed {@link Collection}
     * that the provided {@link Predicate}'s {@link Predicate#apply(Object)}
     * returns {@code true}. Then returns this {@link CollectionValue} for
     * chaining.
     *
     * @param predicate The predicate to remove values
     * @return This value, for chaining
     */
    V removeAll(Predicate<E> predicate);

    /**
     * Checks if the given {@link E} value is contained within the backed
     * {@link Collection}.
     *
     * @param value The value to check
     * @return True if the value is contained within this collection
     */
    boolean contains(E value);

    /**
     * Checks if all of the given {@link Iterable} values are contained within
     * the backed {@link Collection}.
     *
     * @param iterable The iterable elements to check
     * @return True if all of the elements are contained in the backed
     *     collection
     */
    boolean containsAll(Iterable<E> iterable);

    /**
     * Applies the given {@link Predicate} to the backed {@link Collection}
     * value such that any elements that return {@code true} for the
     * {@link Predicate#apply(Object)} method will be included in a new
     * {@link CollectionValue}.
     *
     * <p>The difference between this method and {@link #removeAll(Predicate)}
     * is that this value is NOT modified in any way, and a new instance is
     * returned.</p>
     *
     * @param predicate The predicate to apply for the new instance
     * @return The new {@link CollectionValue} instance
     */
    V filter(Predicate<E> predicate);

    /**
     * Gets all the elements in a new {@link Collection} separate from the
     * backed {@link Collection}. Any modifications made to the returned
     * collection are not reciprocated to this value's backed collection.
     *
     * @return A copied {@link Collection} of the backed collection
     */
    C getAll();

    @Override
    I asImmutable();
}
