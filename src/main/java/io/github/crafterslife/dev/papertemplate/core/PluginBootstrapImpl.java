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
package io.github.crafterslife.dev.papertemplate.core;

import io.github.crafterslife.dev.papertemplate.core.commands.AdminCommand;
import io.github.crafterslife.dev.papertemplate.core.commands.BaseCommand;
import io.github.crafterslife.dev.papertemplate.core.resource.Config;
import io.github.crafterslife.dev.papertemplate.core.resource.Messages;
import io.github.crafterslife.dev.papertemplate.infrastructure.configuration.ConfigurationHolder;
import io.github.crafterslife.dev.papertemplate.infrastructure.translation.TranslationMessageFactory;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;

/**
 * <p>Paperの{@link PluginBootstrap}を実装し、プラグインのブートストラップと初期化ロジックを管理します。</p>
 */
@NullMarked
@SuppressWarnings({"UnstableApiUsage", "unused"})
public final class PluginBootstrapImpl implements PluginBootstrap {

    private @MonotonicNonNull ConfigurationHolder<Config> configHolder;
    private @MonotonicNonNull Messages messages;

    @ApiStatus.Internal
    public PluginBootstrapImpl() {
    }

    @Override
    public void bootstrap(final BootstrapContext context) {

        // 設定ホルダーを生成
        this.configHolder = ConfigurationHolder.from(Config.class, context);

        // メッセージサービスを生成
        this.messages = TranslationMessageFactory.from(Messages.class, context);

        // コマンドを登録
        final LifecycleEventManager<BootstrapContext> lifecycleManager = context.getLifecycleManager();
        lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final BaseCommand adminCommand = new AdminCommand(this.configHolder, this.messages);
            event.registrar().register(adminCommand.node(), adminCommand.description(), adminCommand.aliases());
        });
    }

    @Override
    public JavaPlugin createPlugin(final PluginProviderContext context) {
        return new JavaPluginImpl(this.configHolder, this.messages);
    }
}
