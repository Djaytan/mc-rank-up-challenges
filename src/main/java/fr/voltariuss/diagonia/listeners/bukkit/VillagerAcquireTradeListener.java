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

package fr.voltariuss.diagonia.listeners.bukkit;

import fr.voltariuss.diagonia.controller.EnchantmentController;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.inventory.MerchantRecipe;
import org.jetbrains.annotations.NotNull;

@Singleton
public class VillagerAcquireTradeListener implements Listener {

  private final EnchantmentController enchantmentController;

  @Inject
  public VillagerAcquireTradeListener(@NotNull EnchantmentController enchantmentController) {
    this.enchantmentController = enchantmentController;
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onVillagerAcquireTrade(@NotNull VillagerAcquireTradeEvent event) {
    MerchantRecipe initRecipe = event.getRecipe();
    enchantmentController.adjustEnchantments(initRecipe.getResult());
    MerchantRecipe adjustedRecipe =
        new MerchantRecipe(
            initRecipe.getResult(),
            initRecipe.getUses(),
            initRecipe.getMaxUses(),
            initRecipe.hasExperienceReward(),
            initRecipe.getVillagerExperience(),
            initRecipe.getPriceMultiplier(),
            initRecipe.getDemand(),
            initRecipe.getSpecialPrice(),
            initRecipe.shouldIgnoreDiscounts());
    event.getRecipe().getIngredients().forEach(adjustedRecipe::addIngredient);
    event.setRecipe(adjustedRecipe);
  }
}
