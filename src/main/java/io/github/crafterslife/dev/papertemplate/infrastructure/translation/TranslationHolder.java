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

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.Translator;
import org.jspecify.annotations.NullMarked;

/**
 * 翻訳ソースの保持と再読み込みを管理するクラス。
 * <p>
 * このクラスは {@link Translator} のインスタンス生成と、スレッドセーフな再読み込み機能を提供します。
 *
 * @param <M> メッセージクラスの型
 */
@NullMarked
@SuppressWarnings("UnstableApiUsage")
public final class TranslationHolder<M> {

    private final M messageService;
    private final TranslatorFactory translatorFactory;
    private final ComponentLogger logger;
    private final ReadWriteLock lock;

    private volatile Translator currentTranslator;

    private TranslationHolder(
            final M messageService,
            final TranslatorFactory translatorFactory,
            final ComponentLogger logger
    ) {
        this.messageService = messageService;
        this.translatorFactory = translatorFactory;
        this.logger = logger;
        this.lock = new ReentrantReadWriteLock();

        this.currentTranslator = translatorFactory.create();
        GlobalTranslator.translator().addSource(this.currentTranslator);
        logger.info("翻訳を読み込みました。");
    }

    /**
     * {@code TranslatorHolder} を生成して返す。
     *
     * @param <M> メッセージクラスの型
     * @param messageClass メッセージクラス
     * @param context プラグインのブートストラップ中に提供されるコンテキスト
     * @return {@code TranslatorHolder} の新しいインスタンス
     */
    public static <M> TranslationHolder<M> from(final Class<M> messageClass, final BootstrapContext context) {
        final M messageService = KotonohaMessagesFactory.from(messageClass);
        final TranslatorFactory loader = TranslatorFactory.from(messageClass, context);
        return new TranslationHolder<>(messageService, loader, context.getLogger());
    }

    /**
     * メッセージサービスを取得する。
     *
     * @return メッセージサービス
     */
    public M getMessageService() {
        return this.messageService;
    }

    /**
     * 翻訳ソースを再読み込みする。
     * <p>
     * 再読み込み中は他のスレッドからの読み取りをブロックし、翻訳の一貫性を保証します。
     * {@link GlobalTranslator} から古い翻訳ソースを削除し、新しい翻訳を登録します。
     */
    public void reloadTranslator() {
        this.lock.writeLock().lock();
        try {
            this.logger.info("翻訳を再読み込み中...");

            // グローバルソースから現在のトランスレーターを削除
            GlobalTranslator.translator().removeSource(this.currentTranslator);

            // トランスレーターを生成
            this.currentTranslator = this.translatorFactory.create();

            // グローバルソースに新しいトランスレーターを追加
            GlobalTranslator.translator().addSource(this.currentTranslator);
            this.logger.info("翻訳の再読み込みが完了しました");

        } finally {
            this.lock.writeLock().unlock();
        }
    }
}
