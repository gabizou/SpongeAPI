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
import java.util.Optional;
import java.util.function.Function;

/** The fields created by {@link DataCodec#fieldOf} and {@link DataCodec#optionalFieldOf}. */
final class FieldCodecs {

    private FieldCodecs() {
    }

    static <O, T> FieldCodec<O, T> required(final String key, final DataCodec<T> codec, final Function<? super O, ? extends T> getter) {
        PortableValues.requireValidKey(Objects.requireNonNull(key, "key"));
        Objects.requireNonNull(getter, "getter");
        return new FieldCodec<>() {
            @Override
            public String key() {
                return key;
            }

            @Override
            public T decode(final DataView view) {
                final Object data = BuiltinCodecs.get(view, key).orElseThrow(() -> new DataCodecException("a value", null).at(DataQuery.of(key)));
                return FieldCodecs.decode(codec, key, data);
            }

            @Override
            public void encode(final O record, final DataView view) {
                final T value = getter.apply(record);
                if (value == null) {
                    throw new DataCodecException("a value", null).at(DataQuery.of(key));
                }
                FieldCodecs.write(codec, key, value, view);
            }
        };
    }

    static <O, T> FieldCodec<O, Optional<T>> optional(final String key, final DataCodec<T> codec, final Function<? super O, Optional<T>> getter) {
        PortableValues.requireValidKey(Objects.requireNonNull(key, "key"));
        Objects.requireNonNull(getter, "getter");
        return new FieldCodec<>() {
            @Override
            public String key() {
                return key;
            }

            @Override
            public Optional<T> decode(final DataView view) {
                return BuiltinCodecs.get(view, key).map(data -> FieldCodecs.decode(codec, key, data));
            }

            @Override
            public void encode(final O record, final DataView view) {
                final Optional<T> value = Objects.requireNonNull(getter.apply(record), "Optional field '" + key + "' returned null");
                value.ifPresent(v -> FieldCodecs.write(codec, key, v, view));
            }
        };
    }

    static <O, T> FieldCodec<O, T> withDefault(final String key, final DataCodec<T> codec, final T defaultValue,
        final Function<? super O, ? extends T> getter) {
        PortableValues.requireValidKey(Objects.requireNonNull(key, "key"));
        Objects.requireNonNull(defaultValue, "defaultValue");
        Objects.requireNonNull(getter, "getter");
        return new FieldCodec<>() {
            @Override
            public String key() {
                return key;
            }

            @Override
            public T decode(final DataView view) {
                return BuiltinCodecs.get(view, key).map(data -> FieldCodecs.decode(codec, key, data)).orElse(defaultValue);
            }

            @Override
            public void encode(final O record, final DataView view) {
                final T value = getter.apply(record);
                if (value == null) {
                    throw new DataCodecException("a value", null).at(DataQuery.of(key));
                }
                if (!value.equals(defaultValue)) {
                    FieldCodecs.write(codec, key, value, view);
                }
            }
        };
    }

    private static <T> T decode(final DataCodec<T> codec, final String key, final Object data) {
        try {
            return codec.decode(data);
        } catch (final DataCodecException e) {
            throw e.at(DataQuery.of(key));
        }
    }

    private static <T> void write(final DataCodec<T> codec, final String key, final T value, final DataView view) {
        final DataQuery path = DataQuery.of(key);
        final Object encoded;
        try {
            encoded = PortableValues.check(codec.encode(value));
        } catch (final DataCodecException e) {
            throw e.at(path);
        }
        view.set(path, encoded);
    }
}
