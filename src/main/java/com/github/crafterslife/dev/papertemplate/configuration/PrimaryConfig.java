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
package com.github.crafterslife.dev.papertemplate.configuration;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

/**
 * メイン設定のクラス。
 *
 * <p>{@link ConfigSerializable} でマークしているため、
 * {@link ConfigManager} で設定ファイルへのシリアライズが可能となっている。</p>
 * <p>WARN: このクラスのインスタンスを取得したい場合は、 {@link ConfigManager#primaryConfig()} からを実行してください。
 * このクラスのフィールドの値を直接参照してしまうと、config.ymlの値は無視されるため、大変危険です。</p>
 */
@ConfigSerializable
@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
public final class PrimaryConfig {

    private PrimaryConfig() {
        // インスタンスの不用意な生成を防ぐためにprivateで修飾している。
        // 何がなんでも絶対にpublicにはするな。
    }

    /*
     サンプルコード:

     フィールドとconfig.ymlの項目は1:1の関係
     private String prefix = "テスト項目"

     呼び出しにはgetterを使用する。
     public String template() {
         return this.template;
     }

     Note: 値を処理してから別な値を返すこともできるが推奨はしない。
           そうしたい場合は独自のシリアライザーを実装して、それをConfigManager#configurationLoaderで登録する。
           詳細: https://github.com/SpongePowered/Configurate/wiki/Type-Serializers
    */
}
