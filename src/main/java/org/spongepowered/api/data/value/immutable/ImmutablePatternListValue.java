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
package org.spongepowered.api.data.value.immutable;

import org.spongepowered.api.data.manipulator.mutable.tileentity.BannerData;
import org.spongepowered.api.data.type.BannerPatternShape;
import org.spongepowered.api.data.type.DyeColor;
import org.spongepowered.api.data.value.ValueContainer;
import org.spongepowered.api.data.value.mutable.PatternListValue;

import java.util.List;

public interface ImmutablePatternListValue<H extends ValueContainer<H>> extends ImmutableCollectionValue<BannerData.PatternLayer, List<BannerData.PatternLayer>, ImmutablePatternListValue<H>, PatternListValue<H>, H> {


    ImmutablePatternListValue<H> get(int index);

    ImmutablePatternListValue<H> with(BannerPatternShape patternShape, DyeColor color);

    ImmutablePatternListValue<H> with(int index, BannerPatternShape patternShape, DyeColor color);

    ImmutablePatternListValue<H> with(int index, BannerData.PatternLayer value);

    ImmutablePatternListValue<H> with(int index, Iterable<BannerData.PatternLayer> values);

    ImmutablePatternListValue<H> without(int index);

    ImmutablePatternListValue<H> set(int index, BannerData.PatternLayer element);

    ImmutablePatternListValue<H> set(int index, BannerPatternShape patternShape, DyeColor color);

    int indexOf(BannerData.PatternLayer element);


}
