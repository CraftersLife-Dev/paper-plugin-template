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
package io.github.crafterslife.dev.papertemplate.infrastructure.configuration;

import io.github.crafterslife.dev.papertemplate.infrastructure.configuration.serializers.MaterialSerializer;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import java.nio.file.Path;
import net.kyori.adventure.serializer.configurate4.ConfigurateComponentSerializer;
import org.bukkit.Material;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

/**
 * YAML設定ファイルのローダーを構築するためのクラス。
 * <p>
 * このクラスは、設定ファイルのパス解決、TypeSerializerの構成、YAML設定ローダーの生成を担います。
 *
 * @param <C> 設定クラスの型
 */
@NullMarked
@SuppressWarnings("UnstableApiUsage")
final class ConfigurationLoader<C> {

    private final Class<C> configClass;
    private final YamlConfigurationLoader yamlLoader;

    private ConfigurationLoader(final Class<C> configClass, final YamlConfigurationLoader yamlLoader) {
        this.configClass = configClass;
        this.yamlLoader = yamlLoader;
    }

    /**
     * 指定されたコンテキストから {@code ConfigurationLoader} を生成する。
     *
     * @param <C>         設定クラスの型
     * @param configClass 設定クラス
     * @param context     ブートストラップコンテキスト
     * @return 生成された{@code ConfigurationLoader}
     */
    static <C> ConfigurationLoader<C> from(final Class<C> configClass, final BootstrapContext context) {
        final ConfigurationMetadata metadata = ConfigurationMetadata.create(configClass); // 設定ファイル名やヘッダー情報の取得
        final Path configPath = context.getDataDirectory().resolve(metadata.fileName()); // 設定ファイルのパスを解決
        final YamlConfigurationLoader yamlLoader = createYamlLoader(configPath, metadata.headerText()); // 設定ローダーの生成

        return new ConfigurationLoader<>(configClass, yamlLoader);
    }

    private static YamlConfigurationLoader createYamlLoader(
            final Path configPath,
            final String headerText
    ) {

        // Adventureのシリアライザーコレクション
        final TypeSerializerCollection adventureSerializers = ConfigurateComponentSerializer.configurate().serializers();

        // 設定オプションの生成
        final ConfigurationOptions options = ConfigurationOptions.defaults()
                .shouldCopyDefaults(true) // デフォルト値をコピー
                .header(headerText) // ヘッダー
                .serializers(builder -> builder
                        .registerAll(adventureSerializers)
                        .register(Material.class, new MaterialSerializer())); // Materialシリアライザー

        // 設定ローダーを生成して返す
        return YamlConfigurationLoader.builder()
                .nodeStyle(NodeStyle.BLOCK) // ブロックスタイル
                .defaultOptions(options)
                .path(configPath) // 設定ファイルのパス
                .build();
    }

    /**
     * 設定を読み込む。
     * <p>
     * 設定ファイルが存在しない場合、デフォルト値を使用して新しい設定ファイルを生成します。
     *
     * @return 読み込まれた設定
     * @throws UncheckedConfigurateException 読み込みに失敗した場合
     */
    C load() throws UncheckedConfigurateException {

        try {
            final ConfigurationNode node = this.yamlLoader.load(); // 設定ノードの読み込み
            final C config = node.get(this.configClass); // 設定ファイルを設定クラスにマッピング
            if (config == null) {
                throw new ConfigurateException("Failed to deserialize " + this.configClass.getSimpleName());
            }

            node.set(this.configClass, config);
            this.yamlLoader.save(node); // 設定ファイルに保存

            return config;

        } catch (final ConfigurateException exception) {
            throw new UncheckedConfigurateException(exception);
        }
    }
}
