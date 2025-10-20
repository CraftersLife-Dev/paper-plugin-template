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

import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jspecify.annotations.NullMarked;

/**
 * カスタムタグを含んだ {@code MiniMessage} インスタンスを保持するためのユーティリティークラス。
 */
@NullMarked
final class CustomMiniMessage {

    static final MiniMessage CUSTOM_MINI_MESSAGE;

    static {
        // JIS Z 9103 https://ja.wikipedia.org/wiki/JIS%E5%AE%89%E5%85%A8%E8%89%B2
        final TextColor red = TextColor.color(0xFF4B00);
        final TextColor yellow = TextColor.color(0xF2E700);
        final TextColor green = TextColor.color(0x00B06B);
        final TextColor blue = TextColor.color(0x1971FF);

        final TagResolver.Builder safetyColors = TagResolver.builder()
                .resolver(TagResolver.standard())
                .tag("error", Tag.styling(red))
                .tag("warn", Tag.styling(yellow))
                .tag("info", Tag.styling(green))
                .tag("debug", Tag.styling(blue));

        CUSTOM_MINI_MESSAGE = MiniMessage.builder()
                .tags(safetyColors.build())
                .build();
    }

    private CustomMiniMessage() {
    }
}
