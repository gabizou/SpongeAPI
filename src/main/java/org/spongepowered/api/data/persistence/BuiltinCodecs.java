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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.UUID;

/** The built-in codecs that need more than a one-line conversion. */
final class BuiltinCodecs {

    private static final DataQuery UUID_MOST = DataQuery.of("UuidMost");
    private static final DataQuery UUID_LEAST = DataQuery.of("UuidLeast");

    private BuiltinCodecs() {
    }

    static int[] encodeIntArray(final int[] value) {
        return value.clone();
    }

    static int[] decodeIntArray(final Object data) {
        if (data instanceof final int[] ints) {
            return ints.clone();
        }
        final List<?> list = BuiltinCodecs.elements(data, "an int[] or a List of ints");
        final int[] result = new int[list.size()];
        for (int i = 0; i < result.length; i++) {
            try {
                result[i] = ExactValues.toInt(list.get(i));
            } catch (final DataCodecException e) {
                throw e.atIndex(i);
            }
        }
        return result;
    }

    static List<Long> encodeLongArray(final long[] value) {
        return Arrays.stream(value).boxed().toList();
    }

    static long[] decodeLongArray(final Object data) {
        if (data instanceof final long[] longs) {
            return longs.clone();
        }
        if (data instanceof final int[] ints) {
            return Arrays.stream(ints).asLongStream().toArray();
        }
        final List<?> list = BuiltinCodecs.elements(data, "a long[] or a List of longs");
        final long[] result = new long[list.size()];
        for (int i = 0; i < result.length; i++) {
            try {
                result[i] = ExactValues.toLong(list.get(i));
            } catch (final DataCodecException e) {
                throw e.atIndex(i);
            }
        }
        return result;
    }

    static List<Byte> encodeByteArray(final byte[] value) {
        final List<Byte> list = new ArrayList<>(value.length);
        for (final byte b : value) {
            list.add(b);
        }
        return list;
    }

    static byte[] decodeByteArray(final Object data) {
        if (data instanceof final byte[] bytes) {
            return bytes.clone();
        }
        final List<?> list = BuiltinCodecs.elements(data, "a byte[] or a List of bytes");
        final byte[] result = new byte[list.size()];
        for (int i = 0; i < result.length; i++) {
            try {
                result[i] = ExactValues.toByte(list.get(i));
            } catch (final DataCodecException e) {
                throw e.atIndex(i);
            }
        }
        return result;
    }

    // Minecraft's order: most significant int first
    static int[] encodeUuid(final UUID value) {
        final long most = value.getMostSignificantBits();
        final long least = value.getLeastSignificantBits();
        return new int[] {(int) (most >> 32), (int) most, (int) (least >> 32), (int) least};
    }

    static UUID decodeUuid(final Object data) {
        if (data instanceof final String string) {
            try {
                return UUID.fromString(string);
            } catch (final IllegalArgumentException e) {
                throw new DataCodecException("a UUID", data);
            }
        }
        if (data instanceof final DataView view) {
            final OptionalLong most = view.exactLong(BuiltinCodecs.UUID_MOST);
            final OptionalLong least = view.exactLong(BuiltinCodecs.UUID_LEAST);
            if (most.isPresent() && least.isPresent()) {
                return new UUID(most.getAsLong(), least.getAsLong());
            }
            throw new DataCodecException("a UUID (a view with UuidMost and UuidLeast)", data);
        }
        if (data instanceof int[] || data instanceof List<?>) {
            final int[] ints = BuiltinCodecs.decodeIntArray(data);
            if (ints.length == 4) {
                return new UUID((long) ints[0] << 32 | ints[1] & 0xFFFFFFFFL, (long) ints[2] << 32 | ints[3] & 0xFFFFFFFFL);
            }
        }
        throw new DataCodecException("a UUID (four ints, a UUID string, or a view with UuidMost and UuidLeast)", data);
    }

    static <E extends Enum<E>> DataCodec<E> ofEnum(final Class<E> type) {
        final E[] constants = type.getEnumConstants();
        return DataCodec.of(Enum::name, data -> {
            final String name = ExactValues.toStringValue(data);
            return Arrays.stream(constants).filter(constant -> constant.name().equals(name)).findFirst()
                .orElseThrow(() -> new DataCodecException("one of " + Arrays.toString(constants), data));
        });
    }

    /** The elements of a List or a primitive array. */
    static List<?> elements(final Object data, final String expected) {
        if (data instanceof final List<?> list) {
            return list;
        }
        if (data.getClass().isArray() && data.getClass().getComponentType().isPrimitive()) {
            final List<Object> list = new ArrayList<>(Array.getLength(data));
            for (int i = 0; i < Array.getLength(data); i++) {
                list.add(Array.get(data, i));
            }
            return list;
        }
        throw new DataCodecException(expected, data);
    }

    static Optional<Object> get(final DataView view, final String key) {
        return view.get(DataQuery.of(key));
    }
}
