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
package io.github.crafterslife.dev.papertemplate.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.github.crafterslife.dev.papertemplate.configuration.Configuration;
import io.github.crafterslife.dev.papertemplate.configuration.ConfigurationHolder;
import io.github.crafterslife.dev.papertemplate.permission.Permissions;
import io.github.crafterslife.dev.papertemplate.translation.Messages;
import io.github.crafterslife.dev.papertemplate.translation.TranslatorHolder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
import org.jspecify.annotations.NullMarked;

/**
 * <p>プラグインの設定を再読み込みするなどの管理者向けのタスクを実行するための管理者用コマンド。</p>
 */
@NullMarked
public final class AdminCommand implements BaseCommand {

    private final ConfigurationHolder<Configuration> configHolder;
    private final TranslatorHolder translatorHolder;
    private final Messages messages;

    /**
     * このクラスの新しいインスタンスを生成する。
     *
     * @param configHolder     設定ホルダー
     * @param translatorHolder 翻訳ホルダー
     * @param messages         メッセージ
     */
    public AdminCommand(
            final ConfigurationHolder<Configuration> configHolder,
            final TranslatorHolder translatorHolder,
            final Messages messages
    ) {
        this.configHolder = configHolder;
        this.translatorHolder = translatorHolder;
        this.messages = messages;
    }

    /**
     * 管理者用のコマンドを返します。
     *
     * @return 管理者用コマンド
     */
    @Override
    public LiteralCommandNode<CommandSourceStack> node() {
        return Commands.literal("template") // TODO: コマンド名をプラグイン名に変更
                .then(this.reloadNode())
                .build();
    }

    private LiteralCommandNode<CommandSourceStack> reloadNode() {
        return Commands.literal("reload")
                .requires(source -> source.getSender().hasPermission(Permissions.COMMAND_RELOAD))
                .executes(context -> {

                    final CommandSender sender = context.getSource().getSender();

                    // 設定を再読み込み
                    this.configHolder.reload();
                    sender.sendMessage(this.messages.configurationReloadSuccess());

                    // 翻訳を再読み込み
                    this.translatorHolder.reload();
                    sender.sendMessage(this.messages.translationReloadSuccess());

                    return Command.SINGLE_SUCCESS;
                })
                .build();
    }
}
