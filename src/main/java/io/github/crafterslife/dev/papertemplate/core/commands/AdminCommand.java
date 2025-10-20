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
package io.github.crafterslife.dev.papertemplate.core.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.github.crafterslife.dev.papertemplate.core.resource.Config;
import io.github.crafterslife.dev.papertemplate.core.resource.Messages;
import io.github.crafterslife.dev.papertemplate.core.resource.Permissions;
import io.github.crafterslife.dev.papertemplate.infrastructure.configuration.ConfigurationHolder;
import io.github.crafterslife.dev.papertemplate.infrastructure.translation.TranslationHolder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.jspecify.annotations.NullMarked;

/**
 * <p>プラグインの設定を再読み込みするなどの管理者向けのタスクを実行するための管理者用コマンド。</p>
 */
@NullMarked
public final class AdminCommand implements BaseCommand {

    private final ConfigurationHolder<Config> configHolder;
    private final TranslationHolder<Messages> translationHolder;

    /**
     * このクラスの新しいインスタンスを生成する。
     *
     * @param configHolder      設定ホルダー
     * @param translationHolder 翻訳ホルダー
     */
    public AdminCommand(
            final ConfigurationHolder<Config> configHolder,
            final TranslationHolder<Messages> translationHolder
    ) {
        this.configHolder = configHolder;
        this.translationHolder = translationHolder;
    }

    /**
     * 管理者用のコマンドを返します。
     *
     * @return 管理者用コマンド
     */
    @Override
    public LiteralCommandNode<CommandSourceStack> node() {
        return Commands.literal("template")
                .then(this.reloadNode())
                .build();
    }

    private LiteralCommandNode<CommandSourceStack> reloadNode() {
        return Commands.literal("reload")
                .requires(source -> source.getSender().hasPermission(Permissions.COMMAND_RELOAD))
                .executes(context -> {

                    this.configHolder.reloadConfig(); // 設定を再読み込み
                    this.translationHolder.reloadTranslator(); // 翻訳を再読み込み

                    // 再読み込み成功メッセージを取得
                    final Messages messages = this.translationHolder.getMessageService();
                    final Component reloadedMessage = messages.configReloadSuccess();

                    // 再読み込み成功メッセージを送信
                    final CommandSender sender = context.getSource().getSender();
                    sender.sendMessage(reloadedMessage);

                    return Command.SINGLE_SUCCESS;
                })
                .build();
    }
}
