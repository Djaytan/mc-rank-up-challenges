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

package fr.voltariuss.diagonia.listeners;

import fr.voltariuss.diagonia.model.config.PluginConfig;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Singleton
public class EntityShootBowListener implements Listener {

  private final PluginConfig pluginConfig;

  @Inject
  public EntityShootBowListener(@NotNull PluginConfig pluginConfig) {
    this.pluginConfig = pluginConfig;
  }

  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void onEntityShootBow(@NotNull EntityShootBowEvent event) {
    ItemStack bowItem = event.getBow();

    if (bowItem == null) {
      return;
    }

    event.setConsumeItem(true);
    pluginConfig.getBlacklistedEnchantments().forEach(event.getBow()::removeEnchantment);
  }
}
