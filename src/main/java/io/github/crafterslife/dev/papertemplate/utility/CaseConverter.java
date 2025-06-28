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
package io.github.crafterslife.dev.papertemplate.utility;

import java.util.regex.Pattern;

/**
 * 文字列の命名規則間変換するためのユーティリティ
 */
public final class CaseConverter {

    // 例: "playerName" -> "player_name"
    private static final Pattern CAMEL_TO_SNAKE_PATTERN = Pattern.compile("([a-z])([A-Z]+)");

    private CaseConverter() {

    }

    /**
     * キャメルケースの文字列をスネークケースに変換する。
     *
     * @param camelCaseString キャメルケースの文字列
     * @return スネークケースに変換された文字列
     */
    public static String toSnakeCase(final String camelCaseString) {
        return CAMEL_TO_SNAKE_PATTERN
                .matcher(camelCaseString)
                .replaceAll("$1_$2")
                .toLowerCase();
    }
}
