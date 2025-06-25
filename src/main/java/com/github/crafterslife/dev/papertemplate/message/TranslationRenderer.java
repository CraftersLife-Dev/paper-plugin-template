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

import com.github.crafterslife.dev.papertemplate.integration.MiniPlaceholdersExpansion;
import io.github.miniplaceholders.api.MiniPlaceholders;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.translation.Argument;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
final class TranslationRenderer {

    private static final TagResolver TAG_RESOLVER;

    static {
        final TagResolver.Builder tagBuilder = TagResolver
                .builder()
                .resolver(TagResolver.standard());

        // JIS安全色: https://ja.wikipedia.org/wiki/JIS%E5%AE%89%E5%85%A8%E8%89%B2
        final TextColor red = TextColor.color(Integer.parseInt("ff4b00", 16));
        final TextColor orange = TextColor.color(Integer.parseInt("f6aa00", 16));
        final TextColor yellow = TextColor.color(Integer.parseInt("f2e700", 16));
        final TextColor green = TextColor.color(Integer.parseInt("00b06b", 16));
        final TextColor blue = TextColor.color(Integer.parseInt("1971ff", 16));

        tagBuilder
                .tag("fatal", Tag.styling(red))
                .tag("error", Tag.styling(orange))
                .tag("warn", Tag.styling(yellow))
                .tag("info", Tag.styling(green))
                .tag("debug", Tag.styling(blue));

        TAG_RESOLVER = tagBuilder.build();
    }

    private TranslationRenderer() {

    }

    public static Component render(final String key, final TagResolver... tagResolvers) {
        return TranslationRenderer.render(key, null, tagResolvers);
    }

    public static Component render(final String key, final @Nullable Audience audience, final TagResolver... tagResolvers) {
        final ComponentLike argument = TranslationRenderer.placeholderResolver(audience, tagResolvers);
        return Component.translatable(key, argument);
    }

    private static ComponentLike placeholderResolver(final @Nullable Audience audience, final TagResolver... tagResolver) {
        final TagResolver.Builder tagBuilder = TagResolver.builder();

        tagBuilder
                .resolver(TAG_RESOLVER)
                .resolvers(tagResolver);

        if (MiniPlaceholdersExpansion.miniPlaceholdersLoaded()) {
            tagBuilder.resolver(MiniPlaceholders.getGlobalPlaceholders());

            if (audience != null) {
                tagBuilder.resolver(MiniPlaceholders.getAudiencePlaceholders(audience));
            }
        }

        return Argument.tagResolver(tagBuilder.build());
    }
}
