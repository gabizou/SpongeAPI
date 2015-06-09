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
package org.spongepowered.api.data;

import com.google.common.base.Optional;
import org.spongepowered.api.data.value.ValueContainer;
import org.spongepowered.api.util.annotation.TransformWith;

import java.util.function.Function;

/**
 * Represents a changelist of data that can be applied to a {@link DataHolder}.
 * With a {@link DataManipulator}, specific sets of mutable data can be
 * represented and changed outside the live state of the {@link DataHolder}.
 *
 * <p>{@link DataManipulator}s are serializable such that they can be serialized
 * and deserialized from persistence, and applied to {@link DataHolder}s with
 * respects to their {@link DataPriority}.</p>
 *
 * @param <M> The type of {@link DataManipulator} for comparisons
 */
public interface DataManipulator<M extends DataManipulator<M, I>, I extends ImmutableDataManipulator<I, M>> extends Comparable<M>, DataSerializable,
                                                                                                                    ValueContainer<M> {

    Optional<M> fill(DataHolder dataHolder);

    Optional<M> fill(DataHolder dataHolder, Function<I, M> overlap);

    Optional<M> from(DataContainer container);

    @TransformWith
    @Override
    M copy();

    @TransformWith
    I asImmutable();
}
