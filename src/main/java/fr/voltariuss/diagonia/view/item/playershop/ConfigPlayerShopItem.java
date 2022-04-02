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

package fr.voltariuss.diagonia.view.item.playershop;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.GuiItem;
import fr.voltariuss.diagonia.controller.PlayerShopController;
import java.util.Collections;
import java.util.ResourceBundle;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

@Singleton
public class ConfigPlayerShopItem {

  private static final Material MANAGE_ITEM_MATERIAL = Material.COMPARATOR;

  private final MiniMessage miniMessage;
  private final PlayerShopController playerShopController;
  private final ResourceBundle resourceBundle;

  @Inject
  public ConfigPlayerShopItem(
      @NotNull MiniMessage miniMessage,
      @NotNull PlayerShopController playerShopController,
      @NotNull ResourceBundle resourceBundle) {
    this.miniMessage = miniMessage;
    this.playerShopController = playerShopController;
    this.resourceBundle = resourceBundle;
  }

  public @NotNull GuiItem createItem() {
    return ItemBuilder.from(MANAGE_ITEM_MATERIAL)
        .name(
            miniMessage
                .deserialize(resourceBundle.getString("diagonia.playershop.config.name"))
                .decoration(TextDecoration.ITALIC, false))
        .lore(
            Collections.singletonList(
                miniMessage
                    .deserialize(resourceBundle.getString("diagonia.playershop.config.description"))
                    .decoration(TextDecoration.ITALIC, false)))
        .asGuiItem(onClick());
  }

  public @NotNull GuiAction<InventoryClickEvent> onClick() {
    return event -> playerShopController.openConfigPlayerShopView((Player) event.getWhoClicked());
  }
}
