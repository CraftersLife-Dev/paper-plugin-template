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
package com.github.crafterslife.dev.papertemplate.paper.commands;

import com.github.crafterslife.dev.papertemplate.paper.TemplateContext;
import com.github.crafterslife.dev.papertemplate.paper.TemplatePermissions;
import com.github.crafterslife.dev.papertemplate.configuration.ConfigManager;
import com.github.crafterslife.dev.papertemplate.message.TranslationMessages;
import com.github.crafterslife.dev.papertemplate.message.TranslationManager;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

@SuppressWarnings("UnstableApiUsage")
public final class AdminCommand implements InternalCommand {

    private final String pluginName;
    private final ConfigManager configManager;
    private final TranslationManager translationManager;

    public AdminCommand(final TemplateContext context) {
        this.pluginName = context.pluginContext().getConfiguration().getName();
        this.configManager = context.configManager();
        this.translationManager = context.translationManager();
    }

    // Admin用のコマンドはここに集約
    @Override
    public LiteralCommandNode<CommandSourceStack> create() {

        // ルートコマンド
        final var root = Commands.literal(this.pluginName.toLowerCase())
                .requires(context -> context.getSender().hasPermission(TemplatePermissions.COMMAND_ADMIN));

        // リロードコマンド
        final var reload = Commands.literal("reload")
                .requires(context -> context.getSender().hasPermission(TemplatePermissions.COMMAND_ADMIN_RELOAD))
                .executes(context -> {
                    this.configManager.reloadConfigurations();
                    this.translationManager.reloadTranslations();
                    TranslationMessages.configReloadSuccess(context.getSource().getSender());
                    return Command.SINGLE_SUCCESS;
                });

        // コマンド合体 DXアドミンコマンドオー
        return root
                .then(reload)
                .build();
    }

    @Override
    public String description() {
        return "A %s provided admin command".formatted(this.pluginName);
    }
}
