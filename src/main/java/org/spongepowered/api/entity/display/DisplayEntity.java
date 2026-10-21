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
package org.spongepowered.api.entity.display;

import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.util.Color;
import org.spongepowered.api.util.Ticks;
import org.spongepowered.api.util.Transform;
import org.spongepowered.math.matrix.Matrix4d;

import java.util.Optional;

/**
 * A display entity renders a block, an item or some text in the world,
 * without a hitbox, physics or ticking behavior of its own.
 *
 * <p>Besides its position and rotation in the world, a display has a
 * transformation of its own, applied around the entity's position, see
 * {@link #displayTransform()} and {@link #matrix()}.</p>
 *
 * <p>Changes to the transformation, and to the
 * {@link TextDisplay#backgroundColor() background} and
 * {@link TextDisplay#textOpacity() text opacity} of text displays, are
 * interpolated by the client over the {@link #interpolationDuration()},
 * starting after the {@link #interpolationDelay()}. Changes to the
 * position and rotation of the entity itself are interpolated over the
 * {@link #teleportDuration()} instead.</p>
 */
public interface DisplayEntity extends Entity {

    /**
     * Returns the transformation of the display, relative to the entity's
     * position.
     *
     * <p>This is not the {@link #transform() transform} of the entity in
     * the world. See {@link Keys#TRANSFORM} for how the transformation maps
     * to a {@link Transform}; {@link #matrix()} represents it exactly.</p>
     *
     * <p>Supports interpolation.</p>
     *
     * @return The transformation of the display
     */
    default Transform displayTransform() {
        return this.require(Keys.TRANSFORM);
    }

    /**
     * Returns the transformation of the display, relative to the entity's
     * position, as an affine matrix.
     *
     * <p>Supports interpolation.</p>
     *
     * @return The transformation matrix
     */
    default Matrix4d matrix() {
        return this.require(Keys.MATRIX);
    }

    /**
     * Returns how long the client interpolates changes over.
     *
     * @return The duration of the interpolation
     * @see Keys#INTERPOLATION_DURATION
     */
    default Ticks interpolationDuration() {
        return this.require(Keys.INTERPOLATION_DURATION);
    }

    /**
     * Returns how long the client waits before starting to interpolate
     * the latest change.
     *
     * @return The delay to the start of the interpolation
     * @see Keys#INTERPOLATION_DELAY
     */
    default Ticks interpolationDelay() {
        return this.require(Keys.INTERPOLATION_DELAY);
    }

    /**
     * Returns how long the client interpolates changes of the entity's
     * position and rotation over.
     *
     * @return The duration of the teleportation
     * @see Keys#TELEPORT_DURATION
     */
    default Ticks teleportDuration() {
        return this.require(Keys.TELEPORT_DURATION);
    }

    /**
     * Returns how the display rotates to face the viewer.
     *
     * @return The billboard type
     */
    default BillboardType billboardType() {
        return this.require(Keys.BILLBOARD_TYPE);
    }

    /**
     * Returns the skylight override, if one is set.
     *
     * @return The skylight override
     * @see Keys#SKY_LIGHT
     */
    default Optional<Integer> skyLight() {
        return this.get(Keys.SKY_LIGHT);
    }

    /**
     * Returns the blocklight override, if one is set.
     *
     * @return The blocklight override
     * @see Keys#BLOCK_LIGHT
     */
    default Optional<Integer> blockLight() {
        return this.get(Keys.BLOCK_LIGHT);
    }

    /**
     * Returns the shadow radius.
     *
     * @return The shadow radius
     */
    default Double shadowRadius() {
        return this.require(Keys.SHADOW_RADIUS);
    }

    /**
     * Returns the shadow strength or darkness.
     * <p>Vanilla defaults to 1</p>
     *
     * @return The shadow strength
     */
    default Double shadowStrength() {
        return this.require(Keys.SHADOW_STRENGTH);
    }

    /**
     * Returns the range at which the display entity is rendered.
     *
     * @return The view range
     * @see Keys#VIEW_RANGE
     */
    default Double viewRange() {
        return this.require(Keys.VIEW_RANGE);
    }

    /**
     * Returns the width of the box the client culls the display by.
     *
     * @return The culling width
     * @see Keys#CULLING_WIDTH
     */
    default double cullingWidth() {
        return this.require(Keys.CULLING_WIDTH);
    }

    /**
     * Returns the height of the box the client culls the display by.
     *
     * @return The culling height
     * @see Keys#CULLING_HEIGHT
     */
    default double cullingHeight() {
        return this.require(Keys.CULLING_HEIGHT);
    }

    /**
     * Returns the color of the glowing outline, if it overrides the color
     * of the entity's team.
     *
     * @return The glow color override
     * @see Keys#GLOW_COLOR
     */
    default Optional<Color> glowColor() {
        return this.get(Keys.GLOW_COLOR);
    }

}
