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

package fr.voltariuss.diagonia.controller;

import com.google.common.base.Preconditions;
import fr.voltariuss.diagonia.DiagoniaRuntimeException;
import fr.voltariuss.diagonia.RemakeBukkitLogger;
import fr.voltariuss.diagonia.model.config.PluginConfig;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton
public class EnchantmentControllerImpl implements EnchantmentController {

  private static final Enchantment FALLBACK_ENCHANTMENT = Enchantment.DURABILITY;

  private final RemakeBukkitLogger logger;
  private final PluginConfig pluginConfig;

  @Inject
  public EnchantmentControllerImpl(
      @NotNull RemakeBukkitLogger logger, @NotNull PluginConfig pluginConfig) {
    this.logger = logger;
    this.pluginConfig = pluginConfig;
  }

  @Override
  public void removeBlacklistedEnchantments(@Nullable ItemStack itemStack) {
    if (itemStack == null || itemStack.getType() == Material.AIR || !itemStack.hasItemMeta()) {
      return;
    }

    ItemMeta itemMeta = itemStack.getItemMeta();

    pluginConfig.getBlacklistedEnchantments().forEach(itemMeta::removeEnchant);

    if (itemMeta instanceof EnchantmentStorageMeta enchantmentStorageMeta) {
      pluginConfig
          .getBlacklistedEnchantments()
          .forEach(enchantmentStorageMeta::removeStoredEnchant);
    }
    itemStack.setItemMeta(itemMeta);

    // TODO: recover removed enchantments, send message to player about the remove and log them
  }

  @Override
  public void removeBlacklistedEnchantments(@NotNull Map<Enchantment, Integer> enchantments) {
    Preconditions.checkNotNull(enchantments);
    pluginConfig.getBlacklistedEnchantments().forEach(enchantments::remove);
  }

  @Override
  public void fillEmptyEnchantedBook(@Nullable ItemStack itemStack) {
    if (itemStack == null
        || itemStack.getType() == Material.AIR
        || !(itemStack.getItemMeta() instanceof EnchantmentStorageMeta enchantmentStorageMeta)) {
      return;
    }

    /*
     * According to the interface specifications (see @apiNote), we consider items with
     * EnchantmentStorageMeta as enchanted books.
     */
    if (!enchantmentStorageMeta.hasStoredEnchants()) {
      boolean hasMetaChanged =
          enchantmentStorageMeta.addStoredEnchant(Enchantment.DURABILITY, 3, false);

      if (!hasMetaChanged) {
        throw new DiagoniaRuntimeException(
            "Failed to add store enchantment in enchanted book. This isn't supposed to ever"
                + " happen.");
      }
    }
  }

  @Override
  public void addFallbackEnchantmentIfEmpty(@NotNull Map<Enchantment, Integer> enchantments) {
    Preconditions.checkNotNull(enchantments);

    if (enchantments.isEmpty()) {
      enchantments.put(FALLBACK_ENCHANTMENT, FALLBACK_ENCHANTMENT.getMaxLevel());
    }
  }

  @Override
  public void applyFallbackEnchantmentOffer(@NotNull EnchantmentOffer enchantmentOffer) {
    Preconditions.checkNotNull(enchantmentOffer);

    Enchantment blacklistedEnchantment = enchantmentOffer.getEnchantment();
    int blacklistedEnchantmentLevel = enchantmentOffer.getEnchantmentLevel();
    Enchantment fallbackEnchantment = FALLBACK_ENCHANTMENT;

    enchantmentOffer.setEnchantment(fallbackEnchantment);
    enchantmentOffer.setEnchantmentLevel(fallbackEnchantment.getMaxLevel());

    logger.debug(
        "Blacklisted enchantment offer '{}' (level {}) replaced by '{}' (level {})",
        blacklistedEnchantment.getKey().getKey(),
        blacklistedEnchantmentLevel,
        enchantmentOffer.getEnchantment().getKey().getKey(),
        enchantmentOffer.getEnchantmentLevel());
  }

  @Override
  public boolean isBlacklistedEnchantment(@NotNull Enchantment enchantment) {
    Preconditions.checkNotNull(enchantment);
    return pluginConfig.getBlacklistedEnchantments().contains(enchantment);
  }
}
