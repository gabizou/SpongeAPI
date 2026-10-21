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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/** A {@link DataCodec} for a map with String keys, stored as a {@link DataView}. */
final class MapDataCodec<V> implements DataCodec<Map<String, V>> {

    private final DataCodec<V> valueCodec;

    MapDataCodec(final DataCodec<V> valueCodec) {
        this.valueCodec = valueCodec;
    }

    @Override
    public DataContainer encode(final Map<String, V> value) {
        final DataContainer container = DataContainer.createNew();
        for (final Map.Entry<String, V> entry : value.entrySet()) {
            final DataQuery key = DataQuery.of(entry.getKey());
            try {
                PortableValues.checkKey(entry.getKey());
                container.set(key, PortableValues.check(this.valueCodec.encode(entry.getValue())));
            } catch (final DataCodecException e) {
                throw e.at(key);
            }
        }
        return container;
    }

    @Override
    public Map<String, V> decode(final Object data) {
        final DataView view = ExactValues.toView(data);
        final Map<String, V> decoded = new LinkedHashMap<>();
        for (final DataQuery key : view.keys(false)) {
            try {
                decoded.put(key.parts().getFirst(), this.valueCodec.decode(view.get(key).orElseThrow()));
            } catch (final DataCodecException e) {
                throw e.at(key);
            }
        }
        return Collections.unmodifiableMap(decoded);
    }
}
