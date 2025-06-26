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
package com.github.crafterslife.dev.papertemplate.paper;

import com.github.crafterslife.dev.papertemplate.configuration.ConfigManager;
import com.github.crafterslife.dev.papertemplate.message.TranslationManager;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;

/**
 * このプラグインのリソースをまとめたコンテナ
 *
 * @param pluginContext プラグインのメタ情報
 * @param configManager 設定管理
 * @param translationManager 翻訳管理
 */
@SuppressWarnings("UnstableApiUsage")
public record TemplateContext(PluginProviderContext pluginContext, ConfigManager configManager, TranslationManager translationManager) { // TODO: 書き換えてね

}
