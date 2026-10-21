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
import java.util.List;

/** A {@link DataCodec} for a list of values of an element codec. */
final class ListDataCodec<T> implements DataCodec<List<T>> {

    private final DataCodec<T> elementCodec;

    ListDataCodec(final DataCodec<T> elementCodec) {
        this.elementCodec = elementCodec;
    }

    @Override
    public List<Object> encode(final List<T> value) {
        final List<Object> encoded = new ArrayList<>(value.size());
        for (int i = 0; i < value.size(); i++) {
            try {
                encoded.add(this.elementCodec.encode(value.get(i)));
            } catch (final DataCodecException e) {
                throw e.atIndex(i);
            }
        }
        return encoded;
    }

    @Override
    public List<T> decode(final Object data) {
        final List<?> elements = BuiltinCodecs.elements(data, "a List");
        final List<T> decoded = new ArrayList<>(elements.size());
        for (int i = 0; i < elements.size(); i++) {
            try {
                decoded.add(this.elementCodec.decode(elements.get(i)));
            } catch (final DataCodecException e) {
                throw e.atIndex(i);
            }
        }
        return List.copyOf(decoded);
    }
}
