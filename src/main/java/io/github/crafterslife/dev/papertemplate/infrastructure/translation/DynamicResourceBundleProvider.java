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

import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Optional;
import java.util.ResourceBundle;
import org.jspecify.annotations.NullMarked;

/**
 * {@link DynamicResourceBundleControl} を使用して {@link ResourceBundle} を取得するためのユーティリティークラス。
 */
@NullMarked
final class DynamicResourceBundleProvider {

    private final String baseName;
    private final ResourceBundle.Control control;
    private final ClassLoader classLoader;

    private DynamicResourceBundleProvider(
            final String baseName,
            final ResourceBundle.Control control,
            final ClassLoader classLoader
    ) {
        this.baseName = baseName;
        this.control = control;
        this.classLoader = classLoader;
    }

    /**
     * {@code DynamicResourceBundleProvider} を生成する。
     *
     * @param translationDirectory 翻訳ソースのディレクトリ
     * @param baseName             リソースバンドルのベースネーム
     * @return 構築された{@code ResourceBundleProvider}
     */
    static DynamicResourceBundleProvider from(final Path translationDirectory, final String baseName) {
        final ResourceBundle.Control control = new DynamicResourceBundleControl(translationDirectory);
        final ClassLoader classLoader = DynamicResourceBundleProvider.class.getClassLoader();

        return new DynamicResourceBundleProvider(baseName, control, classLoader);
    }

    /**
     * {@link ResourceBundle} のリストを取得する。
     *
     * @return リソースバンドルのリスト
     */
    List<ResourceBundle> getBundles() {
        return Locale.availableLocales()
                .map(this::getBundle)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .distinct()
                .toList();
    }

    private Optional<ResourceBundle> getBundle(final Locale locale) {
        try {
            final ResourceBundle bundle = ResourceBundle.getBundle(this.baseName, locale, this.classLoader, this.control);
            return Optional.ofNullable(bundle);
        } catch (final MissingResourceException ignore) {
            return Optional.empty();
        }
    }
}
