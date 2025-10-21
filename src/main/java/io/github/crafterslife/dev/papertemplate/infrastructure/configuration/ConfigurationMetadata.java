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

import io.github.crafterslife.dev.papertemplate.infrastructure.configuration.annotations.ConfigHeader;
import io.github.crafterslife.dev.papertemplate.infrastructure.configuration.annotations.ConfigName;
import org.jspecify.annotations.NullMarked;

/**
 * 設定クラスのメタデータを保持するためのオブジェクト。
 * <p>
 * このレコードは、アノテーションから抽出された設定ファイル名とヘッダー情報を保持します。
 *
 * @param fileName   設定ファイル名
 * @param headerText 設定ファイルのヘッダー文字列
 */
@NullMarked
record ConfigurationMetadata(String fileName, String headerText) {

    ConfigurationMetadata {
        if (fileName.isBlank()) {
            throw new IllegalArgumentException("fileName must not be blank");
        }
    }

    /**
     * 指定された設定クラスからメタデータを抽出する。
     *
     * @param configClass 設定クラス
     * @return 抽出されたメタデータ
     * @throws IllegalArgumentException 必要なアノテーションが存在しない場合
     */
    static ConfigurationMetadata create(final Class<?> configClass) {

        // クラスに付与された@ConfigNameアノテーションの値から設定ファイル名を取得
        final ConfigName configNameAnnotation = configClass.getAnnotation(ConfigName.class);
        if (configNameAnnotation == null) {
            final String className = configClass.getName();
            final String message = "Configuration class must be annotated with @ConfigName: %s".formatted(className);
            throw new IllegalArgumentException(message);
        }

        // クラスに付与された@ConfigHeaderアノテーションの値から設定のヘッダーを取得
        final ConfigHeader headerAnnotation = configClass.getAnnotation(ConfigHeader.class);
        if (headerAnnotation == null) {
            final String className = configClass.getName();
            final String message = "Configuration class must be annotated with @ConfigHeader: %s".formatted(className);
            throw new IllegalArgumentException(message);
        }

        return new ConfigurationMetadata(configNameAnnotation.value(), headerAnnotation.value());
    }
}
