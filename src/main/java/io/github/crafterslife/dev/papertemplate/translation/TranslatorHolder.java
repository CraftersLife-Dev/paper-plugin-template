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

import java.io.UncheckedIOException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import net.kyori.adventure.translation.Translator;
import org.jspecify.annotations.NullMarked;

/**
 * プラグインのアドベンチャー用のホルダー。
 */
@NullMarked
public final class TranslatorHolder implements Supplier<Translator> {

    private final TranslatorLoader translatorLoader;
    private final AtomicReference<Translator> translator;

    /**
     * 初期の変換読み込みを実行して、新しいホルダーを作成する。
     *
     * @param translatorLoader 翻訳の読み込みに使用されるローダー
     * @throws UncheckedIOException 初期読み込み時に翻訳ファイルを読み込めない場合
     */
    public TranslatorHolder(final TranslatorLoader translatorLoader) throws UncheckedIOException {
        this.translatorLoader = translatorLoader;

        final Translator initial = translatorLoader.loadTranslator();
        this.translator = new AtomicReference<>(initial);
    }

    /**
     * ディスクから新しい {@link Translator} を読み込み、それを返します。
     *
     * @throws UncheckedIOException 翻訳ファイルを読み込めない場合
     */
    public void reload() throws UncheckedIOException {
        this.translatorLoader.loadTranslator();
    }

    /**
     * 現在アクティブな {@link Translator} を返します。
     *
     * @return 現在のトランスレーター
     */
    @Override
    public Translator get() {
        return this.translator.get();
    }
}
