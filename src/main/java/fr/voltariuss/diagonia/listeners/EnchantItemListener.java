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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.jetbrains.annotations.NotNull;

@Singleton
public class EnchantItemListener implements Listener {

  // TODO: use config
  public static final List<Enchantment> BLACKLISTED_ENCHANTMENTS =
      Arrays.asList(Enchantment.MENDING, Enchantment.ARROW_INFINITE);

  private final RemakeBukkitLogger logger;

  @Inject
  public EnchantItemListener(@NotNull RemakeBukkitLogger logger) {
    this.logger = logger;
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onItemEnchant(@NotNull EnchantItemEvent event) {
    Map<Enchantment, Integer> enchantsToAdd = event.getEnchantsToAdd();

    BLACKLISTED_ENCHANTMENTS.forEach(
        blackListedEnchantment -> {
          if (enchantsToAdd.containsKey(blackListedEnchantment)) {
            enchantsToAdd.remove(blackListedEnchantment);
            logger.info(
                "Blacklisted enchantment {} detected and deleted at enchantment time.",
                blackListedEnchantment.getKey().getKey().toUpperCase());
          }
        });
  }
}
