/*
 * PaperTemplate
 *
 * Copyright (c) 2025. Namiu/うにたろう
 *                     Contributors []
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.github.crafterslife.dev.papertemplate.configuration;

import net.kyori.adventure.serializer.configurate4.ConfigurateComponentSerializer;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.loader.HeaderMode;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.nio.file.Path;

@NullMarked
public final class ConfigManager {

    private static final String PRIMARY_CONFIG_FILE_NAME = "config.yml";

    private final ComponentLogger logger;
    private final Path dataDirectory;

    private @MonotonicNonNull PrimaryConfig primaryConfig;

    public ConfigManager(
            final ComponentLogger logger,
            final Path dataDirectory
    ) {
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    public void reloadConfigurations() {
        this.logger.info("設定を読み込み中...");
        try {
            this.primaryConfig = this.load(PrimaryConfig.class, PRIMARY_CONFIG_FILE_NAME);
        } catch (final ConfigurateException exception) {
            throw new UncheckedConfigurateException("設定の読み込みに失敗", exception);
        }
        this.logger.info("設定の読み込みに成功: {}", PRIMARY_CONFIG_FILE_NAME);
    }

    public PrimaryConfig primaryConfig() {
        return this.primaryConfig;
    }

    public ConfigurationLoader<CommentedConfigurationNode> configurationLoader(final Path file) {
        return YamlConfigurationLoader.builder()
                .nodeStyle(NodeStyle.BLOCK)
                .defaultOptions(options -> {
                    final var kyoriSerializer = ConfigurateComponentSerializer.configurate();
                    return options
                            .shouldCopyDefaults(true)
                            .serializers(serializerBuilder -> serializerBuilder
                                    .registerAll(kyoriSerializer.serializers()));
                })
                .headerMode(HeaderMode.PRESET)
                .path(file)
                .build();
    }

    public <T> T load(final Class<T> clazz, final String fileName) throws ConfigurateException {
        final Path file = this.dataDirectory.resolve(fileName);
        final ConfigurationLoader<CommentedConfigurationNode> loader = this.configurationLoader(file);

        final CommentedConfigurationNode node = loader.load();
        final T config = node.get(clazz);
        if (config == null) {
            throw new ConfigurateException(node, "Failed to deserialize " + clazz.getName() + " from node");
        }

        node.set(clazz, config);
        loader.save(node);

        return config;
    }
}
