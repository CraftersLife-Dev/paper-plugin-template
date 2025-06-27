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
package com.github.crafterslife.dev.papertemplate.paper;

import com.github.crafterslife.dev.papertemplate.configuration.ConfigManager;
import com.github.crafterslife.dev.papertemplate.message.TranslationSource;
import com.github.crafterslife.dev.papertemplate.message.TranslationService;
import com.github.crafterslife.dev.papertemplate.message.TranslationServiceFactory;
import com.github.crafterslife.dev.papertemplate.paper.commands.AdminCommand;
import com.github.crafterslife.dev.papertemplate.paper.commands.InternalCommand;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

// サーバーが読み込まれる前の初期化を担う
@SuppressWarnings({"UnstableApiUsage", "unused"})
public final class TemplateBootstrap implements PluginBootstrap { // TODO: クラス名は書き換えてね

    private @Nullable TemplateBootstrapContext templateBootstrapContext;

    // サーバーによって呼び出されるプラグインの初期化処理を実行
    // 設定や翻訳、あるいはデータベースなどを初期化しておくためのもの
    @Override
    public void bootstrap(final BootstrapContext bootstrapContext) {

        // このプラグインのリソースをまとめたコンテナを初期化
        this.initializeResources(bootstrapContext);

        // コマンドを登録
        final var lifecycleManager = bootstrapContext.getLifecycleManager();
        this.registerCommands(lifecycleManager);
    }

    // JavaPluginのインスタンスを生成
    @Override
    public JavaPlugin createPlugin(final PluginProviderContext providerContext) {
        Objects.requireNonNull(this.templateBootstrapContext);
        return new TemplatePlugin(this.templateBootstrapContext);
    }

    /**
     * プラグインコンテキストを初期化する。
     */
    private void initializeResources(final BootstrapContext bootstrapContext) {
        if (this.templateBootstrapContext == null) {

            // 設定と翻訳のインスタンスを生成
            final var pluginContext = new TemplateBootstrapContext(
                    bootstrapContext,
                    new ConfigManager(bootstrapContext),
                    new TranslationSource(bootstrapContext),
                    TranslationServiceFactory.create(TranslationService.class)
            );

            // 設定と翻訳を初期化
            pluginContext.configManager().reloadConfigurations();
            pluginContext.translationSource().reloadTranslations();

            // フィールドに代入
            this.templateBootstrapContext = pluginContext;
        } else {
            // このメソッドが2回以上呼ばれるようなことがあれば例外
            throw new IllegalStateException("リソースはすでに初期化済み");
        }
    }

    /**
     * コマンドを登録する。
     *
     * @param lifecycleManager ライブサイクルイベントマネージャー
     */
    private void registerCommands(final LifecycleEventManager<BootstrapContext> lifecycleManager) {
        Objects.requireNonNull(this.templateBootstrapContext);

        // Note: 新しいコマンドクラスを定義したらここに追加
        final Set<Function<TemplateBootstrapContext, InternalCommand>> commandFactories = Set.of(
                AdminCommand::new
        );

        lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            for (final var factory : commandFactories) {
                final InternalCommand command = factory.apply(this.templateBootstrapContext); // コマンドのインスタンスを生成
                event.registrar().register(command.create(), command.description(), command.aliases()); // コマンドを登録
            }
        });
    }
}
