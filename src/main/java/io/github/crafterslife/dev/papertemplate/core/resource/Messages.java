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
package io.github.crafterslife.dev.papertemplate.core.resource;

import io.github.crafterslife.dev.papertemplate.infrastructure.translation.TranslationStoreName;
import io.github.namiuni.kotonoha.annotations.Key;
import io.github.namiuni.kotonoha.annotations.Locales;
import io.github.namiuni.kotonoha.annotations.Message;
import io.github.namiuni.kotonoha.translatable.message.extra.miniplaceholders.PlaceholderScope;
import io.github.namiuni.kotonoha.translatable.message.extra.miniplaceholders.WithPlaceholders;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;

/**
 * プラグインのメッセージをまとめるためのユーティリティーインターフェース
 */
@NullMarked
@ApiStatus.NonExtendable
@TranslationStoreName(namespace = "template", value = "messages") // TODO: templateを変更
public interface Messages {

    /**
     * 設定の再読み込みに成功したことを知らせるためのメッセージを返す。
     *
     * @return 再読み込み成功メッセージ
     */
    @WithPlaceholders(PlaceholderScope.GLOBAL)
    @Key("template.config.reload.success") // TODO: templateを変更
    @Message(locale = Locales.EN_US, content = "<info>Configuration reloaded successfully!")
    @Message(locale = Locales.JA_JP, content = "<info>設定の再読み込みに成功しました！")
    Component configReloadSuccess();
}
