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
import fr.voltariuss.diagonia.DiagoniaLogger;
import fr.voltariuss.diagonia.controller.PlayerShopController;
import fr.voltariuss.diagonia.model.entity.PlayerShop;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
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

  private final DiagoniaLogger logger;
  private final MiniMessage miniMessage;
  private final PlayerShopController playerShopController;
  private final ResourceBundle resourceBundle;

  @Inject
  public ConsultPlayerShopItem(
      @NotNull DiagoniaLogger logger,
      @NotNull MiniMessage miniMessage,
      @NotNull PlayerShopController playerShopController,
      @NotNull ResourceBundle resourceBundle) {
    this.logger = logger;
    this.miniMessage = miniMessage;
    this.playerShopController = playerShopController;
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

    String ownerName =
        ownerPlayer.getName(); // TODO: manage case where binding UUID-Name is missing

    if (ownerName == null) {
      logger.error(
          "The UUID {} isn't associated to any existing user on the server.",
          playerShop.getOwnerUuid());
      return null;
    }

    Component psName =
        miniMessage
            .deserialize(
                resourceBundle.getString("diagonia.playershop.consult.name"),
                TemplateResolver.templates(Template.template("diag_player_name", ownerName)))
            .decoration(TextDecoration.ITALIC, false);

    List<Component> psDescLore =
        Collections.singletonList(
            (playerShop.getDescription() != null
                    ? Component.text(playerShop.getDescription())
                        .color(TextColor.fromCSSHexString("gray"))
                    : miniMessage.deserialize(
                        resourceBundle.getString(
                            "diagonia.playershop.consult.description.no_description_set")))
                .decoration(TextDecoration.ITALIC, false));

    GuiItem psItem;

    if (playerShop.hasItemIcon()) {
      psItem = ItemBuilder.from(playerShop.getItemIcon()).name(psName).lore(psDescLore).asGuiItem();
    } else {
      psItem =
          ItemBuilder.skull()
              .owner(ownerPlayer)
              .name(psName)
              .lore(psDescLore)
              .asGuiItem(getClickEvent(playerShop));
    }

    return psItem;
  }

  public @NotNull GuiAction<InventoryClickEvent> getClickEvent(@NotNull PlayerShop playerShop) {
    return event -> {
      Player player = (Player) event.getWhoClicked();
      playerShopController.teleportToPlayerShop(player, playerShop);
    };
  }
}
