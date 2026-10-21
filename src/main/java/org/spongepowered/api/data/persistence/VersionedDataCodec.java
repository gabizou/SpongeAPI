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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.function.UnaryOperator;

/** The {@link VersionedCodec} created by {@link VersionedCodec#builder}. */
final class VersionedDataCodec<O> implements VersionedCodec<O> {

    private final RecordCodec<O> codec;
    private final int version;
    private final String versionKey;
    private final OptionalInt versionWhenMissing;
    private final Map<Integer, DataContentUpdater> updaters;
    private final List<String> keys;

    private VersionedDataCodec(final BuilderImpl<O> builder) {
        this.codec = builder.codec;
        this.version = builder.version;
        this.versionKey = builder.versionKey;
        this.versionWhenMissing = builder.versionWhenMissing;
        this.updaters = Map.copyOf(builder.updaters);
        final List<String> keys = new ArrayList<>(this.codec.keys());
        keys.add(this.versionKey);
        this.keys = List.copyOf(keys);
    }

    @Override
    public DataContainer encode(final O value) {
        final DataContainer container = this.codec.encode(value);
        container.set(DataQuery.of(this.versionKey), this.version);
        return container;
    }

    @Override
    public O decode(final Object data) {
        final DataView view = ExactValues.toView(data);
        final DataQuery versionPath = DataQuery.of(this.versionKey);
        final OptionalInt stored = view.exactInt(versionPath);
        if (stored.isEmpty() && this.versionWhenMissing.isEmpty()) {
            throw new DataCodecException("a version (an int)", null).at(versionPath);
        }
        int current = stored.isPresent() ? stored.getAsInt() : this.versionWhenMissing.getAsInt();
        if (current > this.version) {
            throw new DataCodecException("Data version " + current + " is newer than " + this.version
                + ", the newest version this codec reads").at(versionPath);
        }
        DataView content = view;
        if (current < this.version) {
            content = view.copy();
            while (current < this.version) {
                final DataContentUpdater updater = this.updaters.get(current);
                if (updater == null) {
                    throw new DataCodecException("No updater reads version " + current).at(versionPath);
                }
                content = Objects.requireNonNull(updater.update(content), "An updater returned null");
                current = updater.outputVersion();
            }
        }
        return this.codec.decode(content);
    }

    @Override
    public List<String> keys() {
        return this.keys;
    }

    @Override
    public int version() {
        return this.version;
    }

    @Override
    public String versionKey() {
        return this.versionKey;
    }

    static final class BuilderImpl<O> implements VersionedCodec.Builder<O> {

        private final RecordCodec<O> codec;
        private final int version;
        private String versionKey = Queries.CONTENT_VERSION.asString('.');
        private OptionalInt versionWhenMissing = OptionalInt.empty();
        private final Map<Integer, DataContentUpdater> updaters = new HashMap<>();

        BuilderImpl(final RecordCodec<O> codec, final int version) {
            this.codec = codec;
            this.version = version;
        }

        @Override
        public Builder<O> versionKey(final String key) {
            this.versionKey = PortableValues.requireValidKey(Objects.requireNonNull(key, "key"));
            return this;
        }

        @Override
        public Builder<O> versionWhenMissing(final int version) {
            this.versionWhenMissing = OptionalInt.of(version);
            return this;
        }

        @Override
        public Builder<O> updater(final DataContentUpdater updater) {
            Objects.requireNonNull(updater, "updater");
            if (updater.outputVersion() <= updater.inputVersion()) {
                throw new IllegalArgumentException("An updater must increase the version, but goes from "
                    + updater.inputVersion() + " to " + updater.outputVersion());
            }
            if (updater.outputVersion() > this.version) {
                throw new IllegalArgumentException("An updater goes to version " + updater.outputVersion()
                    + ", past the current version " + this.version);
            }
            if (this.updaters.putIfAbsent(updater.inputVersion(), updater) != null) {
                throw new IllegalArgumentException("Two updaters read version " + updater.inputVersion());
            }
            return this;
        }

        @Override
        public Builder<O> updater(final int fromVersion, final int toVersion, final UnaryOperator<DataView> update) {
            Objects.requireNonNull(update, "update");
            return this.updater(new DataContentUpdater() {
                @Override
                public int inputVersion() {
                    return fromVersion;
                }

                @Override
                public int outputVersion() {
                    return toVersion;
                }

                @Override
                public DataView update(final DataView content) {
                    return update.apply(content);
                }
            });
        }

        @Override
        public VersionedCodec<O> build() {
            if (this.codec.keys().contains(this.versionKey)) {
                throw new IllegalStateException("The version key '" + this.versionKey + "' is also a key of the record");
            }
            for (final int input : this.updaters.keySet()) {
                this.checkReachesCurrent(input);
            }
            if (this.versionWhenMissing.isPresent()) {
                final int missing = this.versionWhenMissing.getAsInt();
                if (missing > this.version) {
                    throw new IllegalStateException("The version for data without one, " + missing
                        + ", is newer than the current version " + this.version);
                }
                this.checkReachesCurrent(missing);
            }
            return new VersionedDataCodec<>(this);
        }

        private void checkReachesCurrent(final int from) {
            int current = from;
            while (current < this.version) {
                final DataContentUpdater updater = this.updaters.get(current);
                if (updater == null) {
                    throw new IllegalStateException("Data at version " + from + " cannot be updated to version "
                        + this.version + ": no updater reads version " + current);
                }
                current = updater.outputVersion();
            }
        }
    }
}
