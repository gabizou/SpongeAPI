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

import java.util.Objects;
import java.util.function.UnaryOperator;

/**
 * A {@link RecordCodec} that stores a version number with the data and
 * updates older data when it is read.
 *
 * <p>Encoding writes the record with the current {@link #version()} under
 * {@link #versionKey()}. Decoding reads the stored version, applies the
 * {@link DataContentUpdater}s from that version up to the current one, in
 * order, to a copy of the data, and then decodes the result with the record
 * codec. The input is never modified.</p>
 *
 * <pre>{@code
 * // version 1 stored "years"; version 2 renamed it to "age"
 * static final VersionedCodec<Pet> VERSIONED = VersionedCodec.builder(Pet.CODEC, 2)
 *     .updater(1, 2, view -> view.set(DataQuery.of("age"), view.get(DataQuery.of("years")).orElseThrow())
 *         .remove(DataQuery.of("years")))
 *     .build();
 * }</pre>
 *
 * @param <O> The type of value
 */
public interface VersionedCodec<O> extends RecordCodec<O> {

    /**
     * Starts a versioned codec for a record codec.
     *
     * @param codec The codec for the current version of the data
     * @param version The current version, written when encoding
     * @param <O> The type of value
     * @return The builder
     */
    static <O> Builder<O> builder(final RecordCodec<O> codec, final int version) {
        return new VersionedDataCodec.BuilderImpl<>(Objects.requireNonNull(codec, "codec"), version);
    }

    /**
     * Gets the current version, which encoding writes and decoding updates
     * to.
     *
     * @return The current version
     */
    int version();

    /**
     * Gets the key the version is stored under.
     *
     * @return The key
     */
    String versionKey();

    /**
     * Builds a {@link VersionedCodec}.
     *
     * @param <O> The type of value
     */
    interface Builder<O> {

        /**
         * Sets the key the version is stored under. Defaults to
         * {@code ContentVersion}, the key {@link Queries#CONTENT_VERSION}
         * names. It must not be one of the record's keys.
         *
         * @param key The key
         * @return This builder, for chaining
         */
        Builder<O> versionKey(String key);

        /**
         * Sets the version to assume for data that has no version stored.
         * Without it, such data is rejected.
         *
         * @param version The version
         * @return This builder, for chaining
         */
        Builder<O> versionWhenMissing(int version);

        /**
         * Adds an updater from its input version to its output version.
         * Each input version may have only one updater, and the output must
         * be greater than the input and at most the current version.
         *
         * @param updater The updater
         * @return This builder, for chaining
         */
        Builder<O> updater(DataContentUpdater updater);

        /**
         * Adds an updater from one version to another.
         *
         * @param fromVersion The version the updater reads
         * @param toVersion The version the updater produces
         * @param update Updates a copy of the data, and returns it or a new
         *     view
         * @return This builder, for chaining
         * @see #updater(DataContentUpdater)
         */
        Builder<O> updater(int fromVersion, int toVersion, UnaryOperator<DataView> update);

        /**
         * Builds the codec.
         *
         * @return The codec
         * @throws IllegalStateException If the version key is one of the
         *     record's keys, or an updater's versions are invalid, or data at
         *     some version an updater reads could not be updated all the way
         *     to the current version
         */
        VersionedCodec<O> build();
    }
}
