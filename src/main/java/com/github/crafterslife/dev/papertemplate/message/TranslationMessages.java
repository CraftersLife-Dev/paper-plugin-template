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
package com.github.crafterslife.dev.papertemplate.message;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;

/**
 * このプラグインで使用するシステムメッセージの集合体。
 */
// Note: プレフィックスなどを使用してキーを記述すると、リソースバンドルとのリンクが切れてしまうので注意が必要
public final class TranslationMessages {

    private TranslationMessages() {

    }

    /**
     * 設定の再読み込み成功メッセージを送信する。
     *
     * @param audience 受信するオーディエンス
     */
    public static void configReloadSuccess(final Audience audience) {
        final Component rendered = TranslationRenderer.render("papertemplate.reload.success", audience); // TODO: キーは書き換えてね
        audience.sendMessage(rendered);
    }
}
