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
package org.spongepowered.api;

import org.spongepowered.api.data.type.Career;
import org.spongepowered.api.data.value.ValueFactory;
import org.spongepowered.api.entity.ai.task.AITaskType;
import org.spongepowered.api.entity.ai.task.AbstractAITask;
import org.spongepowered.api.entity.living.Agent;
import org.spongepowered.api.item.merchant.TradeOfferGenerator;
import org.spongepowered.api.item.merchant.VillagerRegistry;
import org.spongepowered.api.item.recipe.crafting.CraftingRecipeRegistry;
import org.spongepowered.api.item.recipe.smelting.SmeltingRecipeRegistry;
import org.spongepowered.api.network.status.Favicon;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.plugin.PluginManager;
import org.spongepowered.api.registry.CatalogRegistryModule;
import org.spongepowered.api.registry.RegistryModule;
import org.spongepowered.api.registry.RegistryModuleAlreadyRegisteredException;
import org.spongepowered.api.resourcepack.ResourcePack;
import org.spongepowered.api.scoreboard.displayslot.DisplaySlot;
import org.spongepowered.api.text.TextFactory;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.selector.SelectorFactory;
import org.spongepowered.api.text.translation.Translation;
import org.spongepowered.api.util.ResettableBuilder;
import org.spongepowered.api.util.rotation.Rotation;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Provides an easy way to retrieve types from a {@link Game}.
 *
 * <p>Note that the registries may be in flux, especially during game
 * initialization. These will be accurate for the time they are called, however
 * they may change at a later point. Do not assume that the contents of a
 * collection will be all the entries that will exist.</p>
 *
 * <p>Some of the returned instances my become incorrect if they are later
 * overwritten. However, this should occur prior to
 * {@link GameState#POST_INITIALIZATION}.</p>
 */
public interface GameRegistry {

    /**
     * Resolves a catalog key. This should only be used when transforming
     * legacy {@link String} id's that were properly formatted to the
     * {@link CatalogType}s previous id requirements. This does not guarantee
     * that the returned {@link CatalogKey} is valid for the game's current
     * state, as the key may well point to an invalid {@link CatalogType}
     * that is no longer available.
     *
     * <p>If chaining this method with {@link #getType(Class, CatalogKey)},
     * be aware that the value may still be {@link Optional#empty()} as the
     * value is only "translated" into a {@link CatalogKey}. No guarantees
     * are made about the validity of the key's format.</p>
     *
     * @param value The value
     * @return A new catalog key
     */
    CatalogKey resolveKey(final String value);

    /**
     * Attempts to retrieve the specific type of {@link CatalogType} based on
     * the key given.
     *
     * <p>Some types may not be available for various reasons including but not
     * restricted to: mods adding custom types, plugins providing custom types,
     * game version changes.</p>
     *
     * @param typeClass The class of the type of {@link CatalogType}
     * @param key The catalog key
     * @param <T> The type of dummy type
     * @return The found dummy type, if available
     * @see CatalogType
     */
    <T extends CatalogType> Optional<T> getType(Class<T> typeClass, CatalogKey key);

    /**
     * Gets a collection of all available found specific types of
     * {@link CatalogType} requested.
     *
     * <p>The presented {@link CatalogType}s may not exist in default catalogs
     * due to various reasons including but not restricted to: mods, plugins,
     * game changes.</p>
     *
     * @param typeClass The class of {@link CatalogType}
     * @param <T> The type of {@link CatalogType}
     * @return A collection of all known types of the requested catalog type
     */
    <T extends CatalogType> Collection<T> getAllOf(Class<T> typeClass);

    /**
     * Gets a collection of all available found specific types of
     * {@link CatalogType} requested.
     *
     * @param pluginId The plugin id to check for types
     * @param typeClass The class of {@link CatalogType}
     * @param <T> The type of {@link CatalogType}
     * @return A collection of all known types of the requested catalog type
     */
    <T extends CatalogType> Collection<T> getAllFor(String pluginId, Class<T> typeClass);

    /**
     * Gets all {@link CatalogType} for Minecraft as a base mod. Note that
     * some {@link CatalogType}s are not originally from the game itself, and
     * may be provided by Sponge.
     *
     * @param typeClass The type of catalog type
     * @param <T> The type of catalog type
     * @return The collection of all known types of the requested catalog type
     */
    default <T extends CatalogType> Collection<T> getAllForMinecraft(Class<T> typeClass) {
        return getAllFor(PluginManager.MINECRAFT_PLUGIN_ID, typeClass);
    }

    /**
     * Gets all {@link CatalogType} for Sponge as a base mod. Note that
     * some {@link CatalogType}s are not originally from the game itself, and
     * may be provided by Sponge.
     *
     * @param typeClass The type of catalog type
     * @param <T> The type of catalog type
     * @return The collection of all known types of the requested catalog type
     */
    default <T extends CatalogType> Collection<T> getAllForSponge(Class<T> typeClass) {
        return getAllFor(PluginManager.SPONGE_PLUGIN_ID, typeClass);
    }

    /**
     * Registers the {@link CatalogRegistryModule} for dummy registration and
     * handling.
     *
     * <p>By default, the only supported modules that can be registered are
     * dependent that plugins are not attempting to register new modules for
     * API-provided {@link CatalogType}s.</p>
     *
     * @param catalogClass The dummy class itself
     * @param registryModule The registry module
     * @param <T> The type of dummy
     * @return fluent interface
     * @throws IllegalArgumentException If there is a module already registered
     * @throws RegistryModuleAlreadyRegisteredException If the registry module
     *      is already registered
     * @throws UnsupportedOperationException If an attempt is made to register
     *      a module for an API catalog
     */
    <T extends CatalogType> GameRegistry registerModule(Class<T> catalogClass, CatalogRegistryModule<T> registryModule)
            throws IllegalArgumentException, RegistryModuleAlreadyRegisteredException;

    /**
     * Registers the desired {@link RegistryModule}.
     *
     * @param module The module to register
     * @return This registry, for chaining
     * @throws RegistryModuleAlreadyRegisteredException if the specified
     *      registry module is already registered
     */
    GameRegistry registerModule(RegistryModule module) throws RegistryModuleAlreadyRegisteredException;

    /**
     * Registers a {@link Supplier} for creating the desired {@link ResettableBuilder}.
     *
     * @param builderClass The builder class
     * @param supplier The supplier
     * @param <T> The type of builder/supplier
     * @return This registry, for chaining
     */
    <T> GameRegistry registerBuilderSupplier(Class<T> builderClass, Supplier<? extends T> supplier);

    /**
     * Gets a builder of the desired class type, examples may include:
     * {@link org.spongepowered.api.item.inventory.ItemStack.Builder}, etc.
     *
     * @param builderClass The class of the builder
     * @param <T> The type of builder
     * @return The builder, if available
     * @throws IllegalArgumentException If there is no supplier for the given
     *      builder class
     */
    <T extends ResettableBuilder<?, ? super T>> T createBuilder(Class<T> builderClass) throws IllegalArgumentException;

    /**
     * Gets a {@link Collection} of the default GameRules.
     *
     * @return The default GameRules.
     */
    Collection<String> getDefaultGameRules();

    /**
     * Gets the {@link Rotation} with the provided degrees.
     *
     * @param degrees The degrees of the rotation
     * @return The {@link Rotation} with the given degrees or
     *      <tt>Optional.empty()</tt> if not found
     */
    Optional<Rotation> getRotationFromDegree(int degrees);

    /**
     * Loads a {@link Favicon} from the specified encoded string. The format of
     * the input depends on the implementation.
     *
     * @param raw The encoded favicon
     * @return The loaded favicon
     * @throws IOException If the favicon couldn't be loaded
     */
    Favicon loadFavicon(String raw) throws IOException;

    /**
     * Loads a favicon from a specified {@link Path}.
     *
     * @param path The path to the favicon
     * @return The loaded favicon from the file
     * @throws IOException If the favicon couldn't be loaded
     * @throws FileNotFoundException If the file doesn't exist
     */
    Favicon loadFavicon(Path path) throws IOException;

    /**
     * Loads a favicon from a specified {@link URL}.
     *
     * @param url The favicon URL
     * @return The loaded favicon from the URL
     * @throws IOException If the favicon couldn't be loaded
     */
    Favicon loadFavicon(URL url) throws IOException;

    /**
     * Loads a favicon from a specified {@link InputStream}.
     *
     * @param in The favicon input stream
     * @return The loaded favicon from the input stream
     * @throws IOException If the favicon couldn't be loaded
     */
    Favicon loadFavicon(InputStream in) throws IOException;

    /**
     * Loads a favicon from a specified {@link BufferedImage}.
     *
     * @param image The favicon image
     * @return The loaded favicon from the image
     * @throws IOException If the favicon couldn't be loaded
     */
    Favicon loadFavicon(BufferedImage image) throws IOException;

    /**
     * Retrieves the crafting RecipeRegistry for this GameRegistry.
     *
     * @return The crafting recipe registry
     */
    CraftingRecipeRegistry getCraftingRecipeRegistry();

    /**
     * Retrieves the smelting RecipeRegistry for this GameRegistry.
     *
     * @return The smelting recipe registry
     */
    SmeltingRecipeRegistry getSmeltingRecipeRegistry();

    /**
     * Gets a {@link ResourcePack} that's already been created by its ID.
     *
     * @param id The ID of the pack
     * @return The ResourcePack with the specified ID, or Optional.empty() if
     *         none could be found
     */
    Optional<ResourcePack> getResourcePackById(String id);

    /**
     * Gets a {@link DisplaySlot} which displays only for teams with the
     * provided color.
     *
     * @param color The color for the display slot
     * @return The {@link DisplaySlot} with the provided color, or
     *      <tt>Optional.empty()</tt> if not found
     */
    Optional<DisplaySlot> getDisplaySlotForColor(TextColor color);

    /**
     * Registers a new {@link AbstractAITask} with an {@link Agent} as the
     * owner. The complete id will be in the format of
     * <code>{@link PluginContainer#getId()}:id</code>.
     *
     * @param plugin The plugin who owns it
     * @param id The id that represents the task type
     * @param name The name for the task
     * @param aiClass The class of the task
     * @return The type
     */
    AITaskType registerAITaskType(Object plugin, String id, String name, Class<? extends AbstractAITask<? extends Agent>> aiClass);

    /**
     * Gets the {@link ValueFactory} for creating values.
     *
     * @return The value factory
     */
    ValueFactory getValueFactory();

    /**
     * Gets the {@link VillagerRegistry} for the register mappings of
     * {@link Career}s to {@link TradeOfferGenerator}s based on a level.
     *
     * @return The villager registry instance
     */
    VillagerRegistry getVillagerRegistry();

    /**
     * Gets the internal {@link TextFactory}.
     *
     * @return The text factory
     * @deprecated Internal use only
     */
    @Deprecated
    TextFactory getTextFactory();

    /**
     * Gets the internal {@link SelectorFactory}.
     *
     * @return The selector factory
     * @deprecated Internal use only
     */
    @Deprecated
    SelectorFactory getSelectorFactory();

    /**
     * Gets a locale for the specified locale code, e.g. {@code en_US}.
     *
     * @param locale The locale to lookup (e.g. {@code en_US}.
     * @return The locale
     */
    Locale getLocale(String locale);

    /**
     * Gets the {@link Translation} with the provided ID.
     *
     * @param id The ID of the translation
     * @return The {@link Translation} with the given ID or Optional.empty() if
     *      not found
     */
    Optional<Translation> getTranslationById(String id);

}
