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
package com.github.crafterslife.dev.papertemplate.paper.event.events;

import net.kyori.adventure.text.Component;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * カスタムイベントの実例。このイベントは、{@link Component} メッセージを変更できる。
 */
public class ExampleEvent extends Event {

    /**
     * このイベントのハンドラリスト。Bukkitのイベントシステムによって使用される。
     */
    private static final HandlerList HANDLER_LIST = new HandlerList();

    /**
     * このイベントが保持する Componentする。
     */
    private Component message;

    /**
     * 新しい {@code ExampleEvent} を生成する。
     *
     * @param message 設定するComponent
     */
    public ExampleEvent(final Component message) {
        this.message = message;
    }

    /**
     * このイベントに設定されているComponentを取得する。
     *
     * @return このイベントのComponent
     */
    public Component getMessage() {
        return this.message;
    }

    /**
     * このイベントのメッセージコンポーネントを設定する。
     *
     * @param message 設定するComponent
     */
    public void setMessage(final Component message) {
        this.message = message;
    }

    /**
     * このイベントクラスの静的なハンドラリストを取得する。
     * Bukkitのイベントシステムによって内部的に使用される。
     *
     * @return このイベントの静的なハンドラリスト
     */
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    /**
     * このイベントのハンドラリストを取得する。
     * イベントハンドラを登録するためにBukkitによって呼び出される。
     *
     * @return このイベントのハンドラリスト
     */
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
