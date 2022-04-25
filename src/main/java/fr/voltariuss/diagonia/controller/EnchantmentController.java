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

import java.util.Map;
import java.util.Set;
import net.kyori.adventure.audience.Audience;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface EnchantmentController {

  @NotNull
  Set<Enchantment> removeBlacklistedEnchantments(@Nullable ItemStack itemStack);

  void removeBlacklistedEnchantments(@NotNull Map<Enchantment, Integer> enchantments);

  /**
   * Fills empty {@link org.bukkit.Material#ENCHANTED_BOOK} with {@link
   * org.bukkit.enchantments.Enchantment#DURABILITY} at max level as a stored enchantment.
   *
   * @param itemStack A mutable item (item other than enchanted books will be ignored).
   * @apiNote Non-stored enchantments will not be taken into account by this method. Furthermore, we
   *     consider all items with EnchantmentStorageMeta as enchanted books since the CraftBukkit
   *     implementation of the Bukkit API has only the
   *     org.bukkit.craftbukkit.inventory.CraftMetaEnchantedBook implementation for this interface.
   * @see org.bukkit.inventory.meta.EnchantmentStorageMeta
   */
  void fillEmptyEnchantedBook(@Nullable ItemStack itemStack);

  void addFallbackEnchantmentIfEmpty(@NotNull Map<Enchantment, Integer> enchantments);

  void applyFallbackEnchantmentOffer(@NotNull EnchantmentOffer enchantmentOffer);

  boolean isBlacklistedEnchantment(@NotNull Enchantment enchantment);

  void sendRemovedBlacklistedEnchantmentsMessage(
      @NotNull Audience audience, @NotNull Set<Enchantment> removedBlacklistedEnchantments);
}
