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
 * The strict conversions shared by the {@code DataView#exact*} getters and
 * the built-in {@link DataCodec}s.
 *
 * <p>Integral targets accept any {@link Byte}, {@link Short}, {@link Integer}
 * or {@link Long} within range, because formats such as JSON change the
 * width of stored numbers. Nothing is truncated and strings are never
 * parsed.</p>
 */
final class ExactValues {

    private ExactValues() {
    }

    static boolean isIntegral(final Object value) {
        return value instanceof Byte || value instanceof Short || value instanceof Integer || value instanceof Long;
    }

    static long integral(final Object value, final long min, final long max, final String expected) {
        if (ExactValues.isIntegral(value)) {
            final long l = ((Number) value).longValue();
            if (l >= min && l <= max) {
                return l;
            }
        }
        throw new DataCodecException(expected, value);
    }

    static byte toByte(final Object value) {
        return (byte) ExactValues.integral(value, Byte.MIN_VALUE, Byte.MAX_VALUE, "a byte");
    }

    static short toShort(final Object value) {
        return (short) ExactValues.integral(value, Short.MIN_VALUE, Short.MAX_VALUE, "a short");
    }

    static int toInt(final Object value) {
        return (int) ExactValues.integral(value, Integer.MIN_VALUE, Integer.MAX_VALUE, "an int");
    }

    static long toLong(final Object value) {
        return ExactValues.integral(value, Long.MIN_VALUE, Long.MAX_VALUE, "a long");
    }

    // Minecraft stores booleans as byte tags, and JSON reads those back as integers
    static boolean toBoolean(final Object value) {
        if (value instanceof final Boolean bool) {
            return bool;
        }
        if (ExactValues.isIntegral(value)) {
            final long l = ((Number) value).longValue();
            if (l == 0 || l == 1) {
                return l == 1;
            }
        }
        throw new DataCodecException("a Boolean or an integral 0 or 1", value);
    }

    static float toFloat(final Object value) {
        if (value instanceof final Float f) {
            return f;
        }
        if (value instanceof final Double d) {
            if (!Double.isFinite(d) || Math.abs(d) <= Float.MAX_VALUE) {
                return d.floatValue();
            }
            throw new DataCodecException("a float", value);
        }
        if (ExactValues.isIntegral(value)) {
            final long l = ((Number) value).longValue();
            final float f = (float) l;
            // 0x1p63f is out of long range, so (long) f saturates; exclude it explicitly
            if (f != 0x1p63f && (long) f == l) {
                return f;
            }
        }
        throw new DataCodecException("a float", value);
    }

    static double toDouble(final Object value) {
        if (value instanceof final Double d) {
            return d;
        }
        if (value instanceof final Float f) {
            return f;
        }
        if (ExactValues.isIntegral(value)) {
            final long l = ((Number) value).longValue();
            final double d = (double) l;
            // 0x1p63 is out of long range, so (long) d saturates; exclude it explicitly
            if (d != 0x1p63 && (long) d == l) {
                return d;
            }
        }
        throw new DataCodecException("a double", value);
    }

    static String toStringValue(final Object value) {
        if (value instanceof final String string) {
            return string;
        }
        throw new DataCodecException("a String", value);
    }

    static DataView toView(final Object value) {
        if (value instanceof final DataView view) {
            return view;
        }
        throw new DataCodecException("a DataView", value);
    }
}
