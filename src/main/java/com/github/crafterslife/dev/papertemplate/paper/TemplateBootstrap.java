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
import com.github.crafterslife.dev.papertemplate.message.TranslationManager;
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

// サーバーが読み込まれる前の初期化を実行
@SuppressWarnings({"UnstableApiUsage", "unused"})
public final class TemplateBootstrap implements PluginBootstrap { // TODO: 書き換えてね

    private @Nullable TemplateContext templateContext;

    // サーバーによって呼び出されるプラグインの起動処理を実行
    // 設定やメッセージ、あるいはデータベースなどのリソースを初期化
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
        Objects.requireNonNull(this.templateContext);
        return new TemplatePlugin(this.templateContext);
    }

    /**
     * プラグインコンテキストを初期化する。
     */
    private void initializeResources(final BootstrapContext bootstrapContext) {
        if (this.templateContext == null) {
            // 設定と翻訳メッセージのインスタンスを生成
            final var pluginContext = new TemplateContext(
                    bootstrapContext,
                    new ConfigManager(bootstrapContext),
                    new TranslationManager(bootstrapContext)
            );

            // 設定と翻訳メッセージを初期化
            pluginContext.configManager().reloadConfigurations();
            pluginContext.translationManager().reloadTranslations();

            // フィールドに代入
            this.templateContext = pluginContext;
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
        Objects.requireNonNull(this.templateContext);

        // Note: 新しいコマンドクラスを書いたら必ず追加
        final Set<Function<TemplateContext, InternalCommand>> commandFactories = Set.of(
                AdminCommand::new
        );

        lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            for (final var factory : commandFactories) {
                final InternalCommand command = factory.apply(this.templateContext); // コマンドのインスタンスを生成
                event.registrar().register(command.create(), command.description(), command.aliases()); // コマンドを登録
            }
        });
    }
}
