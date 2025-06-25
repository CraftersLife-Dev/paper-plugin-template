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

import org.bukkit.Bukkit;

public final class MiniPlaceholdersExpansion {

//    private static final String PLUGIN_NAME = "PluginName"; // TODO: プラグイン名に変えてね

    public MiniPlaceholdersExpansion() {
    }

    public static boolean miniPlaceholdersLoaded() {
        return Bukkit.getPluginManager().isPluginEnabled("MiniPlaceholders");
    }

/*
    public void registerExpansion() {
        final var expansion = Expansion.builder(PLUGIN_NAME)
                    .audiencePlaceholder("your_audience_placeholder", (audience, queue, ctx) ->
                            Tag.selfClosingInserting())
                    .globalPlaceholder("your_global_placeholder", ((argumentQueue, context) ->
                            Tag.selfClosingInserting(Component.empty())))
                .build();
        expansion.register();
    }
*/
}
