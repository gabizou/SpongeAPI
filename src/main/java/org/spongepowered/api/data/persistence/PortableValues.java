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
 * Checks that encoded values are portable: stored and read back unchanged by
 * every data format. See {@link DataCodec} for the rules.
 */
final class PortableValues {

    /** NBT stores strings with {@link java.io.DataOutput#writeUTF(String)}, limited to 65535 bytes. */
    static final int MAX_STRING_BYTES = 65535;
    /** NBT stores booleans under this suffix, and strips it on read wherever it appears in a key. */
    private static final String NBT_BOOLEAN_MARKER = "$Boolean";
    /** JSON stores arrays as a view with this key, and reads any such view back as an array. */
    private static final String JSON_ARRAY_TYPE = "_arraytype";

    private PortableValues() {
    }

    static Number finite(final Number value) {
        if (!Double.isFinite(value.doubleValue())) {
            throw new DataCodecException("a finite number", value);
        }
        return value;
    }

    static String string(final String value) {
        final long bytes = PortableValues.modifiedUtf8Length(value);
        if (bytes > PortableValues.MAX_STRING_BYTES) {
            throw new DataCodecException("String is " + bytes + " bytes in modified UTF-8; at most "
                + PortableValues.MAX_STRING_BYTES + " can be stored");
        }
        return value;
    }

    static DataView view(final DataView value) {
        PortableValues.check(value);
        return value;
    }

    static boolean isValidKey(final String key) {
        return !key.isEmpty() && !key.contains(PortableValues.NBT_BOOLEAN_MARKER) && !key.equals(PortableValues.JSON_ARRAY_TYPE);
    }

    /** For keys chosen by code, such as field names: an invalid key is a programming error. */
    static String requireValidKey(final String key) {
        if (!PortableValues.isValidKey(key)) {
            throw new IllegalArgumentException("'" + key + "' is not a valid key: keys must not be empty, contain '"
                + PortableValues.NBT_BOOLEAN_MARKER + "' or equal '" + PortableValues.JSON_ARRAY_TYPE + "'");
        }
        return key;
    }

    /** For keys that come from data, such as map keys. */
    static String checkKey(final String key) {
        if (!PortableValues.isValidKey(key)) {
            throw new DataCodecException("a valid key (not empty, not containing '" + PortableValues.NBT_BOOLEAN_MARKER
                + "', not '" + PortableValues.JSON_ARRAY_TYPE + "')", key);
        }
        return key;
    }

    static Object check(final Object value) {
        if (value instanceof Boolean || value instanceof Byte || value instanceof Short || value instanceof Integer
            || value instanceof Long || value instanceof int[]) {
            return value;
        }
        if (value instanceof final Float f) {
            return PortableValues.finite(f);
        }
        if (value instanceof final Double d) {
            return PortableValues.finite(d);
        }
        if (value instanceof final String s) {
            return PortableValues.string(s);
        }
        if (value instanceof final List<?> list) {
            for (int i = 0; i < list.size(); i++) {
                try {
                    PortableValues.check(list.get(i));
                } catch (final DataCodecException e) {
                    throw e.atIndex(i);
                }
            }
            return value;
        }
        if (value instanceof final DataView view) {
            for (final DataQuery key : view.keys(false)) {
                try {
                    PortableValues.checkKey(key.parts().getFirst());
                    PortableValues.check(view.get(key).orElseThrow());
                } catch (final DataCodecException e) {
                    throw e.at(key);
                }
            }
            return value;
        }
        throw new DataCodecException("a portable value (Boolean, number, String, int[], List or DataView)", value);
    }

    private static long modifiedUtf8Length(final String value) {
        long bytes = 0;
        for (int i = 0; i < value.length(); i++) {
            final char c = value.charAt(i);
            bytes += c >= 0x0001 && c <= 0x007F ? 1 : c <= 0x07FF ? 2 : 3;
        }
        return bytes;
    }
}
