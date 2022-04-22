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

import fr.voltariuss.diagonia.controller.MessageController;
import fr.voltariuss.diagonia.utils.PredefinedItem;
import fr.voltariuss.diagonia.view.message.MinecraftMessage;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Singleton
public class InventoryClickListener implements Listener {

  private final MessageController messageController;
  private final MinecraftMessage minecraftMessage;
  private final PredefinedItem predefinedItem;

  @Inject
  public InventoryClickListener(
      @NotNull MessageController messageController,
      @NotNull MinecraftMessage minecraftMessage,
      @NotNull PredefinedItem predefinedItem) {
    this.messageController = messageController;
    this.minecraftMessage = minecraftMessage;
    this.predefinedItem = predefinedItem;
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onInventoryClick(@NotNull InventoryClickEvent event) {
    ItemStack currentItem = event.getCurrentItem();

    if (currentItem == null) {
      return;
    }

    if (currentItem.equals(predefinedItem.resultDeactivated())) {
      event.setCancelled(true);
      messageController.sendFailureMessage(
          event.getWhoClicked(), minecraftMessage.resultDeactivated());
    }
  }
}
