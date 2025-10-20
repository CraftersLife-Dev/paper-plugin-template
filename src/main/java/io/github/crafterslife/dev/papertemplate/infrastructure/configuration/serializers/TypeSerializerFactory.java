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
package io.github.crafterslife.dev.papertemplate.infrastructure.configuration.serializers;

import net.kyori.adventure.serializer.configurate4.ConfigurateComponentSerializer;
import org.bukkit.Material;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

/**
 * {@code Configurate} で使用する {@link org.spongepowered.configurate.serialize.TypeSerializer} のコレクションを生成するためのファクトリークラス。
 * <p>
 * このクラスは、 {@code Adventure} の {@link net.kyori.adventure.text.Component} や {@link net.kyori.adventure.key.Key} のシリアライザーと、
 * このプラグイン専用のカスタムシリアライザーを統合したコレクションを提供します。
 */
@NullMarked
public final class TypeSerializerFactory {

    private TypeSerializerFactory() {
    }

    /**
     * TypeSerializerのコレクションを生成する。
     *
     * @return 生成されたTypeSerializerコレクション
     */
    // 作成したシリアライザーはここで登録する
    public static TypeSerializerCollection createSerializers() {
        return TypeSerializerCollection.builder()
                .registerAll(ConfigurateComponentSerializer.configurate().serializers())
                .register(Material.class, new MaterialSerializer())
                .build();
    }
}
