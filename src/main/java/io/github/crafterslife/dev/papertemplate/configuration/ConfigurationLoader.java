/*
 * PaperTemplate
 *
 * Copyright (c) 2025. Namiu (うにたろう)
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
package io.github.crafterslife.dev.papertemplate.configuration;

import io.github.crafterslife.dev.papertemplate.configuration.annotations.ConfigHeader;
import io.github.crafterslife.dev.papertemplate.configuration.annotations.ConfigName;
import java.nio.file.Path;
import net.kyori.adventure.serializer.configurate4.ConfigurateComponentSerializer;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

/**
 * ディスク上のYAMLファイルに基づいて、型指定された設定レコードを読み込んで保存する。
 *
 * @param <C> 設定レコード
 */
@NullMarked
public final class ConfigurationLoader<C> {

    private final Class<C> configClass;
    private final C defaultConfig;

    private final org.spongepowered.configurate.loader.ConfigurationLoader<CommentedConfigurationNode> configLoader;

    /**
     * 指定された設定クラス用の新しいローダーを作成する。
     *
     * <p>`configClass` に付与された {@link ConfigName} アノテーションは、
     * `dataDirectory` に対する相対的なファイル名を決定するために使用される。
     * {@code ConfigHeader} アノテーションが存在する場合、ファイルの先頭にコメントが記述されます。
     * </p>
     *
     * @param configClass 設定レコードクラス;
     *                    {@link ConfigName} および {@link ConfigHeader} アノテーションを付与する必要があります。
     * @param defaultConfig ファイルにキーが存在しない場合に使用されるフォールバックインスタンス
     * @param dataDirectory ファイルが保存されるプラグインのデータディレクトリ
     */
    public ConfigurationLoader(
            final Class<C> configClass,
            final C defaultConfig,
            final Path dataDirectory
    ) {
        this.configClass = configClass;
        this.defaultConfig = defaultConfig;

        // Config path
        final String configName = configClass.getAnnotation(ConfigName.class).value();
        final Path configPath = dataDirectory.resolve(configName);

        // Config header
        final ConfigHeader headerAnnotation = configClass.getAnnotation(ConfigHeader.class);
        final String configHeader = headerAnnotation.value();

        final var kyoriSerializer = ConfigurateComponentSerializer.builder()
                .scalarSerializer(MiniMessage.miniMessage())
                .build()
                .serializers();

        this.configLoader = YamlConfigurationLoader.builder()
                .nodeStyle(NodeStyle.BLOCK)
                .defaultOptions(options -> options
                        .shouldCopyDefaults(true)
                        .header(configHeader)
                        .serializers(builder -> builder
                                .registerAll(kyoriSerializer)
                        )
                )
                .path(configPath)
                .build();
    }

    /**
     * ディスクから設定を読み込み、存在しないキーにはデフォルト値を設定し、その結果をファイルに書き戻す。
     *
     * @return the deserialized configuration instance
     * @throws UncheckedConfigurateException if the file cannot be read, parsed, or written
     */
    C loadConfiguration() throws UncheckedConfigurateException {
        try {
            final ConfigurationNode node = this.configLoader.load();
            final C config = node.get(this.configClass, this.defaultConfig);
            this.configLoader.save(node);
            return config;
        } catch (final ConfigurateException exception) {
            throw new UncheckedConfigurateException(exception);
        }
    }
}
