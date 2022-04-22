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
import fr.voltariuss.diagonia.model.config.PluginConfig;
import java.util.stream.IntStream;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.jetbrains.annotations.NotNull;

@Singleton
public class PrepareItemEnchantListener implements Listener {

  private final PluginConfig pluginConfig;
  private final RemakeBukkitLogger logger;

  @Inject
  public PrepareItemEnchantListener(
      @NotNull PluginConfig pluginConfig, @NotNull RemakeBukkitLogger logger) {
    this.pluginConfig = pluginConfig;
    this.logger = logger;
  }

  private static final int NB_ENCHANTING_SLOTS = 3;

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onPrepareItemEnchant(@NotNull PrepareItemEnchantEvent event) {
    EnchantmentOffer[] enchantmentOffers = event.getOffers();

    IntStream.range(0, NB_ENCHANTING_SLOTS)
        .forEach(
            i -> {
              EnchantmentOffer enchantmentOffer = enchantmentOffers[i];
              if (pluginConfig
                  .getBlacklistedEnchantments()
                  .contains(enchantmentOffer.getEnchantment())) {
                logger.info(
                    "Blacklisted enchantment {} detected for an EnchantmentOffer, replacing it by"
                        + " the durability one.",
                    enchantmentOffer.getEnchantment().getKey().getKey().toUpperCase());

                enchantmentOffer.setEnchantment(Enchantment.DURABILITY);
                enchantmentOffer.setEnchantmentLevel(3);
              }
            });
  }
}
