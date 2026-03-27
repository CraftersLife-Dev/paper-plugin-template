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
package io.github.crafterslife.dev.papertemplate.configuration;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
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
public final class ConfigurationHolder<C> implements Supplier<C> {

    private final ConfigurationLoader<C> configLoader;
    private final AtomicReference<C> config;

    /**
     * {@code ConfigHolder} のインスタンスを生成して返す。
     *
     * @param configLoader 設定の読み込みに使用されるローダー
     * @param logger プラグインのブートストラップ中に提供されるコンテキスト
     * @throws UncheckedConfigurateException 設定の読み込みに失敗した場合
     */
    public ConfigurationHolder(
            final ConfigurationLoader<C> configLoader,
            final ComponentLogger logger
    ) throws UncheckedConfigurateException {
        this.configLoader = configLoader;
        this.config = new AtomicReference<>(configLoader.loadConfiguration());
        logger.info("設定を読み込みました。");
    }

    /**
     * 現在の設定を取得する。
     * <p>
     * このメソッドはスレッドセーフで、常に一貫性のある設定インスタンスを返します。
     *
     * @return 現在の設定
     */
    @Override
    public C get() {
        return this.config.get();
    }

    /**
     * 設定を再読み込みする。
     * <p>
     * 再読み込み中は他のスレッドからの読み取りをブロックし、設定の一貫性を保証します。
     *
     * @throws UncheckedConfigurateException 設定の再読み込みに失敗した場合
     */
    public void reload() throws UncheckedConfigurateException {
        final C loaded = this.configLoader.loadConfiguration();
        this.config.set(loaded);
    }
}
