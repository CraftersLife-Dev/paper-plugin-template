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
package com.github.crafterslife.dev.papertemplate;

import com.github.crafterslife.dev.papertemplate.commands.AdminCommand;
import com.github.crafterslife.dev.papertemplate.commands.InternalCommand;
import com.github.crafterslife.dev.papertemplate.configuration.ConfigManager;
import com.github.crafterslife.dev.papertemplate.message.TranslationRegistry;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jspecify.annotations.NullMarked;

import java.util.Set;
import java.util.function.Function;

@NullMarked
@SuppressWarnings({"UnstableApiUsage", "unused"})
public final class TemplateBootstrap implements PluginBootstrap {

    private @MonotonicNonNull TemplateContext pluginContext;

    @Override
    public void bootstrap(final BootstrapContext context) {
        this.pluginContext = new TemplateContext(
                context.getLogger(),
                new ConfigManager(context.getLogger(), context.getDataDirectory()),
                new TranslationRegistry(context.getLogger(), context.getPluginSource(), context.getDataDirectory())
        );

        // リソースを読み込む
        this.loadResources(context);

        // コマンドを登録する
        this.registerCommands(context);
    }

    @Override
    public JavaPlugin createPlugin(final PluginProviderContext context) {
        return new TemplatePlugin(this.pluginContext);
    }

    public void loadResources(final BootstrapContext context) {
        this.pluginContext.configManager().reloadConfigurations();
        this.pluginContext.translationRegistry().reloadTranslations();
    }

    public void registerCommands(final BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Set<Function<TemplateContext, InternalCommand>> commandFactories = Set.of(
                    AdminCommand::new
            );

            commandFactories.stream()
                    .map(factory -> factory.apply(this.pluginContext))
                    .forEach(command -> event.registrar().register(command.create(), command.description(), command.aliases()));
        });
    }
}
