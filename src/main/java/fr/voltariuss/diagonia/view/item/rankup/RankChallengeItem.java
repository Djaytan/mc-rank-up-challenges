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

package fr.voltariuss.diagonia.view.item.rankup;

import com.google.common.base.Preconditions;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.GuiItem;
import fr.voltariuss.diagonia.controller.RankUpController;
import fr.voltariuss.diagonia.model.GiveActionType;
import fr.voltariuss.diagonia.model.config.rank.Rank;
import fr.voltariuss.diagonia.model.config.rank.RankChallenge;
import fr.voltariuss.diagonia.model.entity.RankChallengeProgression;
import fr.voltariuss.diagonia.view.GiveActionTypeConverter;
import fr.voltariuss.diagonia.view.message.RankUpMessage;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.stream.Stream;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;
import net.kyori.adventure.text.minimessage.template.TemplateResolver;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton
public class RankChallengeItem {

  private final GiveActionTypeConverter giveActionTypeConverter;
  private final MiniMessage miniMessage;
  private final RankUpController rankUpController;
  private final RankUpMessage rankUpMessage;
  private final ResourceBundle resourceBundle;

  @Inject
  public RankChallengeItem(
      @NotNull GiveActionTypeConverter giveActionTypeConverter,
      @NotNull MiniMessage miniMessage,
      @NotNull RankUpController rankUpController,
      @NotNull RankUpMessage rankUpMessage,
      @NotNull ResourceBundle resourceBundle) {
    this.giveActionTypeConverter = giveActionTypeConverter;
    this.miniMessage = miniMessage;
    this.rankUpController = rankUpController;
    this.rankUpMessage = rankUpMessage;
    this.resourceBundle = resourceBundle;
  }

  public @NotNull GuiItem createItem(
      @NotNull UUID playerUuid,
      @NotNull Rank rank,
      @NotNull RankChallenge rankChallenge,
      @Nullable RankChallengeProgression rankChallengeProgression) {
    Preconditions.checkState(
        rankChallengeProgression == null
            || rankChallenge.getChallengeItemMaterial()
                == rankChallengeProgression.getChallengeMaterial(),
        "The challenge and the associated progression must both concern a same item.");

    boolean isChallengeCompleted = isChallengeCompleted(rankChallenge, rankChallengeProgression);

    // TODO: refactoring - remove duplication
    if (!isChallengeCompleted) {
      return ItemBuilder.from(rankChallenge.getChallengeItemMaterial())
          .name(
              miniMessage
                  .deserialize(
                      resourceBundle.getString("diagonia.rankup.challenges.item.name.incomplete"),
                      TemplateResolver.templates(
                          Template.template(
                              "diag_challenge_name",
                              rankChallenge.getChallengeItemMaterial().name())))
                  .decoration(TextDecoration.ITALIC, false))
          .lore(
              Stream.concat(
                      Stream.of(
                          miniMessage
                              .deserialize(
                                  resourceBundle.getString(
                                      "diagonia.rankup.challenges.item.lore.progress"),
                                  TemplateResolver.templates(
                                      Template.template(
                                          "diag_amount_given",
                                          String.valueOf(
                                              rankChallengeProgression != null
                                                  ? rankChallengeProgression
                                                      .getChallengeAmountGiven()
                                                  : 0)),
                                      Template.template(
                                          "diag_amount_required",
                                          String.valueOf(rankChallenge.getChallengeItemAmount()))))
                              .decoration(TextDecoration.ITALIC, false),
                          Component.empty()),
                      Stream.of(
                          miniMessage
                              .deserialize(
                                  resourceBundle.getString(
                                      "diagonia.rankup.challenges.item.lore.action.left_click"))
                              .decoration(TextDecoration.ITALIC, false),
                          miniMessage
                              .deserialize(
                                  resourceBundle.getString(
                                      "diagonia.rankup.challenges.item.lore.action.right_click"))
                              .decoration(TextDecoration.ITALIC, false),
                          miniMessage
                              .deserialize(
                                  resourceBundle.getString(
                                      "diagonia.rankup.challenges.item.lore.action.shift_right_click.1"))
                              .decoration(TextDecoration.ITALIC, false),
                          miniMessage
                              .deserialize(
                                  resourceBundle.getString(
                                      "diagonia.rankup.challenges.item.lore.action.shift_right_click.2"))
                              .decoration(TextDecoration.ITALIC, false)))
                  .toList())
          .asGuiItem(onClick(rank, rankChallenge, isChallengeCompleted));
    }
    // TODO: feat - keep trace of amount asked even when challenge is completed
    return ItemBuilder.from(rankChallenge.getChallengeItemMaterial())
        .name(
            miniMessage
                .deserialize(
                    resourceBundle.getString("diagonia.rankup.challenges.item.name.completed"),
                    TemplateResolver.templates(
                        Template.template(
                            "diag_challenge_name",
                            rankChallenge.getChallengeItemMaterial().name())))
                .decoration(TextDecoration.ITALIC, false))
        .lore(
            List.of(
                miniMessage
                    .deserialize(
                        resourceBundle.getString("diagonia.rankup.rankup.challenge.completed"))
                    .decoration(TextDecoration.ITALIC, false)))
        .asGuiItem();
  }

  private @NotNull GuiAction<InventoryClickEvent> onClick(
      @NotNull Rank rank, @NotNull RankChallenge rankChallenge, boolean isChallengeCompleted) {
    return event -> {
      Player whoClicked = (Player) event.getWhoClicked();
      ItemStack clickedItem = event.getCurrentItem();
      GiveActionType giveActionType = giveActionTypeConverter.convert(event.getClick());

      if (clickedItem == null) {
        throw new NullPointerException(
            "The current item involved in the InventoryClickEvent can't be null.");
      }
      if (giveActionType == null) {
        return;
      }

      if (isChallengeCompleted) {
        whoClicked.sendMessage(rankUpMessage.challengeAlreadyCompleted());
        return;
      }

      int nbItemsInInventory = countItem(whoClicked.getInventory(), clickedItem.getType());

      if (nbItemsInInventory == 0) {
        whoClicked.sendMessage(rankUpMessage.noItemInInventory());
        return;
      }

      rankUpController.giveItemChallenge(
          whoClicked, rank, rankChallenge, giveActionType, nbItemsInInventory);
    };
  }

  private boolean isChallengeCompleted(
      @NotNull RankChallenge rankChallenge,
      @Nullable RankChallengeProgression rankChallengeProgression) {
    if (rankChallengeProgression == null) {
      return false;
    }

    return rankChallengeProgression.getChallengeAmountGiven()
        >= rankChallenge.getChallengeItemAmount();
  }

  private int countItem(Inventory inventory, Material material) {
    return Arrays.stream(inventory.getContents())
        .filter(item -> item != null && item.getType().equals(material))
        .mapToInt(ItemStack::getAmount)
        .sum();
  }
}
