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
package io.github.crafterslife.dev.papertemplate.translation;

import io.github.namiuni.kotonoha.annotations.Key;
import io.github.namiuni.kotonoha.annotations.Message;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.translation.MiniMessageTranslationStore;
import net.kyori.adventure.translation.Translator;
import org.jspecify.annotations.NullMarked;

/**
 * アノテーションに埋め込まれたデフォルト設定と、
 * プラグインの翻訳ディレクトリに保存された上書き設定に基づいて、
 * 完全に初期化されたTranslatorを読み込む。
 */
@NullMarked
public final class TranslatorLoader {

    // JIS Z 9103 https://ja.wikipedia.org/wiki/JIS%E5%AE%89%E5%85%A8%E8%89%B2
    private static final TextColor RED = TextColor.color(0xFF4B00);
    private static final TextColor YELLOW = TextColor.color(0xF2E700);
    private static final TextColor GREEN = TextColor.color(0x00B06B);
    private static final TextColor BLUE = TextColor.color(0x1971FF);
    private static final MiniMessage MINI_MESSAGE = MiniMessage.builder()
            .tags(TagResolver.builder()
                    .resolver(TagResolver.standard())
                    .tag("error", Tag.styling(RED))
                    .tag("warn", Tag.styling(YELLOW))
                    .tag("info", Tag.styling(GREEN))
                    .tag("debug", Tag.styling(BLUE))
                    .build())
            .build();

    private static final net.kyori.adventure.key.Key TRANSLATION_KEY =
            net.kyori.adventure.key.Key.key("template_plugin", "messages"); // TODO: namespaceをプラグイン名に変更

    private static final String FILE_PREFIX = "messages";
    private static final String FILE_SUFFIX = ".properties";

    private final Path translationDir;

    /**
     * 新しいローダーを構築し、{@code translation/} ディレクトリがまだ存在しない場合は、それを作成します。
     *
     * @param dataDirectory プラグインのデータディレクトリ
     * @throws UncheckedIOException 翻訳ディレクトリを作成できない場合
     */
    public TranslatorLoader(final Path dataDirectory) throws UncheckedIOException {
        this.translationDir = dataDirectory.resolve("translation");
        try {
            Files.createDirectories(this.translationDir);
        } catch (final IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }

    Translator loadTranslator() throws UncheckedIOException {
        final var store = MiniMessageTranslationStore.create(TRANSLATION_KEY, MINI_MESSAGE);
        store.defaultLocale(Locale.JAPAN);

        // 1. Register ROOT locale from compile-time annotations (ultimate fallback)
        final Map<String, String> rootTranslations = readAnnotations(Messages.class, Locale.ROOT)
                .messages()
                .stream()
                .collect(Collectors.toUnmodifiableMap(Translation.Message::key, Translation.Message::content));
        store.registerAll(Locale.ROOT, rootTranslations);

        // 2. Register all translation files present on disk (operator overrides)
        final Set<Locale> diskLocales = new HashSet<>();
        try (Stream<Path> files = Files.list(this.translationDir)) {
            files.filter(Files::isRegularFile)
                    .filter(TranslatorLoader::isTranslationFile)
                    .forEach(file -> {
                        final Locale locale = parseLocale(file);
                        store.registerAll(locale, file, false);
                        diskLocales.add(locale);
                    });
        } catch (final IOException exception) {
            throw new UncheckedIOException(exception);
        }

        // 3. Fill in locales defined in annotations but absent on disk, and write them out
        for (final Translation translation : readAllAnnotations(Messages.class)) {
            translation.messages().stream()
                    .filter(msg -> !store.contains(msg.key(), translation.locale()))
                    .forEach(msg -> store.register(msg.key(), translation.locale(), msg.content()));

            if (!diskLocales.contains(translation.locale())) {
                writeTranslationFile(this.translationDir, translation);
            }
        }

        return store;
    }

    // -------------------------------------------------------------------------
    // Annotation reading
    // -------------------------------------------------------------------------

    private static Set<Translation> readAllAnnotations(final Class<?> translationClass) {
        return Locale.availableLocales()
                .map(locale -> readAnnotations(translationClass, locale))
                .filter(translation -> !translation.messages().isEmpty())
                .collect(Collectors.toSet());
    }

    private static Translation readAnnotations(final Class<?> translationClass, final Locale locale) {
        final List<Translation.Message> messages = new ArrayList<>();

        for (final var method : translationClass.getMethods()) {
            final Key keyAnnotation = method.getAnnotation(Key.class);
            if (keyAnnotation == null) {
                continue;
            }
            final String key = keyAnnotation.value();
            for (final Message msg : method.getAnnotationsByType(Message.class)) {
                if (locale.equals(msg.locale().asLocale())) {
                    messages.add(new Translation.Message(key, msg.content()));
                }
            }
        }

        return new Translation(locale, messages);
    }

    // -------------------------------------------------------------------------
    // File name helpers
    // -------------------------------------------------------------------------

    private static boolean isTranslationFile(final Path file) {
        final String name = file.getFileName().toString();
        return name.startsWith(FILE_PREFIX) && name.endsWith(FILE_SUFFIX);
    }

    private static Locale parseLocale(final Path file) {
        final String name = file.getFileName().toString();
        final String tag = name.substring(FILE_PREFIX.length() + 1, name.length() - FILE_SUFFIX.length());
        final Locale locale = Translator.parseLocale(tag);
        if (locale == null) {
            throw new IllegalArgumentException("Cannot parse locale from translation file name: " + name);
        }
        return locale;
    }

    private static String fileNameFromLocale(final Locale locale) {
        if (locale == Locale.ROOT) {
            return "";
        }
        return FILE_PREFIX + "_" + locale + FILE_SUFFIX;
    }

    // -------------------------------------------------------------------------
    // File writing
    // -------------------------------------------------------------------------

    private static void writeTranslationFile(final Path parentDir, final Translation translation) throws UncheckedIOException {
        final Locale locale = translation.locale() == Locale.ROOT ? Locale.US : translation.locale();
        final String fileName = fileNameFromLocale(locale);
        if (fileName.isEmpty()) {
            return;
        }
        try (BufferedWriter writer = Files.newBufferedWriter(parentDir.resolve(fileName))) {
            for (final Translation.Message entry : translation.messages()) {
                writer.write(entry.key());
                writer.write('=');
                writer.write(entry.content());
                writer.newLine();
            }
        } catch (final IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }
}
