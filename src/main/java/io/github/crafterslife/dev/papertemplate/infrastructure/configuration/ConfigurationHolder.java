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

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jspecify.annotations.NullMarked;

/**
 * 設定の保持と再読み込みを管理するためのクラス。
 * <p>
 * このクラスは設定インスタンスの生成と、スレッドセーフな再読み込み機能を提供します。
 *
 * @param <C> 設定クラスの型
 */
@NullMarked
@SuppressWarnings("UnstableApiUsage")
public final class ConfigurationHolder<C> {

    private final ConfigurationLoader<C> configurationLoader;
    private final ComponentLogger logger;

    private volatile C currentConfig;
    private final ReadWriteLock lock;

    private ConfigurationHolder(
            final ConfigurationLoader<C> configurationLoader,
            final ComponentLogger logger
    ) throws UncheckedConfigurateException {
        this.configurationLoader = configurationLoader;
        this.logger = logger;
        this.lock = new ReentrantReadWriteLock();

        this.currentConfig = configurationLoader.load();
        logger.info("設定を読み込みました。");
    }

    /**
     * {@code ConfigHolder} のインスタンスを生成して返す。
     *
     * @param <C> 設定クラスの型
     * @param configClass 設定クラス
     * @param context プラグインのブートストラップ中に提供されるコンテキスト
     * @return {@code ConfigHolder} の新しいインスタンス
     * @throws UncheckedConfigurateException 設定の読み込みに失敗した場合
     */
    public static <C> ConfigurationHolder<C> from(final Class<C> configClass, final BootstrapContext context) throws UncheckedConfigurateException {
        final ConfigurationLoader<C> loader = ConfigurationLoader.from(configClass, context);
        return new ConfigurationHolder<>(loader, context.getLogger());
    }

    /**
     * 現在の設定を取得する。
     * <p>
     * このメソッドはスレッドセーフで、常に一貫性のある設定インスタンスを返します。
     *
     * @return 現在の設定
     */
    public C getConfig() {
        this.lock.readLock().lock();
        try {
            return this.currentConfig;
        } finally {
            this.lock.readLock().unlock();
        }
    }

    /**
     * 設定を再読み込みする。
     * <p>
     * 再読み込み中は他のスレッドからの読み取りをブロックし、設定の一貫性を保証します。
     *
     * @throws UncheckedConfigurateException 設定の再読み込みに失敗した場合
     */
    public void reloadConfig() throws UncheckedConfigurateException {
        this.lock.writeLock().lock();
        try {
            this.logger.info("設定を再読み込み中...");
            this.currentConfig = this.configurationLoader.load();
            this.logger.info("設定を再読み込みしました。");
        } finally {
            this.lock.writeLock().unlock();
        }
    }
}
