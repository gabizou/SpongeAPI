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
package org.spongepowered.api.effect.particle;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.data.persistence.DataBuilder;
import org.spongepowered.api.util.weighted.WeightedTable;

import java.util.Random;
import java.util.function.Function;

public interface ParticleEffectGenerator extends Function<Random, ParticleEffect>, DataSerializable {

    /**
     * Creates a new {@link Builder} instance to construct a new
     * {@link ParticleEffectGenerator}.
     *
     * @return The builder instance
     */
    static Builder builder() {
        return Sponge.getRegistry().createBuilder(Builder.class);
    }

    /**
     * Gets the {@link ParticleType} that will be generated.
     *
     * @return The particle type
     */
    ParticleType getType();

    /**
     * Gets the {@link WeightedTable} of values that are valid for the
     * particular {@link ParticleOption} provided.
     *
     * @param option The particle option to retrieve the values for
     * @param <T> The type of values
     * @return The weighted table of values
     */
    <T> WeightedTable<T> getOptions(ParticleOption<T> option);

    /**
     * Creates a new {@link ParticleEffect} with the provided
     * {@link Random}. Semantically the same as {@link #apply(Object)}.
     *
     * @param random The random object provided
     * @return The newly constructed effect
     */
    default ParticleEffect build(Random random) {
        return apply(random);
    }


    /**
     * A builder that can generate a new {@link ParticleEffectGenerator} or
     * deserialize a {@link ParticleEffectGenerator}.
     */
    interface Builder extends DataBuilder<ParticleEffectGenerator> {

        /**
         * Sets the {@link ParticleType} to this builder.
         *
         * @param type The type
         * @return This builder, for chaining
         */
        Builder type(ParticleType type);

        /**
         *
         * @param option
         * @param value
         * @param <T>
         * @return
         */
        <T> Builder option(ParticleOption<T> option, T value);

        <T> Builder option(ParticleOption<T> option, T value, double weight);

        <T> Builder option(ParticleOption<T> option, T[] values);

        <T> Builder option(ParticleOption<T> option, Iterable<T> values);

        <T> Builder option(ParticleOption<T> option, WeightedTable<T> values);

        ParticleEffectGenerator build();

        @Override
        Builder reset();

        @Override
        Builder from(ParticleEffectGenerator value);
    }

}
