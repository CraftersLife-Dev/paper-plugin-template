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
package com.github.crafterslife.dev.papertemplate.paper;

public final class TemplatePermissions {

    private static final String PREFIX = "template."; // TODO: 小文字のプラグイン名+.に書き換えてね

    private TemplatePermissions() {

    }

    public static final String COMMAND_ADMIN = PREFIX + "command.admin";
    public static final String COMMAND_ADMIN_RELOAD = COMMAND_ADMIN + ".reload";
}
