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

package fr.voltariuss.diagonia.controller.listener.bukkit;

import fr.voltariuss.diagonia.controller.api.EnchantmentController;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemMendEvent;
import org.jetbrains.annotations.NotNull;

@Singleton
public class PlayerItemMendListener implements Listener {

  private final EnchantmentController enchantmentController;

  @Inject
  public PlayerItemMendListener(@NotNull EnchantmentController enchantmentController) {
    this.enchantmentController = enchantmentController;
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerItemMend(@NotNull PlayerItemMendEvent event) {
    event.setCancelled(true);

    Set<Enchantment> removedBlacklistedEnchantments =
        enchantmentController.removeBlacklistedEnchantments(event.getItem());

    if (removedBlacklistedEnchantments.isEmpty()) {
      return;
    }

    enchantmentController.sendRemovedBlacklistedEnchantmentsMessage(
        event.getPlayer(), removedBlacklistedEnchantments);
  }
}
