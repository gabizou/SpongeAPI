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
package org.spongepowered.api.data.persistence;

import java.util.List;

/**
 * A {@link DataCodec} for a structured value, stored as a {@link DataView}
 * with one key per field. Create one with
 * {@link DataCodec#record(java.util.function.Function, FieldCodec[])}.
 *
 * @param <O> The type of value
 */
public interface RecordCodec<O> extends DataCodec<O> {

    /**
     * Encodes a value into a new {@link DataContainer}.
     *
     * @param value The value
     * @return The container holding the value's fields
     * @throws DataCodecException If a field cannot be encoded portably
     */
    @Override
    DataContainer encode(O value) throws DataCodecException;

    /**
     * Gets the keys of the fields, in the order they were given.
     *
     * @return The keys
     */
    List<String> keys();

    /**
     * The decoded fields of a record, passed to the factory of
     * {@link DataCodec#record(java.util.function.Function, FieldCodec[])}.
     *
     * @param <O> The type of the record
     */
    interface Fields<O> {

        /**
         * Gets the decoded value of a field.
         *
         * @param field The field, which must be one of the record's fields
         * @param <T> The type of the field's value
         * @return The value
         * @throws IllegalArgumentException If the field is not one of the
         *     record's fields
         */
        <T> T get(FieldCodec<O, T> field);
    }
}
