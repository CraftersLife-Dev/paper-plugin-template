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

import io.github.namiuni.kotonoha.annotations.ResourceBundle;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import java.util.Locale;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.translation.MiniMessageTranslationStore;
import net.kyori.adventure.translation.Translator;
import org.jspecify.annotations.NullMarked;

/**
 * {@link Translator} を生成するためのファクトリークラス。
 * <p>
 * このクラスは、{@link ResourceBundle} の読み込みと {@link Translator} の生成を管理します。
 */
@NullMarked
@SuppressWarnings("UnstableApiUsage")
final class TranslatorFactory {

    private final Key storeName;
    private final DynamicResourceBundleProvider bundleProvider;

    private TranslatorFactory(
            final Key storeName,
            final DynamicResourceBundleProvider bundleProvider
    ) {
        this.storeName = storeName;
        this.bundleProvider = bundleProvider;
    }

    /**
     * 指定されたメッセージサービスクラスとメタデータから {@code TranslatorFactory} を作成する。
     *
     * @param messageServiceClass メッセージサービスクラス
     * @param context             プラグインのブートストラップ中に提供されるコンテキスト
     * @return 構築された{@code TranslatorFactory}
     * @throws IllegalArgumentException メッセージサービスクラスに {@link ResourceBundle} アノテーションが付与されていない場合
     */
    @SuppressWarnings("PatternValidation")
    public static TranslatorFactory from(final Class<?> messageServiceClass, final BootstrapContext context) {
        if (!messageServiceClass.isAnnotationPresent(ResourceBundle.class)) {
            final String className = messageServiceClass.getName();
            final String message = "Translation interface must be annotated with @ResourceBundle: " + className;
            throw new IllegalArgumentException(message);
        }

        final String pluginName = context.getConfiguration().getName().toLowerCase();
        final ResourceBundle bundleAnnotation = messageServiceClass.getAnnotation(ResourceBundle.class);
        final String bundleBaseName = bundleAnnotation.baseName();
        final Key key = Key.key(pluginName, bundleBaseName);

        // リソースバンドルプロバイダを生成
        final var bundleProvider = DynamicResourceBundleProvider.from(context.getDataDirectory(), bundleBaseName);

        return new TranslatorFactory(key, bundleProvider);
    }

    /**
     * {@link Translator} を生成する。
     * <p>
     * このメソッドは、すべてのサポートされているロケールの翻訳を {@link net.kyori.adventure.translation.TranslationStore} に登録します。
     *
     * @return 生成された {@code Translator}
     * @throws TranslationException 翻訳の読み込みに失敗した場合
     */
    Translator create() throws TranslationException {
        try {
            // TranslationStoreを生成
            final MiniMessage miniMessage = CustomMiniMessage.CUSTOM_MINI_MESSAGE;
            final var translator = MiniMessageTranslationStore.create(this.storeName, miniMessage);

            // デフォルト言語を日本語に設定
            translator.defaultLocale(Locale.JAPAN);

            // すべてのリソースバンドルを取得
            final var bundles = this.bundleProvider.getBundles();

            // すべてのリソースバンドルを登録
            bundles.forEach(bundle -> translator.registerAll(bundle.getLocale(), bundle, false));

            return translator;
        } catch (final Exception exception) {
            throw new TranslationException("翻訳の読み込みに失敗しました。", exception);
        }
    }
}
