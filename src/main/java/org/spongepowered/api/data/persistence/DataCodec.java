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
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

/**
 * Converts values of type {@code T} to and from values that a
 * {@link DataView} can store, so that they can be written to and read back
 * from every {@link DataFormat} without loss.
 *
 * <p><strong>Encoding</strong> produces only <em>portable</em> values, the
 * ones that every data format stores and reads back: {@link Boolean},
 * {@link Byte}, {@link Short}, {@link Integer}, {@link Long}, finite
 * {@link Float}s and {@link Double}s, {@link String}s of at most 65535 bytes
 * in modified UTF-8, {@code int[]}, {@link List}s of portable values, and
 * {@link DataView}s whose keys are valid and whose values are portable. A
 * value that cannot be encoded portably is rejected with a
 * {@link DataCodecException} when it is encoded, instead of failing or
 * silently changing later when the data is saved. Keys must not be empty,
 * contain {@code $Boolean}, or equal {@code _arraytype}, which data formats
 * use internally.</p>
 *
 * <p><strong>Decoding</strong> is strict about types and lenient about
 * number width. A value of the wrong type, or out of range, causes a
 * {@link DataCodecException} that names its path; it is never converted or
 * truncated. Integral numbers are accepted at any width that holds the
 * value, because formats such as JSON read a stored {@link Byte} or small
 * {@link Long} back as an {@link Integer}.</p>
 *
 * <p><strong>Absence</strong> is represented by an omitted key, never by
 * {@code null}: use {@link #optionalFieldOf(String, Function)} for values
 * that may be missing. A key that is present but holds a malformed value is
 * an error, not absence.</p>
 *
 * <p>Codecs for structured values are built with
 * {@link #record(Function, FieldCodec[])}:</p>
 *
 * <pre>{@code
 * record Pet(String name, Optional<Integer> age, List<String> tags) {
 *     static final FieldCodec<Pet, String> NAME = DataCodec.STRING.fieldOf("name", Pet::name);
 *     static final FieldCodec<Pet, Optional<Integer>> AGE = DataCodec.INT.optionalFieldOf("age", Pet::age);
 *     static final FieldCodec<Pet, List<String>> TAGS = DataCodec.STRING.listOf().optionalFieldOf("tags", List.of(), Pet::tags);
 *     static final RecordCodec<Pet> CODEC = DataCodec.record(f -> new Pet(f.get(NAME), f.get(AGE), f.get(TAGS)), NAME, AGE, TAGS);
 * }
 * }</pre>
 *
 * <p>Use {@link VersionedCodec} to store a version with the data and update
 * older data when it is read.</p>
 *
 * @param <T> The type of value this codec converts
 */
public interface DataCodec<T> {

    /**
     * A {@link Boolean}. Decoding also accepts an integral {@code 0} or
     * {@code 1}, which is how Minecraft stores booleans in NBT.
     */
    DataCodec<Boolean> BOOLEAN = DataCodec.of(value -> value, ExactValues::toBoolean);

    /** A {@code byte}. Decoding accepts any integral number in range. */
    DataCodec<Byte> BYTE = DataCodec.of(value -> value, ExactValues::toByte);

    /** A {@code short}. Decoding accepts any integral number in range. */
    DataCodec<Short> SHORT = DataCodec.of(value -> value, ExactValues::toShort);

    /** An {@code int}. Decoding accepts any integral number in range. */
    DataCodec<Integer> INT = DataCodec.of(value -> value, ExactValues::toInt);

    /** A {@code long}. Decoding accepts any integral number. */
    DataCodec<Long> LONG = DataCodec.of(value -> value, ExactValues::toLong);

    /**
     * A finite {@code float}. Encoding rejects NaN and infinities, which JSON
     * cannot store. Decoding also accepts a {@link Double} within range.
     */
    DataCodec<Float> FLOAT = DataCodec.of(PortableValues::finite, ExactValues::toFloat);

    /**
     * A finite {@code double}. Encoding rejects NaN and infinities, which JSON
     * cannot store. Decoding also accepts a {@link Float}.
     */
    DataCodec<Double> DOUBLE = DataCodec.of(PortableValues::finite, ExactValues::toDouble);

    /**
     * A {@link String} of at most 65535 bytes in modified UTF-8, the most NBT
     * can store.
     */
    DataCodec<String> STRING = DataCodec.of(PortableValues::string, ExactValues::toStringValue);

    /** An {@code int[]}. Decoding also accepts a {@link List} of ints. */
    DataCodec<int[]> INT_ARRAY = DataCodec.of(BuiltinCodecs::encodeIntArray, BuiltinCodecs::decodeIntArray);

    /**
     * A {@code long[]}, stored as a {@link List} of longs because JSON cannot
     * read back a stored {@code long[]}. Decoding accepts a {@code long[]},
     * an {@code int[]} or a {@link List}.
     */
    DataCodec<long[]> LONG_ARRAY = DataCodec.of(BuiltinCodecs::encodeLongArray, BuiltinCodecs::decodeLongArray);

    /**
     * A {@code byte[]}, stored as a {@link List} of bytes because JSON cannot
     * read back a stored {@code byte[]}. Decoding accepts a {@code byte[]} or
     * a {@link List}.
     */
    DataCodec<byte[]> BYTE_ARRAY = DataCodec.of(BuiltinCodecs::encodeByteArray, BuiltinCodecs::decodeByteArray);

    /**
     * A {@link UUID}, stored as four ints like Minecraft does. Decoding also
     * accepts a {@link java.util.UUID#toString() UUID string} and a view with
     * {@code UuidMost} and {@code UuidLeast} longs.
     */
    DataCodec<UUID> UUID = DataCodec.of(BuiltinCodecs::encodeUuid, BuiltinCodecs::decodeUuid);

    /**
     * A {@link DataView}, passed through as it is. Encoding checks that its
     * keys and values are portable.
     */
    DataCodec<DataView> DATA_VIEW = DataCodec.of(PortableValues::view, ExactValues::toView);

    /**
     * Creates a codec from an encoding and a decoding function.
     *
     * <p>The decoder should throw a {@link DataCodecException} for data it
     * does not accept; the codecs that use it add the path. Encoded values
     * are checked for portability when they are written to a
     * {@link DataView}.</p>
     *
     * @param encoder Converts a value to a portable value
     * @param decoder Converts stored data to a value
     * @param <T> The type of value
     * @return The codec
     */
    static <T> DataCodec<T> of(final Function<? super T, ?> encoder, final Function<Object, ? extends T> decoder) {
        return new SimpleDataCodec<>(Objects.requireNonNull(encoder, "encoder"), Objects.requireNonNull(decoder, "decoder"));
    }

    /**
     * Creates a codec for an enum, stored as the constant's
     * {@link Enum#name() name}.
     *
     * @param type The enum class
     * @param <E> The enum type
     * @return The codec
     */
    static <E extends Enum<E>> DataCodec<E> ofEnum(final Class<E> type) {
        return BuiltinCodecs.ofEnum(Objects.requireNonNull(type, "type"));
    }

    /**
     * Creates a codec for a map with {@link String} keys, stored as a
     * {@link DataView}. Keys must be valid keys, see {@link DataCodec}.
     *
     * @param valueCodec The codec for the values
     * @param <V> The type of the values
     * @return The codec
     */
    static <V> DataCodec<Map<String, V>> mapOf(final DataCodec<V> valueCodec) {
        return new MapDataCodec<>(Objects.requireNonNull(valueCodec, "valueCodec"));
    }

    /**
     * Creates a codec for a structured value, stored as a {@link DataView}
     * with one key per field.
     *
     * <p>Decoding reads every field first, so the first malformed field is
     * reported with its path, and then passes the values to the
     * {@code factory}. Keys that are not fields are ignored.</p>
     *
     * @param factory Creates the value from its decoded fields
     * @param fields The fields, each with a distinct key
     * @param <O> The type of value
     * @return The codec
     */
    @SafeVarargs
    static <O> RecordCodec<O> record(final Function<RecordCodec.Fields<O>, ? extends O> factory, final FieldCodec<O, ?>... fields) {
        return new RecordDataCodec<>(Objects.requireNonNull(factory, "factory"), List.of(fields));
    }

    /**
     * Converts a value to portable data.
     *
     * @param value The value
     * @return The encoded data
     * @throws DataCodecException If the value cannot be encoded portably
     */
    Object encode(T value) throws DataCodecException;

    /**
     * Converts stored data to a value.
     *
     * @param data The stored data
     * @return The value
     * @throws DataCodecException If the data is not valid for this codec
     */
    T decode(Object data) throws DataCodecException;

    /**
     * Reads the value at a path of a view.
     *
     * <p>Absent is returned only if nothing is stored at the path. A part of
     * the path before the last that holds a value other than a
     * {@link DataView} is an error, as is a malformed value.</p>
     *
     * @param view The view to read from
     * @param path The path of the value
     * @return The decoded value, if present
     * @throws DataCodecException If the value is malformed, or the path passes
     *     through a non-view value
     * @throws IllegalArgumentException If the path is empty
     */
    default Optional<T> read(final DataView view, final DataQuery path) throws DataCodecException {
        final List<String> parts = path.parts();
        if (parts.isEmpty()) {
            throw new IllegalArgumentException("The path is empty");
        }
        final Optional<DataView> parent = parts.size() > 1 ? view.exactView(DataQuery.of(parts.subList(0, parts.size() - 1))) : Optional.of(view);
        final Optional<Object> data = parent.flatMap(p -> p.get(DataQuery.of(parts.getLast())));
        if (data.isEmpty()) {
            return Optional.empty();
        }
        try {
            return Optional.of(this.decode(data.get()));
        } catch (final DataCodecException e) {
            throw e.at(path);
        }
    }

    /**
     * Encodes a value and writes it at a path of a view.
     *
     * <p>Views are created for missing parts of the path. A part that already
     * holds a value other than a {@link DataView} is an error rather than
     * being replaced.</p>
     *
     * @param view The view to write to
     * @param path The path to write at
     * @param value The value
     * @throws DataCodecException If the value cannot be encoded portably, or
     *     the path passes through a non-view value
     * @throws IllegalArgumentException If the path is empty or one of its
     *     parts is not a valid key
     */
    default void write(final DataView view, final DataQuery path, final T value) throws DataCodecException {
        Objects.requireNonNull(value, "value");
        final List<String> parts = path.parts();
        if (parts.isEmpty()) {
            throw new IllegalArgumentException("The path is empty");
        }
        parts.forEach(PortableValues::requireValidKey);
        final Object encoded;
        try {
            encoded = PortableValues.check(this.encode(value));
        } catch (final DataCodecException e) {
            throw e.at(path);
        }
        DataView target = view;
        for (int i = 0; i < parts.size() - 1; i++) {
            final DataQuery part = DataQuery.of(parts.get(i));
            final Optional<DataView> existing;
            try {
                existing = target.exactView(part);
            } catch (final DataCodecException e) {
                throw e.at(DataQuery.of(parts.subList(0, i)));
            }
            target = existing.isPresent() ? existing.get() : target.createView(part);
        }
        target.set(DataQuery.of(parts.getLast()), encoded);
    }

    /**
     * Creates a codec for lists of this codec's values, stored as a
     * {@link List}. Decoding also accepts a primitive array.
     *
     * @return The codec
     */
    default DataCodec<List<T>> listOf() {
        return new ListDataCodec<>(this);
    }

    /**
     * Creates a codec for another type that converts through this one.
     *
     * @param to Converts a decoded value of this codec to the new type
     * @param from Converts a value of the new type for encoding with this
     *     codec
     * @param <U> The new type
     * @return The codec
     */
    default <U> DataCodec<U> map(final Function<? super T, ? extends U> to, final Function<? super U, ? extends T> from) {
        Objects.requireNonNull(to, "to");
        Objects.requireNonNull(from, "from");
        return DataCodec.of(value -> this.encode(from.apply(value)), data -> to.apply(this.decode(data)));
    }

    /**
     * Creates a required field for a {@link #record(Function, FieldCodec[])
     * record} codec.
     *
     * @param key The key of the field
     * @param getter Gets the field's value from the record
     * @param <O> The type of the record
     * @return The field
     * @throws IllegalArgumentException If the key is not valid
     */
    default <O> FieldCodec<O, T> fieldOf(final String key, final Function<? super O, ? extends T> getter) {
        return FieldCodecs.required(key, this, getter);
    }

    /**
     * Creates an optional field for a {@link #record(Function, FieldCodec[])
     * record} codec. An empty value is written as an omitted key, and an
     * omitted key is read as empty.
     *
     * @param key The key of the field
     * @param getter Gets the field's value from the record
     * @param <O> The type of the record
     * @return The field
     * @throws IllegalArgumentException If the key is not valid
     */
    default <O> FieldCodec<O, Optional<T>> optionalFieldOf(final String key, final Function<? super O, Optional<T>> getter) {
        return FieldCodecs.optional(key, this, getter);
    }

    /**
     * Creates a field with a default value for a
     * {@link #record(Function, FieldCodec[]) record} codec. An omitted key is
     * read as the default, and a value equal to the default is written as an
     * omitted key.
     *
     * @param key The key of the field
     * @param defaultValue The default value
     * @param getter Gets the field's value from the record
     * @param <O> The type of the record
     * @return The field
     * @throws IllegalArgumentException If the key is not valid
     */
    default <O> FieldCodec<O, T> optionalFieldOf(final String key, final T defaultValue, final Function<? super O, ? extends T> getter) {
        return FieldCodecs.withDefault(key, this, defaultValue, getter);
    }
}
