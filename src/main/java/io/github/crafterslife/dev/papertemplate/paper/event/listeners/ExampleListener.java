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
package io.github.crafterslife.dev.papertemplate.paper.event.listeners;

import io.github.crafterslife.dev.papertemplate.paper.TemplateContext;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * リスナーの実例
 */
public final class ExampleListener implements Listener {

    private final TemplateContext context;

    /**
     * 新しい {@code ExampleListener} を生成する。
     *
     * @param context プラグインのコンテキスト
     */
    public ExampleListener(final TemplateContext context) {
        this.context = context;
    }

    // Note: イベントハンドラはプライベートメソッドにしよう
    @EventHandler
    private void onInteract(final PlayerInteractEvent event) {

    }
}
