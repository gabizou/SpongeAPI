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
package org.spongepowered.api.text.format;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.generator.dummy.DummyObjectProvider;

import java.util.Optional;

import javax.annotation.Nullable;

/**
 * Represents a list of the text styles provided by Vanilla Minecraft.
 */
public final class TextStyles {

    private TextStyles() {
    }

    /**
     * Represents an empty {@link TextStyle}.
     */
    public static final TextStyle NONE = DummyObjectProvider.createFor(TextStyle.class, "NONE");

    public static final TextStyle OBFUSCATED = DummyObjectProvider.createFor(TextStyle.class, "OBFUSCATED");
    public static final TextStyle BOLD = DummyObjectProvider.createFor(TextStyle.class, "BOLD");
    public static final TextStyle STRIKETHROUGH = DummyObjectProvider.createFor(TextStyle.class, "STRIKETHROUGH");
    public static final TextStyle UNDERLINE = DummyObjectProvider.createFor(TextStyle.class, "UNDERLINE");
    public static final TextStyle ITALIC = DummyObjectProvider.createFor(TextStyle.class, "ITALIC");

    /**
     * Represents a {@link TextStyle} with all bases set to {@code false}.
     */
    public static final TextStyle RESET = DummyObjectProvider.createFor(TextStyle.class, "RESET");

    /**
     * Returns an empty {@link TextStyle}.
     *
     * @return An empty text style
     */
    public static TextStyle of() {
        return NONE;
    }

    /**
     * Constructs a composite text style from the specified styles. This will
     * result in the same as calling {@link TextStyle#and(TextStyle...)} on all
     * of the text styles.
     *
     * @param styles The styles to combine
     * @return A composite text style from the specified styles
     */
    public static TextStyle of(TextStyle... styles) {
        return NONE.and(styles);
    }

}
