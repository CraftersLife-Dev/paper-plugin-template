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
package io.github.crafterslife.dev.papertemplate.message;

import java.lang.reflect.InvocationHandler;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;

/**
 * このインターフェースは、アプリケーション全体の翻訳メッセージを定義し、その動的な解決および送信機能を提供する。
 *
 * <p>各メソッドは、{@link MessageKey} アノテーションを通じて翻訳キーと紐付けられ、
 * {@link InvocationHandler} を使用して実行時に処理される。</p>
 *
 * <p>戻り値が {@code void} または {@link Void} のメソッドは、第一引数に {@link Audience} を取り、
 * メッセージを送信する。それ以外の場合は、{@link Component Component} 型の翻訳結果を返す。</p>
 *
 * @see MessageKey
 * @see TranslationServiceHandler
 */
// うにたろうの魔法で作られているので、メソッドを正しく定義すれば実装クラスがどこからともなく湧いてきます。
public interface TranslationService {

    /*
     * 翻訳メソッドを追加する際の注意事項:
     *
     * 1. '@MessageKey'アノテーションを必ず付与し、対応する翻訳キーを指定する。
     *    例: @MessageKey("yourplugin.command.teleport.failed")
     *
     * 2. メソッドに定義可能な引数の型は'TagResolver','String','Component'。
     *    上記以外の型は実行時に例外がスローされる。回避するには上記の型でラップする。
     *    例: int killCount = 24;
     *    　  TagResolver tag = Placeholder.parsed("kill_count", String.valueOf(killCount));
     *    　  Component component = Component.text(killCount);
     *
     * 3. 引数の型を'String'や'Component'で定義した場合は、変数名がタグ名となる。
     * 　　変数名はスネークケースへ変換されてからタグ名に使用される。
     *    例: welcomeMessage(Component playerName, string level)
     *    　  playerName -> <player_name>
     *    　  level      -> <level>
     *
     * 4. 戻り値の型を'void'、第一引数を'Audience'で定義すると直接送信が可能。
     *    メソッドの第一引数が'Audience'じゃない場合は、例外がスローされる。
     *    例: 真下にあるメソッドを参照してください。
     */

    /**
     * 設定と翻訳を再読み込みしたときに呼び出すメッセージ。
     *
     * @param audience 再読み込みの実行者
     */
    @MessageKey("papertemplate.command.reload.success") // TODO: プレフィックスは必ず変えてね
    void sendCommandConfigReloadSuccess(Audience audience);
}
