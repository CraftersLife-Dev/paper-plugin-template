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
package io.github.crafterslife.dev.papertemplate.infrastructure.translation;

import java.io.Serial;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * 翻訳の読み込み中に例外が発生した場合にスローされる非検査例外。
 * <p>
 * この例外は、リソースバンドルの読み込み失敗、Translator生成の失敗、または翻訳システムの初期化に関連する問題が発生した際にスローされます。
 */
@NullMarked
public final class TranslationException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 3827461983746129847L;

    /**
     * 指定されたメッセージで {@code TranslationLoadException}を生成する。
     *
     * @param message 例外メッセージ
     */
    TranslationException(final String message) {
        super(message);
    }

    /**
     * 指定されたメッセージと原因で {@code TranslationLoadException} を生成する。
     *
     * @param message 例外メッセージ
     * @param cause   例外の原因
     */
    TranslationException(final String message, final @Nullable Throwable cause) {
        super(message, cause);
    }
}
