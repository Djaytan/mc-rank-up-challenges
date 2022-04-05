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
import fr.voltariuss.diagonia.RemakeBukkitLogger;
import java.util.ResourceBundle;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Singleton
public class BukkitUtils {

  private final RemakeBukkitLogger logger;
  private final ResourceBundle resourceBundle;

  @Inject
  public BukkitUtils(@NotNull RemakeBukkitLogger logger, @NotNull ResourceBundle resourceBundle) {
    this.logger = logger;
    this.resourceBundle = resourceBundle;
  }

  /**
   * Removes N items in the specified inventory which match with the given material.
   *
   * <p>Unfortunately, no method works like expected in {@link Inventory} interface.
   *
   * @param inventory The inventory targeted by the remove of items.
   * @param material The material of targeted items to remove in the inventory.
   * @param n The number of items to remove in the inventory.
   * @return The number of items not removed from the inventory (>= 0).
   */
  public int removeItemsInInventory(
      @NotNull Inventory inventory, @NotNull Material material, int n) {
    Preconditions.checkNotNull(inventory);
    Preconditions.checkNotNull(material);
    Preconditions.checkArgument(
        n >= 0, "The number of items to remove in inventory must be higher or equals to zero.");

    if (inventory.getType() != InventoryType.PLAYER) {
      logger.warn(
          "Trying to remove items to another inventory than those of type PLAYER: cancelled.");
      return 0;
    }

    if (inventory.isEmpty()) {
      return 0;
    }

    int remainingItemsToRemove = n;
    int storageContentsSize = inventory.getStorageContents().length;
    ItemStack[] newStorageContent = new ItemStack[storageContentsSize];

    for (int i = 0; i < storageContentsSize; i++) {
      ItemStack itemStack = inventory.getStorageContents()[i];

      if (itemStack == null || !itemStack.getType().equals(material)) {
        newStorageContent[i] = itemStack;
        continue;
      }

      int amountToRemove = Math.min(remainingItemsToRemove, itemStack.getAmount());
      int newAmount = itemStack.getAmount() - amountToRemove;

      remainingItemsToRemove -= amountToRemove;

      if (newAmount > 0) {
        itemStack.setAmount(newAmount);
        newStorageContent[i] = itemStack;
      }
    }

    inventory.setStorageContents(newStorageContent);
    return remainingItemsToRemove;
  }

  public @NotNull String getOfflinePlayerName(@NotNull OfflinePlayer offlinePlayer) {
    String offlinePlayerName = offlinePlayer.getName();

    if (offlinePlayerName == null) {
      logger.warn("The UUID {} isn't associated to any player name.", offlinePlayer.getUniqueId());
      offlinePlayerName = resourceBundle.getString("diagonia.common.default_player_name");
    }

    return offlinePlayerName;
  }
}
