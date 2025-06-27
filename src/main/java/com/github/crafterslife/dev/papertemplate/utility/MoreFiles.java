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
package com.github.crafterslife.dev.papertemplate.utility;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Files関係のユーティリティの集合体。
 */
public final class MoreFiles {

    private MoreFiles() {

    }

    /**
     * ディレクトリが存在しなければ生成する。
     *
     * @param path 生成するディレクトリ
     * @throws IOException ディレクトリが生成できなかった場合
     */
    public static void createDirectoriesIfNotExists(final Path path) throws IOException {
        if (Files.exists(path) && (Files.isDirectory(path) || Files.isSymbolicLink(path))) {
            return;
        }

        try {
            Files.createDirectories(path);
        } catch (final FileAlreadyExistsException ignore) {
            // ignore
        }
    }

    /**
     * アーカイブファイルでもディレクトリのように走査する。
     *
     * @param archivePath 対象のアーカイブファイル
     * @param consumer ストリームのコンシューマー
     * @throws IOException ディレクトリを走査できなかった場合
     */
    public static void walkAsDirectory(final Path archivePath, final Consumer<Stream<Path>> consumer) throws IOException {

        // ディレクトリだった場合は普通に走査
        if (Files.isDirectory(archivePath)) {
            try (Stream<Path> paths = Files.walk(archivePath)) {
                consumer.accept(paths);
            }
        }

        // アーカイブファイルだった場合は裏技使って走査
        try (FileSystem archiveFile = FileSystems.newFileSystem(archivePath, MoreFiles.class.getClassLoader())) {
            final Path rootPath = archiveFile.getRootDirectories().iterator().next();
            try (Stream<Path> paths = Files.walk(rootPath)) {
                consumer.accept(paths);
            }
        }
    }
}
