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
package com.github.crafterslife.dev.papertemplate.integration;

import io.github.miniplaceholders.api.MiniPlaceholders;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import java.util.Objects;

/**
 * MiniPlaceholdersプラグインのAPIを利用するためのラッパークラス。
 *
 * <p>このクラスは、 {@link MiniPlaceholders} に破壊的な変更が加えられた場合でも、
 * 柔軟に対応するためのラッパークラスである。</p>
 */
public final class MiniPlaceholdersExpansion {

    private static byte miniPlaceholdersLoaded = -1;

    public MiniPlaceholdersExpansion() {
    }

    /**
     * MiniPlaceholdersが読み込まれているかどうかを確認する。
     *
     * @return MiniMessageが読み込まれていればtrue
     */
    private static boolean miniPlaceholdersLoaded() {
        if (miniPlaceholdersLoaded == -1) {
            try {
                final String name = MiniPlaceholders.class.getName();
                Objects.requireNonNull(name);
                miniPlaceholdersLoaded = 1;
            } catch (final NoClassDefFoundError error) {
                miniPlaceholdersLoaded = 0;
            }
        }
        return miniPlaceholdersLoaded == 1;
    }

    /**
     * グローバル・プレースホルダーに基づいてTagResolverを取得する。
     *
     * <p>MiniPlaceholdersが読み込まれている場合はグローバル・プレースホルダーを返すが、
     * 読み込まれていない場合は空っぽのTagResolverを返す。</p>
     *
     * @return グローバル・プレースホルダーに基づいたTagResolver
     */
    public static TagResolver globalPlaceholders() {
        if (MiniPlaceholdersExpansion.miniPlaceholdersLoaded()) {
            return MiniPlaceholders.getGlobalPlaceholders();
        }

        return TagResolver.empty();
    }

    /**
     * オーディエンス、およびグローバルのプレースホルダーに基づいてTagResolverを取得する。
     *
     * <p>MiniPlaceholdersが読み込まれている場合は、オーディエンス、およびグローバルのプレースホルダーを返すが、
     * 読み込まれていない場合は空っぽのTagResolverを返す。</p>
     *
     * @return オーディエンス、およびグローバルのプレースホルダーに基づいたTagResolver
     */
    public static TagResolver audiencePlaceholders(final Audience audience) {
        if (MiniPlaceholdersExpansion.miniPlaceholdersLoaded()) {
            return MiniPlaceholders.getAudienceGlobalPlaceholders(audience);
        }

        return TagResolver.empty();
    }
}
