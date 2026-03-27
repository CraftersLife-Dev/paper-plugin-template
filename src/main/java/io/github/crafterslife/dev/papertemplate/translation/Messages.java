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
package io.github.crafterslife.dev.papertemplate.translation;

import io.github.namiuni.kotonoha.annotations.Key;
import io.github.namiuni.kotonoha.annotations.Locales;
import io.github.namiuni.kotonoha.annotations.Message;
import io.github.namiuni.kotonoha.annotations.ResourceBundle;
import io.github.namiuni.kotonoha.translatable.message.extra.miniplaceholders.PlaceholderScope;
import io.github.namiuni.kotonoha.translatable.message.extra.miniplaceholders.WithPlaceholders;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.NullMarked;

/**
 * プラグイン用の一元化されたメッセージ定義。
 */
@NullMarked
@ResourceBundle(baseName = "messages")
public interface Messages {

    /**
     * 設定の再読み込みが正常に完了した際、コマンド送信者に送信されるメッセージを返す。
     *
     * @return 成功を示す翻訳された {@link Component}
     */
    @Key("template.command.reload.config.success") // TODO: templateをプラグイン名に変更
    @Message(locale = Locales.ROOT, content = "<info>Configuration reloaded successfully.")
    @Message(locale = Locales.JA_JP, content = "<info>設定の再読み込みに成功しました。")
    @WithPlaceholders(PlaceholderScope.AUDIENCE_GLOBAL)
    Component configurationReloadSuccess();

    /**
     * 翻訳の再読み込みが正常に完了した際、コマンド送信者に送信されるメッセージを返す。
     *
     * @return 成功を示す翻訳された {@link Component}
     */
    @Key("template.command.reload.translation.success") // TODO: templateをプラグイン名に変更
    @Message(locale = Locales.ROOT, content = "<info>Configuration reloaded successfully.")
    @Message(locale = Locales.JA_JP, content = "<info>翻訳の再読み込みに成功しました。")
    @WithPlaceholders(PlaceholderScope.AUDIENCE_GLOBAL)
    Component translationReloadSuccess();
}
