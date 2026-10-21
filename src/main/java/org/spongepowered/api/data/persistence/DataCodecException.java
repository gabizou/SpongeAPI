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

import org.checkerframework.checker.nullness.qual.Nullable;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * An {@link InvalidDataException} raised when a value cannot be read or
 * written strictly, by a {@link DataCodec} or a {@code DataView#exact*}
 * getter.
 *
 * <p>The exception records where the problem is. As it travels up through
 * nested codecs, each level adds its key or list index with
 * {@link #at(DataQuery)} or {@link #atIndex(int)}, so the message ends up
 * naming the full path, for example
 * {@code Expected an int at 'pets[1].age' but found String: x}.</p>
 */
public class DataCodecException extends InvalidDataException {

    private static final long serialVersionUID = 5260195932306287381L;
    private static final int MAX_FOUND_LENGTH = 100;

    private final @Nullable String expected;
    private final @Nullable String found;
    private final @Nullable String detail;
    private final List<String> path = new ArrayList<>();

    /**
     * Creates an exception for a value that is not what was expected.
     *
     * @param expected What was expected, for example {@code "an int"}
     * @param found The value that was found, or {@code null} if nothing was
     *     present
     */
    public DataCodecException(final String expected, final @Nullable Object found) {
        this.expected = expected;
        this.found = DataCodecException.describe(found);
        this.detail = null;
    }

    /**
     * Creates an exception with a free-form message.
     *
     * @param detail The message
     */
    public DataCodecException(final String detail) {
        this.expected = null;
        this.found = null;
        this.detail = detail;
    }

    /**
     * Adds the given path in front of the path already recorded.
     *
     * @param path The path of the value relative to the enclosing view
     * @return This exception, for chaining
     */
    public DataCodecException at(final DataQuery path) {
        this.path.addAll(0, path.parts());
        return this;
    }

    /**
     * Adds a list index in front of the path already recorded.
     *
     * @param index The index of the element in the enclosing list
     * @return This exception, for chaining
     */
    public DataCodecException atIndex(final int index) {
        this.path.add(0, "[" + index + "]");
        return this;
    }

    /**
     * Gets the path of the value this exception is about, for example
     * {@code pets[1].age}, or an empty string for a top-level value.
     *
     * @return The path
     */
    public String path() {
        final StringBuilder builder = new StringBuilder();
        for (final String part : this.path) {
            if (!part.startsWith("[") && !builder.isEmpty()) {
                builder.append('.');
            }
            builder.append(part);
        }
        return builder.toString();
    }

    @Override
    public String getMessage() {
        final String path = this.path();
        if (this.detail != null) {
            return path.isEmpty() ? this.detail : "At '" + path + "': " + this.detail;
        }
        return "Expected " + this.expected + (path.isEmpty() ? "" : " at '" + path + "'") + " but found " + this.found;
    }

    private static String describe(final @Nullable Object found) {
        if (found == null) {
            return "nothing";
        }
        final String value;
        if (found.getClass().isArray()) {
            final StringBuilder builder = new StringBuilder("[");
            for (int i = 0; i < Array.getLength(found); i++) {
                builder.append(i == 0 ? "" : ", ").append(Array.get(found, i));
            }
            value = builder.append(']').toString();
        } else {
            value = String.valueOf(found);
        }
        final String shortened = value.length() > DataCodecException.MAX_FOUND_LENGTH
            ? value.substring(0, DataCodecException.MAX_FOUND_LENGTH) + "..." : value;
        return found.getClass().getSimpleName() + ": " + shortened;
    }
}
