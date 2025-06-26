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

import com.github.crafterslife.dev.papertemplate.utility.MoreFiles;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.kyori.adventure.text.minimessage.translation.MiniMessageTranslationStore;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.Translator;
import net.kyori.adventure.util.UTF8ResourceBundleControl;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// このクラスがシングルトンで設計されていない理由は、テストを容易にするためです。
public final class TranslationRegistry {

    private static final Locale FALLBACK_LOCALE = Locale.JAPANESE;
    private static final String TRANSLATIONS_DIRECTORY_NAME = "translations";

    private final ComponentLogger logger;
    private final Path pluginSource;

    private final Path translationsDirectory;
    private final Set<Locale> installedLocales;

    private @MonotonicNonNull MiniMessageTranslationStore translationStore;

    public TranslationRegistry(
            final ComponentLogger logger,
            final Path pluginSource,
            final Path dataDirectory
    ) {
        this.logger = logger;
        this.pluginSource = pluginSource;

        this.translationsDirectory = dataDirectory.resolve(TRANSLATIONS_DIRECTORY_NAME);
        this.installedLocales = ConcurrentHashMap.newKeySet();

        try {
            MoreFiles.createDirectoriesIfNotExists(this.translationsDirectory);
        } catch (final IOException exception) {
            throw new UncheckedIOException("ディレクトリの生成に失敗: %s".formatted(this.translationsDirectory), exception);
        }
    }

    public void reloadTranslations() {
        this.logger.info("翻訳を読み込み中...");
        if (this.translationStore != null) {
            GlobalTranslator.translator().removeSource(this.translationStore);
            this.installedLocales.clear();
        }

        final Key translationKey = Key.key("template", "messages"); // TODO: namespaceはプラグイン名に変えてね
        this.translationStore = MiniMessageTranslationStore.create(translationKey);
        this.translationStore.defaultLocale(FALLBACK_LOCALE);

        this.loadFromPluginDirectory();
        this.loadFromResourceBundle();
        GlobalTranslator.translator().addSource(this.translationStore);

        final String locales = this.installedLocales.stream()
                .map(Locale::toString)
                .collect(Collectors.joining(", "));
        this.logger.info("翻訳の読み込みに成功: [{}]", locales);
    }

    private void loadFromPluginDirectory() {
        try (final Stream<Path> pathStream = Files.list(this.translationsDirectory)) {
            pathStream
                    .filter(Files::isRegularFile)
                    .filter(file -> file.startsWith("messages_"))
                    .filter(file -> file.endsWith(".properties"))
                    .forEach(translationFile -> {
                        final Locale locale = this.extractLocale(translationFile);
                        this.translationStore.registerAll(locale, translationFile, true);
                        this.installedLocales.add(locale);
                    });
        } catch (final IOException exception) {
            throw new UncheckedIOException("ディレクトリの走査に失敗: %s".formatted(this.translationsDirectory), exception);
        }
    }

    private void loadFromResourceBundle() {
        try {
            MoreFiles.walkAsDirectory(this.pluginSource, pathStream -> pathStream
                    .filter(Files::isRegularFile)
                    .filter(file -> file.toString().startsWith("/translations/messages_"))
                    .filter(file -> file.toString().endsWith(".properties"))
                    .map(file -> {
                        final Locale locale = this.extractLocale(file);
                        final ResourceBundle bundle = ResourceBundle.getBundle("translations/messages", locale, UTF8ResourceBundleControl.get());
                        return Map.entry(bundle, file);
                    })
                    .forEach(entry -> {
                        this.translationStore.registerAll(entry.getKey().getLocale(), entry.getKey(), true);
                        this.installedLocales.add(entry.getKey().getLocale());
                        this.copyTranslationsDirectoryFrom(entry.getValue());
                    }));
        } catch (final IOException exception) {
            throw new UncheckedIOException("プラグインJarの走査に失敗: %s".formatted(this.pluginSource), exception);
        }
    }

    private Locale extractLocale(final Path targetPath) {
        final String fileName = targetPath.getFileName().toString();
        final String localeString = fileName.substring("messages_".length()).replace(".properties", "");
        final Locale locale = Translator.parseLocale(localeString);
        if (locale == null) {
            throw new IllegalArgumentException("ロケールが不明: %s".formatted(localeString));
        }

        return locale;
    }

    private void copyTranslationsDirectoryFrom(final Path sourcePath) {
        try {
            final String fileName = sourcePath.getFileName().toString();
            Files.copy(sourcePath, this.translationsDirectory.resolve(fileName));
        } catch (final FileAlreadyExistsException ignore) {
            // ignore
        } catch (final IOException exception) {
            throw new UncheckedIOException("ファイルのコピーに失敗: %s".formatted(sourcePath) ,exception);
        }
    }
}
