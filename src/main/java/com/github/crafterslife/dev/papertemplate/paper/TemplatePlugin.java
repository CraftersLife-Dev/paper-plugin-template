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
package com.github.crafterslife.dev.papertemplate.paper;

import com.github.crafterslife.dev.papertemplate.paper.event.listeners.ExampleListener;
import java.util.Set;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class TemplatePlugin extends JavaPlugin { // TODO: クラス名は書き換えてね

    private final Set<Listener> listenerFactories;

    TemplatePlugin(final TemplateContext context) {

        // リスナークラスを定義したらここに追加
        this.listenerFactories = Set.of(
                new ExampleListener(context)
        );
    }

    @Override
    public void onEnable() {

        // リスナーを登録
        this.listenerFactories.forEach(listener -> this.getServer().getPluginManager().registerEvents(listener, this));
    }
}
