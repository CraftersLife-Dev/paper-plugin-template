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

import com.github.crafterslife.dev.papertemplate.configuration.ConfigManager;
import com.github.crafterslife.dev.papertemplate.message.TranslationService;
import com.github.crafterslife.dev.papertemplate.message.TranslationSource;
import com.github.crafterslife.dev.papertemplate.paper.TemplateContext;
import com.github.crafterslife.dev.papertemplate.paper.TemplatePermissions;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;

/**
 * 管理者向けの機能をコマンドとして定義するクラス。
 */
@SuppressWarnings("UnstableApiUsage")
public final class AdminCommand implements InternalCommand {

    private final BootstrapContext bootstrapContext;
    private final ConfigManager configManager;
    private final TranslationSource translationSource;
    private final TranslationService translationService;

    /**
     * {@code AdminCommand}の新しいインスタンスを生成する。
     *
     * @param bootstrapContext  ブートストラップコンテキスト
     * @param configManager     設定管理
     * @param translationSource 翻訳管理
     * @param templateContext   プラグインコンテキスト
     */
    public AdminCommand(
            final BootstrapContext bootstrapContext,
            final ConfigManager configManager,
            final TranslationSource translationSource,
            final TemplateContext templateContext
    ) {
        this.bootstrapContext = bootstrapContext;
        this.configManager = configManager;
        this.translationSource = translationSource;
        this.translationService = templateContext.translationService();
    }

    // Admin用のコマンドはここに集約
    @Override
    public LiteralCommandNode<CommandSourceStack> create() {

        // ルートコマンド
        final var root = Commands.literal(this.bootstrapContext.getPluginMeta().getName().toLowerCase())
                .requires(context -> context.getSender().hasPermission(TemplatePermissions.COMMAND_ADMIN));

        // リロードコマンド
        final var reload = Commands.literal("reload")
                .requires(context -> context.getSender().hasPermission(TemplatePermissions.COMMAND_ADMIN_RELOAD))
                .executes(context -> {
                    this.configManager.reloadConfigurations();
                    this.translationSource.reloadTranslations();
                    this.translationService.sendCommandConfigReloadSuccess(context.getSource().getSender());
                    return Command.SINGLE_SUCCESS;
                });

        // コマンド合体 DXアドミンコマンドオー
        return root
                .then(reload)
                .build();
    }

    @Override
    public String description() {
        return "A %s provided admin command".formatted(this.bootstrapContext.getPluginMeta().getName());
    }

}
