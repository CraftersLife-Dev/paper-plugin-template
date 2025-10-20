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
package io.github.crafterslife.dev.papertemplate.core.commands;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import java.util.List;
import org.jspecify.annotations.NullMarked;

/**
 * <p>PaperのBrigadier APIを使用したコマンド。</p>
 */
@NullMarked
public interface BaseCommand {

    /**
     * コマンドノードを返します。
     *
     * @return コマンドのルートノード
     */
    LiteralCommandNode<CommandSourceStack> node();

    /**
     * このコマンドのエイリアスを返します。
     * <p>デフォルトでは空のリストを返します。</p>
     *
     * @return このコマンドのエイリアスの文字列リスト
     */
    default List<String> aliases() {
        return List.of();
    }

    /**
     * このコマンドの説明文を返します。
     *
     * @return コマンドの説明文
     */
    default String description() {
        return "";
    }
}
