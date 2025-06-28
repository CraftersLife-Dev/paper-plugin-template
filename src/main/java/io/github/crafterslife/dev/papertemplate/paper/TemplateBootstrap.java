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
package io.github.crafterslife.dev.papertemplate.paper;

import io.github.crafterslife.dev.papertemplate.configuration.ConfigManager;
import io.github.crafterslife.dev.papertemplate.message.TranslationService;
import io.github.crafterslife.dev.papertemplate.message.TranslationServiceFactory;
import io.github.crafterslife.dev.papertemplate.message.TranslationSource;
import io.github.crafterslife.dev.papertemplate.paper.commands.AdminCommand;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import java.util.Objects;
import java.util.Set;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.Nullable;

/**
 * サーバーが読み込まれる前のプラグインの初期化を担う。
 */
@SuppressWarnings({"UnstableApiUsage", "unused"})
public final class TemplateBootstrap implements PluginBootstrap { // TODO: クラス名は書き換えてね

    private @Nullable ConfigManager configManager;
    private @Nullable TranslationSource translationSource;
    private @Nullable TemplateContext templateContext;

    /**
     * サーバーによって呼び出されるプラグインの初期化処理を実行する。
     * 設定や翻訳、あるいはデータベースなどを初期化する。
     *
     * @param bootstrapContext サーバーが提供するコンテキスト
     */
    @Override
    public void bootstrap(final BootstrapContext bootstrapContext) {

        // このプラグインのリソースをまとめたコンテナを初期化
        this.initializeResources(bootstrapContext);

        // コマンドを登録
        this.registerCommands(bootstrapContext);
    }

    // JavaPluginのインスタンスを生成
    @Override
    public JavaPlugin createPlugin(final PluginProviderContext providerContext) {
        Objects.requireNonNull(this.templateContext);
        return new TemplatePlugin(this.templateContext);
    }

    /**
     * プラグインコンテキストを初期化する。
     *
     * @param bootstrapContext サーバーが提供するコンテキスト
     * @throws IllegalStateException リソースがすでに初期化されている場合
     */
    private void initializeResources(final BootstrapContext bootstrapContext) throws IllegalStateException {
        // このメソッドが2回以上呼ばれるようなことがあれば例外
        if (Objects.isNull(this.configManager) || Objects.isNull(this.translationSource) || Objects.isNull(this.templateContext)) {
            throw new IllegalStateException("リソースはすでに初期化済み");
        }

        // リソースのインスタンスを生成
        this.configManager = new ConfigManager(bootstrapContext);
        this.translationSource = new TranslationSource(bootstrapContext);

        // リソースを初期化
        this.configManager.reloadConfigurations();
        this.translationSource.reloadTranslations();

        // プラグインコンテキストを生成
        this.templateContext = new TemplateContext(this.configManager::primaryConfig, TranslationServiceFactory.create(TranslationService.class));
    }

    /**
     * コマンドを登録する。
     *
     * @param bootstrapContext ブートストラップコンテキスト
     */
    private void registerCommands(final BootstrapContext bootstrapContext) {
        Objects.requireNonNull(this.configManager);
        Objects.requireNonNull(this.translationSource);
        Objects.requireNonNull(this.templateContext);

        // Note: 新しいコマンドクラスを定義したらここに追加
        bootstrapContext.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final var commands = Set.of(
                    new AdminCommand(bootstrapContext, this.configManager, this.translationSource, this.templateContext)
            );
            commands.forEach(command -> event.registrar().register(command.create(), command.description(), command.aliases()));
        });
    }
}
