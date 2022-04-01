/*
 * Copyright (c) 2022 - Loïc DUBOIS-TERMOZ
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.voltariuss.diagonia;

import javax.inject.Inject;
import javax.inject.Singleton;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Singleton
public class CriticalErrorHandler {

  // used instead of DiagoniaLogger because of instanciation before Guice injection
  private final Logger logger;
  private final PluginManager pluginManager;
  private final JavaPlugin javaPlugin;

  @Inject
  public CriticalErrorHandler(
      @NotNull Logger logger,
      @NotNull PluginManager pluginManager,
      @NotNull JavaPlugin javaPlugin) {
    this.logger = logger;
    this.pluginManager = pluginManager;
    this.javaPlugin = javaPlugin;
  }

  public void raiseCriticalError(@NotNull String message) {
    raiseCriticalError(message, null);
  }

  public void raiseCriticalError(@NotNull String message, @Nullable Throwable cause) {
    logger.error(message, cause);
    logger.error("A critical error has been raised! Disabling plugin...");
    pluginManager.disablePlugin(javaPlugin);
  }
}
