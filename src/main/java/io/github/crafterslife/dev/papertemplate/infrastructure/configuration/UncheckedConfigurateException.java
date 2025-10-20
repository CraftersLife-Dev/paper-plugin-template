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
package io.github.crafterslife.dev.papertemplate.infrastructure.configuration;

import java.io.Serial;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurateException;

/**
 * {@link ConfigurateException} をラップした非検査例外。
 */
@NullMarked
public final class UncheckedConfigurateException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -2214743499979182898L;

    /**
     * 指定された {@link ConfigurateException} 原因で {@code UncheckedConfigurateException} を生成する。
     *
     * @param cause 例外の原因となる {@link ConfigurateException}
     */
    UncheckedConfigurateException(final ConfigurateException cause) {
        super(cause);
    }

    /**
     * 指定されたメッセージと原因で、 {@code UncheckedConfigurateException} を生成する。
     *
     * @param message 例外メッセージ
     * @param cause   例外の原因となる {@link ConfigurateException}
     */
    UncheckedConfigurateException(final String message, final @Nullable Throwable cause) {
        super(message, cause);
    }
}
