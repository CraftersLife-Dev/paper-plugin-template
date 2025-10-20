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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * jarファイルと外部ディレクトリの両方から翻訳ファイルを動的に読み込むための
 * {@link ResourceBundle.Control}のカスタム実装。
 * <p>
 * このクラスは、jar内のリソースバンドルを読み込むだけでなく、
 * 特定のデータディレクトリにファイルをインストールし、そこから優先的に読み込む機能を提供します。
 * これにより、ユーザーは外部ファイルとして翻訳を簡単にカスタマイズできます。
 * <p>
 * 読み込みの優先順位:
 * <ol>
 *   <li>外部ディレクトリに存在するファイル（カスタマイズされた翻訳）</li>
 *   <li>jar内のリソースファイル（デフォルト翻訳）</li>
 * </ol>
 */
@NullMarked
final class DynamicResourceBundleControl extends ResourceBundle.Control {

    private final Path directoryPath;

    /**
     * このクラスのインスタンスを生成します。
     *
     * @param directoryPath リソースバンドルを保存および読み込むためのディレクトリパス
     */
    DynamicResourceBundleControl(final Path directoryPath) {
        this.directoryPath = directoryPath;
    }

    @Override
    public @Nullable ResourceBundle newBundle(
            final String baseName,
            final Locale locale,
            final String format,
            final ClassLoader loader,
            final boolean reload
    ) throws IOException {
        final String resourceFileName = this.toResourceName(
                this.toBundleName(baseName, locale),
                "properties"
        );

        // 外部ファイルが存在する場合は、そちらを優先的に読み込む
        final Path externalFilePath = this.directoryPath.resolve(resourceFileName);
        if (Files.exists(externalFilePath)) {
            return this.loadFromExternalFile(externalFilePath);
        }

        // 外部ファイルが存在しない場合は、jar内のリソースを読み込み、外部に出力する
        return this.loadFromJarAndInstall(resourceFileName, externalFilePath, loader);
    }

    /**
     * 外部ファイルからリソースバンドルを読み込む。
     *
     * @param filePath 読み込むファイルのパス
     * @return 読み込まれたリソースバンドル
     * @throws IOException ファイルの読み込み中にエラーが発生した場合
     */
    private ResourceBundle loadFromExternalFile(final Path filePath) throws IOException {
        return new PropertyResourceBundle(
                Files.newBufferedReader(filePath, StandardCharsets.UTF_8)
        );
    }

    /**
     * jar内のリソースを読み込み、外部ディレクトリにインストールする。
     *
     * @param resourceFileName リソースファイル名
     * @param externalFilePath インストール先のファイルパス
     * @param loader           クラスローダー
     * @return 読み込まれたリソースバンドル、またはリソースが見つからない場合はnull
     * @throws IOException ファイルの読み込みまたは書き込み中にエラーが発生した場合
     */
    private @Nullable ResourceBundle loadFromJarAndInstall(
            final String resourceFileName,
            final Path externalFilePath,
            final ClassLoader loader
    ) throws IOException {
        try (InputStream inputStream = loader.getResourceAsStream(resourceFileName)) {
            if (inputStream == null) {
                return null;
            }

            final byte[] content = inputStream.readAllBytes();
            this.installToExternalDirectory(content, externalFilePath);

            return new PropertyResourceBundle(
                    new InputStreamReader(
                            new ByteArrayInputStream(content),
                            StandardCharsets.UTF_8
                    )
            );
        }
    }

    /**
     * リソースバンドルのプロパティファイルを外部ディレクトリにインストールする。
     * <p>
     * 必要に応じて親ディレクトリを作成します。
     *
     * @param content  インストールするファイルの内容
     * @param filePath インストール先のファイルパス
     * @throws IOException ファイルの書き込み中にエラーが発生した場合
     */
    private void installToExternalDirectory(final byte[] content, final Path filePath) throws IOException {
        final Path parentDirectory = filePath.getParent();
        if (parentDirectory != null) {
            Files.createDirectories(parentDirectory);
        }
        Files.write(filePath, content);
    }
}
