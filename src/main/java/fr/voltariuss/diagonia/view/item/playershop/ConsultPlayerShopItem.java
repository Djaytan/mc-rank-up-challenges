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

import com.google.common.base.Preconditions;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.GuiItem;
import fr.voltariuss.diagonia.controller.BukkitUtils;
import fr.voltariuss.diagonia.controller.MessageController;
import fr.voltariuss.diagonia.controller.playershop.PlayerShopListController;
import fr.voltariuss.diagonia.model.entity.PlayerShop;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;
import net.kyori.adventure.text.minimessage.template.TemplateResolver;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton
public class ConsultPlayerShopItem {

  private final BukkitUtils bukkitUtils;
  private final MessageController messageController;
  private final MiniMessage miniMessage;
  private final PlayerShopListController playerShopListController;
  private final ResourceBundle resourceBundle;

  @Inject
  public ConsultPlayerShopItem(
      @NotNull BukkitUtils bukkitUtils,
      @NotNull MessageController messageController,
      @NotNull MiniMessage miniMessage,
      @NotNull PlayerShopListController playerShopListController,
      @NotNull ResourceBundle resourceBundle) {
    this.bukkitUtils = bukkitUtils;
    this.messageController = messageController;
    this.miniMessage = miniMessage;
    this.playerShopListController = playerShopListController;
    this.resourceBundle = resourceBundle;
  }

  public @Nullable GuiItem createItem(
      @NotNull OfflinePlayer ownerPlayer, @NotNull PlayerShop playerShop) {
    Preconditions.checkState(
        ownerPlayer.getUniqueId().equals(playerShop.getOwnerUuid()),
        "The owner player specified isn't the same than the one of the playershop.");

    if (!playerShop.isActive()) {
      return null;
    }

    String ownerName = bukkitUtils.getOfflinePlayerName(ownerPlayer);

    Component itemName = getName(ownerName);
    List<Component> itemLore = getLore(playerShop.getDescription());

    GuiItem psItem;

    if (playerShop.hasItemIcon()) {
      psItem = ItemBuilder.from(playerShop.getItemIcon()).name(itemName).lore(itemLore).asGuiItem();
    } else {
      psItem =
          ItemBuilder.skull()
              .owner(ownerPlayer)
              .name(itemName)
              .lore(itemLore)
              .asGuiItem(onClick(playerShop));
    }

    return psItem;
  }

  private @NotNull GuiAction<InventoryClickEvent> onClick(@NotNull PlayerShop playerShop) {
    return event -> {
      Player player = (Player) event.getWhoClicked();
      playerShopListController.teleportToPlayerShop(player, playerShop);
    };
  }

  private @NotNull Component getName(@NotNull String ownerName) {
    return miniMessage
        .deserialize(
            resourceBundle.getString("diagonia.playershop.list.shop.name"),
            TemplateResolver.templates(
                Template.template("diag_player_name", miniMessage.deserialize(ownerName))))
        .decoration(TextDecoration.ITALIC, false);
  }

  private @NotNull List<Component> getLore(@Nullable String psDesc) {
    return Collections.singletonList(
        (psDesc != null
                ? miniMessage.deserialize(
                    resourceBundle.getString("diagonia.playershop.list.shop.description"),
                    TemplateResolver.templates(Template.template("diag_ps_description", psDesc)))
                : miniMessage.deserialize(
                    resourceBundle.getString("diagonia.playershop.list.shop.description.default")))
            .decoration(TextDecoration.ITALIC, false));
  }
}
