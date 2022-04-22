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

import fr.voltariuss.diagonia.RemakeBukkitLogger;
import fr.voltariuss.diagonia.utils.ItemUtils;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Singleton
public class LootGenerateListener implements Listener {

  private final ItemUtils itemUtils;
  private final RemakeBukkitLogger logger;

  @Inject
  public LootGenerateListener(@NotNull ItemUtils itemUtils, @NotNull RemakeBukkitLogger logger) {
    this.itemUtils = itemUtils;
    this.logger = logger;
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onLootGenerate(LootGenerateEvent event) {
    List<ItemStack> lootItems = event.getLoot();

    for (int i = 0; i < lootItems.size(); i++) {
      ItemStack lootItem = lootItems.get(i);

      if (itemUtils.hasAnyBlacklistedEnchantment(lootItem)) {
        lootItems.set(i, null);
        logger.info("Blacklisted enchantment detected: remove item from loot table.");
      }
    }
  }
}
