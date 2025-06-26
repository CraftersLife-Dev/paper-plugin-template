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

import com.github.crafterslife.dev.papertemplate.paper.TemplateContext;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import net.kyori.adventure.serializer.configurate4.ConfigurateComponentSerializer;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.loader.HeaderMode;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.nio.file.Path;
import java.util.Objects;

/**
 * 設定ファイルの読み込みや、設定オブジェクトの管理を担う。
 *
 * <p>このクラスはプラグインのデータディレクトリ内のYAML設定ファイルを扱い、オブジェクトへのシリアライズおよびデシリアライズをおこなう。</p>
 * <p>WARN: このクラスのインスタンスは安易に生成しない。複数の設定オブジェクトが存在する危険な状態になる。
 * このクラスのインスタンスが必要な場合は、 {@link TemplateContext#configManager()} から取得する。</P>
 */
// このクラスをシングルトンで設計しなかった理由は、テストを容易にするため
@SuppressWarnings("UnstableApiUsage")
public final class ConfigManager {

    private static final String PRIMARY_CONFIG_FILE_NAME = "config.yml";
    private static final String PRIMARY_CONFIG_HEADER = """
            PluginTemplateのメイン設定です。
            値を変更する前に、その項目が何をするものなのか必ず確認してください。
            項目によってはゲームプレイに多大な影響を与えます。
            """; // TODO: 書き換えてね

    private final ComponentLogger logger;
    private final Path dataDirectory;

    private @Nullable PrimaryConfig primaryConfig; // config.yml

    /**
     * 頼むからこれは呼び出さないでくれ。
     *
     * @param context ブートストラップコンテキスト
     */
    public ConfigManager(final BootstrapContext context) {
        this.logger = context.getLogger();
        this.dataDirectory = context.getDataDirectory();
    }

    /**
     * プライマリ設定 ({@code config.yml}) のオブジェクトを返す。
     *
     * @return {@link PrimaryConfig} のインスタンス
     * @throws NullPointerException 設定がまだ読み込まれていない場合
     */
    public PrimaryConfig primaryConfig() {
        return Objects.requireNonNull(this.primaryConfig, "プライマリ設定がまだ読み込まれていない");
    }

    /**
     * すべての設定ファイルを再読み込みする。
     *
     * <p>Note: 現在は {@code config.yml} のみが対象。</p>
     *
     * @throws UncheckedConfigurateException 設定の読み込みまたは保存中にConfigurateエラーが発生した場合
     */
    public void reloadConfigurations() {
        this.logger.info("設定を読み込み中...");
        try {
            this.primaryConfig = this.load(PrimaryConfig.class, PRIMARY_CONFIG_FILE_NAME, PRIMARY_CONFIG_HEADER);
        } catch (final ConfigurateException exception) {
            throw new UncheckedConfigurateException("設定の読み込みに失敗", exception);
        }
        this.logger.info("設定の読み込みに成功: {}", PRIMARY_CONFIG_FILE_NAME);
    }

    /**
     * 設定ファイルをオブジェクトへとデシリアライズして返す。
     *
     * <p>引数で指定した名称の設定ファイルをプラグインディレクトリから読み込み、引数で指定したクラスのオブジェクトへとデシリアライズして返す。
     * 設定ファイルが見つからなかった場合は指定したクラスのフィールドのデフォルト値でオブジェクトを生成し、それをシリアライズしてプラグインディレクトリに書き込む。
     *
     * @param <T> 設定オブジェクトの型
     * @param clazz デシリアライズする対象
     * @param fileName 設定ファイルの名称
     * @return デシリアライズされたオブジェクト
     * @throws ConfigurateException 設定の読み込みや保存、あるいはデシリアライズなどによってConfigurateエラーが発生した場合
     */
    private <T> T load(final Class<T> clazz, final String fileName, String header) throws ConfigurateException {
        // データディレクトリパスとfileNameを繋ぎ、新たなファイルパスを生成する。
        final Path filePath = this.dataDirectory.resolve(fileName);

        // ローダーを生成して、設定ファイルをオブジェクトへとデシリアライズする。
        // 設定ファイルがファイルパスに存在しなかった場合は、clazzのオブジェクトが直接生成される。
        final ConfigurationLoader<CommentedConfigurationNode> loader = this.configurationLoader(filePath, header);
        final CommentedConfigurationNode root = loader.load(); // ルートとなるノード
        final T config = root.get(clazz);
        if (config == null) {
            throw new ConfigurateException(root, "デシリアライズに失敗: %s".formatted(clazz.getName()));
        }

        // ローダーを使用して、設定オブジェクトを設定ファイルにシリアライズしてからfilePathへ書き込む。
        loader.save(root);

        return config;
    }

    /**
     * ファイルパスを元に {@link YamlConfigurationLoader} を作成して返す。
     *
     * <p>このローダーは、ブロックノードスタイルでYamlを処理する。
     * ローダーが担うシリアライズやデシリアライズは、プリミティブ型に加えていくつかの参照型を標準でサポートしている。</p>
     * <p>また、Adventureのシリアライザーもサポートしているため、 {@link Component} や {@link Sound} などのデータ型もシリアライズ可能。</p>
     *
     * @param filePath ローダーが設定を処理するファイルへのパス
     * @return 指定されたファイル用の {@link ConfigurationLoader} インスタンス
     * @see <a href="https://github.com/SpongePowered/Configurate/wiki/Type-Serializers">Type Serializers</a>
     * @see <a href="https://github.com/KyoriPowered/adventure/tree/main/4/serializer-configurate4/src/main/java/net/kyori/adventure/serializer/configurate4">Adventure Serializers</a>
     */
    // 独自のシリアライザーを登録したい場合は以下を参照
    // https://github.com/SpongePowered/Configurate/wiki/Type-Serializers
    private ConfigurationLoader<CommentedConfigurationNode> configurationLoader(final Path filePath, final String header) {

        // Adventureシリアライザー
        final var adventureSerializer = ConfigurateComponentSerializer.builder()
                .scalarSerializer(MiniMessage.miniMessage()) // 設定クラスの型にComponentを使用する場合はMiniMessage形式で設定ファイルへ出力
                .outputStringComponents(true)
                .build()
                .serializers();

        // ローダーをビルドして返す
        return YamlConfigurationLoader.builder()
                .nodeStyle(NodeStyle.BLOCK)
                .headerMode(HeaderMode.PRESET)
                .defaultOptions(options -> options
                        .shouldCopyDefaults(true)
                        .header(header)
                        .serializers(builder -> builder.registerAll(adventureSerializer))) // Adventureのシリアライザーを登録
                .path(filePath)
                .build();
    }
}
