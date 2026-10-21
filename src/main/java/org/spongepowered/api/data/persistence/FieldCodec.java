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

/**
 * One key of a {@link DataCodec#record(java.util.function.Function, FieldCodec[]) record}
 * codec: how to write it from the record and how to read it from a view.
 *
 * <p>Create fields with {@link DataCodec#fieldOf}, {@link DataCodec#optionalFieldOf(String, java.util.function.Function)}
 * or {@link DataCodec#optionalFieldOf(String, Object, java.util.function.Function)}.</p>
 *
 * @param <O> The type of the record
 * @param <T> The type of the field's value
 */
public interface FieldCodec<O, T> {

    /**
     * Gets the key the field is stored under.
     *
     * @return The key
     */
    String key();

    /**
     * Reads the field from a record's view.
     *
     * @param view The record's view
     * @return The field's value
     * @throws DataCodecException If a required field is missing or the value
     *     is malformed
     */
    T decode(DataView view) throws DataCodecException;

    /**
     * Writes the field of a record into the record's view.
     *
     * @param record The record
     * @param view The record's view
     * @throws DataCodecException If the value cannot be encoded portably
     */
    void encode(O record, DataView view) throws DataCodecException;
}
