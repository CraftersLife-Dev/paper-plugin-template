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
package io.github.crafterslife.dev.papertemplate.core.resource;

import io.github.crafterslife.dev.papertemplate.infrastructure.configuration.annotations.ConfigHeader;
import io.github.crafterslife.dev.papertemplate.infrastructure.configuration.annotations.ConfigName;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

/**
 * プラグインの設定ファイルをマッピングするためのクラス
 */
//Note: 1. フィールドはfinalにはしない。
//      2. メソッドで値を取得する。
@NullMarked
@SuppressWarnings("FieldMayBeFinal")
@ConfigSerializable
@ConfigName("config.yml")
@ConfigHeader("プラグインの設定ファイル")
public final class Config {

    // 自分でインスタンスを生成しないための防御的プライベートコンストラクタ
    private Config() {
    }
}
