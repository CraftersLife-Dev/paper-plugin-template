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
package com.github.crafterslife.dev.papertemplate.configuration;

import java.io.Serial;
import org.spongepowered.configurate.ConfigurateException;

/**
 * 設定関連の例外を扱う。
 *
 * <p>このクラスは、{@link ConfigurateException} を {@link RuntimeException} でラップしただけのクラスである。
 * プレイヤーがreloadコマンドを実行して例外が発生した場合に、エラーメッセージをプレイヤーへ送信するためのもの。</p>
 * <p>Note: 例外処理を {@link ConfigManager} クラス内で完結してしまうとエラーメッセージをログにしか残せないため、
 * 例外発生をプレイヤーへ知らせる手段がなくなる。例外をプレイヤーへ知らせないと壊れた設定のままプラグインが動作することに繋がりかねない。</p>
 */
// Note: ただのラッパークラスだし誰も使わないと思うので詳細なJavadocは省略
@SuppressWarnings("unused")
final class UncheckedConfigurateException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -3277407553778580668L;

    public UncheckedConfigurateException() {
        super();
    }

    public UncheckedConfigurateException(final String message) {
        super(message);
    }

    public UncheckedConfigurateException(final ConfigurateException cause) {
        super(cause);
    }

    public UncheckedConfigurateException(final String message, final ConfigurateException cause) {
        super(message, cause);
    }

    public UncheckedConfigurateException(final String message, final ConfigurateException cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
