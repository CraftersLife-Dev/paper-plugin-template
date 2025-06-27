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

import com.github.crafterslife.dev.papertemplate.paper.TemplateContext;
import com.github.crafterslife.dev.papertemplate.utility.MoreFiles;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.configuration.PluginMeta;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.kyori.adventure.text.minimessage.translation.MiniMessageTranslationStore;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.Translator;
import net.kyori.adventure.util.UTF8ResourceBundleControl;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 翻訳可能なシステムメッセージの管理を担う。
 *
 * <p>このクラスは、翻訳可能なシステムメッセージの読み込みや、メッセージファイルの書き込みをおこなう。</p>
 */
// このクラスをシングルトンで設計しなかった理由は、テストを容易にするため
@SuppressWarnings("UnstableApiUsage")
public final class TranslationSource {

    private static final Locale FALLBACK_LOCALE = Locale.JAPANESE; // 翻訳が見つからなかった場合にフォールバックする言語

    private final PluginMeta pluginMeta;
    private final ComponentLogger logger;
    private final Path pluginSource;
    private final Path translationsDirectory; // 翻訳メッセージのディレクトリ

    private final Set<Locale> installedLocales; // 翻訳可能な言語のコレクション
    private @Nullable MiniMessageTranslationStore translationStore; // 翻訳メッセージの格納

    /**
     * 頼むからこれは呼び出さないでくれ。
     *
     * @param bootstrapContext ブートストラップコンテキスト
     */
    public TranslationSource(final BootstrapContext bootstrapContext) {
        this.pluginMeta = bootstrapContext.getPluginMeta();
        this.logger = bootstrapContext.getLogger();
        this.pluginSource = bootstrapContext.getPluginSource();
        this.translationsDirectory = bootstrapContext.getDataDirectory().resolve("translationService");
        this.installedLocales = ConcurrentHashMap.newKeySet();

        try {
            MoreFiles.createDirectoriesIfNotExists(this.translationsDirectory);
        } catch (final IOException exception) {
            throw new UncheckedIOException("ディレクトリの生成に失敗: %s".formatted(this.translationsDirectory), exception);
        }
    }

    /**
     * 翻訳可能なシステムメッセージをすべて再読み込みする。
     */
    public void reloadTranslations() {
        this.logger.info("翻訳を読み込み中...");

        // このプラグインがすでに登録しているメッセージがある場合は消去する。
        if (this.translationStore != null) {
            GlobalTranslator.translator().removeSource(this.translationStore);
            this.installedLocales.clear();
        }

        // メッセージ保管所のインスタンスを生成する。
        // noinspection PatternValidation
        final Key translationKey = Key.key(this.pluginMeta.getName().toLowerCase(), "messages");
        this.translationStore = MiniMessageTranslationStore.create(translationKey);
        this.translationStore.defaultLocale(FALLBACK_LOCALE);

        // 翻訳ディレクトリ、およびプラグインjarからメッセージを読み込み、GlobalTranslatorに追加する。
        this.loadFromPluginDirectory();
        this.loadFromResourceBundle();
        GlobalTranslator.translator().addSource(this.translationStore);

        final String locales = this.installedLocales.stream()
                .map(Locale::toString)
                .collect(Collectors.joining(", "));
        this.logger.info("翻訳の読み込みに成功: [{}]", locales);
    }

    /**
     * 翻訳可能なメッセージを翻訳ディレクトリから読み込む。
     *
     * @throws UncheckedIOException ディレクトリの走査に失敗した場合
     */
    private void loadFromPluginDirectory() {
        Objects.requireNonNull(this.translationStore);
        try (final Stream<Path> pathStream = Files.list(this.translationsDirectory)) {
            // プラグインディレクトリ内にあるtranslationsディレクトリを走査する
            pathStream
                    .filter(Files::isRegularFile)
                    .filter(file -> file.getFileName().toString().startsWith("messages_"))
                    .filter(file -> file.getFileName().toString().endsWith(".properties"))
                    .forEach(translationFile -> {
                        final Locale locale = this.parseLocale(translationFile); // ファイル名からロケールを取得
                        this.translationStore.registerAll(locale, translationFile, true); // メッセージファイルを登録
                        this.installedLocales.add(locale); // 登録が終わった言語をインストール済み言語のコレクションへ追加
                    });
        } catch (final IOException exception) {
            throw new UncheckedIOException("ディレクトリの走査に失敗: %s".formatted(this.translationsDirectory), exception);
        }
    }

    /**
     * 翻訳可能なメッセージファイルをプラグインjarから読み込み、翻訳ディレクトリへのコピーも実行する
     *
     * @throws UncheckedIOException jarの走査に失敗した場合
     */
    private void loadFromResourceBundle() {
        Objects.requireNonNull(this.translationStore);
        try {
            // ディレクトリ内を走査する
            // jarはアーカイブファイルなので、一旦展開しなければFiles#listなどは直接使えない
            MoreFiles.walkAsDirectory(this.pluginSource, pathStream -> pathStream
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().startsWith("/translationService/messages_"))
                    .filter(path -> path.toString().endsWith(".properties"))
                    .map(filePath -> { // ロケールとファイルパスのペアを生成
                        record Translation(Locale locale, Path path) {}
                        return new Translation(this.parseLocale(filePath), filePath);
                    })
                    .filter(translation -> !this.installedLocales.contains(translation.locale())) // インストール済みロケールはスキップ
                    .forEach(translation -> { // loadFromPluginDirectoryと同じ処理を実行する
                        final ResourceBundle bundle = ResourceBundle.getBundle("translationService/messages", translation.locale(), UTF8ResourceBundleControl.get());
                        this.translationStore.registerAll(translation.locale(), bundle, true);
                        this.installedLocales.add(translation.locale());
                        this.copyFrom(translation.path()); // 翻訳ディレクトリへのコピー
                    }));
        } catch (final IOException exception) {
            throw new UncheckedIOException("プラグインJarの走査に失敗: %s".formatted(this.pluginSource), exception);
        }
    }

    /**
     * ファイル名をロケールへとパースする。
     *
     * <p>例: messages_ja_JP.properties -> jp_JP</p>
     *
     * @param targetPath 対象ファイルへのパス
     * @return ロケール
     * @throws IllegalArgumentException ロケールが不明な場合
     */
    private Locale parseLocale(final Path targetPath) {
        final String fileName = targetPath.getFileName().toString();
        final String localeString = fileName.substring("messages_".length()).replace(".properties", "");
        final Locale locale = Translator.parseLocale(localeString);
        if (locale == null) {
            throw new IllegalArgumentException("ロケールが不明: %s".formatted(localeString));
        }

        return locale;
    }

    /**
     * 指定したパスに存在するファイルを翻訳ディレクトリにコピーする。
     *
     * @param sourcePath コピー対象となるファイルへのパス
     * @throws UncheckedIOException ファイルのコピーに失敗した場合
     */
    private void copyFrom(final Path sourcePath) {
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
