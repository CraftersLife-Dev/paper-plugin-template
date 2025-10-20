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

import java.lang.reflect.Type;
import java.util.Objects;
import net.kyori.adventure.key.Key;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

/**
 * {@code Configurate} のためのカスタム {@link TypeSerializer} 。
 * <p>
 * 設定ファイルにおいて、Minecraftのキー形式（例: {@code minecraft:stone}）の文字列を使用して、
 * {@link Material} オブジェクトをシリアライズおよびデシリアライズします。
 */
@NullMarked
public final class MaterialSerializer implements TypeSerializer<Material> {

    /**
     * このシリアライザーのインスタンスを生成する。
     */
    public MaterialSerializer() {
    }

    /**
     * 設定ノードから {@link Key} を取得し、それを使用して {@link Registry#MATERIAL} から対応する
     * {@link Material} オブジェクトを取得する。
     *
     * @param type デシリアライズ対象の型情報
     * @param node 設定ファイル上のノード
     * @return デシリアライズされた {@link Material}
     * @throws SerializationException ノードからキーが取得できない、または対応するマテリアルが見つからない場合
     */
    @Override
    public Material deserialize(final Type type, final ConfigurationNode node) throws SerializationException {
        final String nodeKey = node.getString();

        if (nodeKey == null) {
            throw new SerializationException(Material.class, "Failed to deserialize from %s".formatted(node.toString()));
        }

        return Objects.requireNonNull(Material.matchMaterial(nodeKey));
    }

    /**
     * {@link Material} オブジェクトのキーを設定ノードにシリアライズする。
     *
     * @param type シリアライズ対象の型
     * @param obj  シリアライズ対象の {@link Material} オブジェクト
     * @param node 設定ファイル上のノード
     * @throws SerializationException シリアライズ中にエラーが発生した場合
     */
    @Override
    public void serialize(final Type type, @Nullable final Material obj, final ConfigurationNode node) throws SerializationException {
        if (obj == null) {
            node.set(null);
        } else {
            node.set(obj.key().asString());
        }
    }
}
