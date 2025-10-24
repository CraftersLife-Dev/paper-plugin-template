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

import io.github.namiuni.kotonoha.translator.KotonohaTranslationStore;
import java.util.Locale;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.translation.Translator;
import org.jspecify.annotations.NullMarked;

/**
 * メッセージインターフェースから {@link Translator} を生成するためのファクトリークラス。
 */
@NullMarked
final class TranslatorFactory {

    private TranslatorFactory() {
    }

    /**
     * 指定されたメッセージインターフェースから {@link Translator} を生成する。
     *
     * @param messageInterface メッセージインターフェース
     * @return {@code Translator}
     * @throws IllegalArgumentException メッセージインターフェースに {@link TranslationStoreName} アノテーションが付与されていない場合
     */
    static Translator from(final Class<?> messageInterface) {

        // TranslationStoreを生成
        final Key storeName = getName(messageInterface);
        final MiniMessage miniMessage = CustomMiniMessage.CUSTOM_MINI_MESSAGE;
        final KotonohaTranslationStore<String> translator = KotonohaTranslationStore.miniMessage(storeName, miniMessage);

        // デフォルト言語を日本語に設定
        translator.defaultLocale(Locale.JAPAN);

        // インターフェース内のすべてのメソッドから翻訳を登録
        translator.registerInterface(messageInterface);

        return translator;
    }

    @SuppressWarnings("PatternValidation")
    private static Key getName(final Class<?> messageInterface) {
        // メッセージインターフェースに@TranslationStoreNameがついていなければ例外を投げる
        if (!messageInterface.isAnnotationPresent(TranslationStoreName.class)) {
            final String className = messageInterface.getName();
            final String message = "Translation interface must be annotated with @TranslationStore: " + className;
            throw new IllegalArgumentException(message);
        }

        // @TranslationStoreNameからTranslationStoreを識別するためのKeyを生成
        final TranslationStoreName storeAnnotation = messageInterface.getAnnotation(TranslationStoreName.class);
        return storeAnnotation.namespace().isBlank()
                ? Key.key(storeAnnotation.value())
                : Key.key(storeAnnotation.namespace(), storeAnnotation.value());
    }
}
