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

import io.github.namiuni.kotonoha.translatable.message.KotonohaMessages;
import io.github.namiuni.kotonoha.translatable.message.configuration.FormatTypes;
import io.github.namiuni.kotonoha.translatable.message.configuration.InvocationConfiguration;
import io.github.namiuni.kotonoha.translatable.message.extra.miniplaceholders.MiniPlaceholdersArgumentPolicy;
import io.github.namiuni.kotonoha.translatable.message.policy.argument.TranslationArgumentAdaptationPolicy;
import io.github.namiuni.kotonoha.translatable.message.policy.argument.tag.TagNameResolver;
import io.github.namiuni.kotonoha.translatable.message.utility.TranslationArgumentAdapter;
import org.jspecify.annotations.NullMarked;

@NullMarked
@SuppressWarnings("UnstableApiUsage")
final class KotonohaMessagesFactory {

    private KotonohaMessagesFactory() {
    }

    public static <I> I from(final Class<I> messageInterface) {

        final InvocationConfiguration config = FormatTypes.MINI_MESSAGE.configure()
                .withArgumentPolicy(argumentPolicy());

        return KotonohaMessages.of(messageInterface, config);
    }

    private static TranslationArgumentAdaptationPolicy argumentPolicy() {
        // スタンダードな引数適合
        final TranslationArgumentAdapter argumentAdapter = TranslationArgumentAdapter.standard();

        // 引数名か、またはアノテーションからタグ名を解決
        final TagNameResolver nameResolver = TagNameResolver.annotationOrParameterNameResolver();

        return MiniPlaceholdersArgumentPolicy.of(argumentAdapter, nameResolver);
    }
}
