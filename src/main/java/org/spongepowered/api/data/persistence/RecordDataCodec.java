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

import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/** The {@link RecordCodec} created by {@link DataCodec#record}. */
final class RecordDataCodec<O> implements RecordCodec<O> {

    private final Function<RecordCodec.Fields<O>, ? extends O> factory;
    private final List<FieldCodec<O, ?>> fields;
    private final List<String> keys;

    RecordDataCodec(final Function<RecordCodec.Fields<O>, ? extends O> factory, final List<FieldCodec<O, ?>> fields) {
        final Set<String> seen = new HashSet<>();
        for (final FieldCodec<O, ?> field : fields) {
            if (!seen.add(field.key())) {
                throw new IllegalArgumentException("Two fields use the key '" + field.key() + "'");
            }
        }
        this.factory = factory;
        this.fields = fields;
        this.keys = fields.stream().map(FieldCodec::key).toList();
    }

    @Override
    public DataContainer encode(final O value) {
        final DataContainer container = DataContainer.createNew();
        for (final FieldCodec<O, ?> field : this.fields) {
            field.encode(value, container);
        }
        return container;
    }

    @Override
    public O decode(final Object data) {
        final DataView view = ExactValues.toView(data);
        final Map<FieldCodec<O, ?>, Object> values = new IdentityHashMap<>();
        for (final FieldCodec<O, ?> field : this.fields) {
            values.put(field, field.decode(view));
        }
        return this.factory.apply(new Fields<O>() {
            @Override
            @SuppressWarnings("unchecked")
            public <T> T get(final FieldCodec<O, T> field) {
                if (!values.containsKey(field)) {
                    throw new IllegalArgumentException("The field '" + field.key() + "' is not a field of this record");
                }
                return (T) values.get(field);
            }
        });
    }

    @Override
    public List<String> keys() {
        return this.keys;
    }
}
