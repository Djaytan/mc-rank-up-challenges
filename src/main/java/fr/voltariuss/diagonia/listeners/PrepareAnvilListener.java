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
import fr.voltariuss.diagonia.utils.PredefinedItem;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

@Singleton
public class PrepareAnvilListener implements Listener {

  private final PluginConfig pluginConfig;
  private final PredefinedItem predefinedItem;

  @Inject
  public PrepareAnvilListener(
      @NotNull PluginConfig pluginConfig, @NotNull PredefinedItem predefinedItem) {
    this.pluginConfig = pluginConfig;
    this.predefinedItem = predefinedItem;
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPrepareAnvil(@NotNull PrepareAnvilEvent event) {
    ItemStack result = event.getResult();

    if (result == null) {
      return;
    }

    ItemMeta resultMeta = result.getItemMeta();

    if (resultMeta == null) {
      return;
    }

    if (hasAnyBlacklistedEnchantment(resultMeta)) {
      event.setResult(predefinedItem.resultDeactivated());
    }
  }

  private boolean hasAnyBlacklistedEnchantment(@NotNull ItemMeta itemMeta) {
    boolean hasAnyBlacklistedEnchantment =
        pluginConfig.getBlacklistedEnchantments().stream().anyMatch(itemMeta::hasEnchant);

    if (!hasAnyBlacklistedEnchantment
        && itemMeta instanceof EnchantmentStorageMeta enchantmentStorageMeta) {
      return pluginConfig.getBlacklistedEnchantments().stream()
          .anyMatch(enchantmentStorageMeta::hasStoredEnchant);
    }

    return hasAnyBlacklistedEnchantment;
  }
}
