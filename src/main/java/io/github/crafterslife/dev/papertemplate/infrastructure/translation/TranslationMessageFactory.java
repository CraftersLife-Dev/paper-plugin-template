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

import io.github.namiuni.kotonoha.translatable.message.KotonohaMessage;
import io.github.namiuni.kotonoha.translatable.message.configuration.FormatTypes;
import io.github.namiuni.kotonoha.translatable.message.configuration.InvocationConfiguration;
import io.github.namiuni.kotonoha.translatable.message.extra.miniplaceholders.MiniPlaceholdersArgumentPolicy;
import io.github.namiuni.kotonoha.translatable.message.policy.argument.TranslationArgumentAdaptationPolicy;
import io.github.namiuni.kotonoha.translatable.message.policy.argument.tag.TagNameResolver;
import io.github.namiuni.kotonoha.translatable.message.utility.TranslationArgumentAdapter;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.Translator;
import org.jspecify.annotations.NullMarked;

/**
 * 翻訳ソースを初期化し、メッセージインターフェースのプロキシインスタンスを生成するためのクラス。
 */
@NullMarked
@SuppressWarnings("UnstableApiUsage")
public final class TranslationMessageFactory {

    private TranslationMessageFactory() {
    }

    /**
     * メッセージインターフェースのプロキシインスタンスを生成して返す。
     *
     * @param <I>              メッセージインターフェースの型
     * @param messageInterface メッセージインターフェース
     * @param context          プラグインの起動時に提供されるコンテキスト
     * @return メッセージインターフェースのプロキシインスタンス
     */
    public static <I> I from(final Class<I> messageInterface, final BootstrapContext context) {

        final Translator translator = TranslatorFactory.from(messageInterface);
        GlobalTranslator.translator().addSource(translator);
        context.getLogger().info("翻訳を読み込みました。");

        final InvocationConfiguration invocationConfiguration = FormatTypes.MINI_MESSAGE
                .withArgumentPolicy(argumentPolicy());

        return KotonohaMessage.createProxy(messageInterface, invocationConfiguration);
    }

    private static TranslationArgumentAdaptationPolicy argumentPolicy() {
        // スタンダードな引数適合
        final TranslationArgumentAdapter argumentAdapter = TranslationArgumentAdapter.standard();

        // 引数名か、またはアノテーションからタグ名を解決
        final TagNameResolver nameResolver = TagNameResolver.annotationOrParameterNameResolver();

        return MiniPlaceholdersArgumentPolicy.of(argumentAdapter, nameResolver);
    }
}
