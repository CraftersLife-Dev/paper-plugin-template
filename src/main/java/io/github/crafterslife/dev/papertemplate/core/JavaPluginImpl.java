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
package io.github.crafterslife.dev.papertemplate.core;

import io.github.crafterslife.dev.papertemplate.core.resource.Config;
import io.github.crafterslife.dev.papertemplate.core.resource.Messages;
import io.github.crafterslife.dev.papertemplate.infrastructure.configuration.ConfigurationHolder;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;

/**
 * <p>プラグインのロジックやライフサイクルを管理するメインクラス。</p>
 */
@NullMarked
public final class JavaPluginImpl extends JavaPlugin {

    private final ConfigurationHolder<Config> configHolder;
    private final Messages messages;

    JavaPluginImpl(
            final ConfigurationHolder<Config> configHolder,
            final Messages messages
    ) {
        this.configHolder = configHolder;
        this.messages = messages;
    }

    @Override
    public void onEnable() {
        // Bukkit.getPluginManager().registerEvents(listener, this);
    }
}
