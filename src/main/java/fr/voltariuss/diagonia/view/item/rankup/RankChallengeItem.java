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
import fr.voltariuss.diagonia.controller.rankup.RankUpChallengesController;
import fr.voltariuss.diagonia.model.GiveActionType;
import fr.voltariuss.diagonia.model.config.rank.Rank;
import fr.voltariuss.diagonia.model.config.rank.RankChallenge;
import fr.voltariuss.diagonia.model.entity.RankChallengeProgression;
import fr.voltariuss.diagonia.view.GiveActionTypeConverter;
import fr.voltariuss.diagonia.view.message.RankUpMessage;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

@Singleton
public class RankChallengeItem {

  private final GiveActionTypeConverter giveActionTypeConverter;
  private final MiniMessage miniMessage;
  private final RankUpChallengesController rankUpChallengesController;
  private final RankUpMessage rankUpMessage;
  private final ResourceBundle resourceBundle;

  @Inject
  public RankChallengeItem(
      @NotNull GiveActionTypeConverter giveActionTypeConverter,
      @NotNull MiniMessage miniMessage,
      @NotNull RankUpChallengesController rankUpChallengesController,
      @NotNull RankUpMessage rankUpMessage,
      @NotNull ResourceBundle resourceBundle) {
    this.giveActionTypeConverter = giveActionTypeConverter;
    this.miniMessage = miniMessage;
    this.rankUpChallengesController = rankUpChallengesController;
    this.rankUpMessage = rankUpMessage;
    this.resourceBundle = resourceBundle;
  }

  public @NotNull GuiItem createItem(
      @NotNull Rank rank,
      @NotNull RankChallenge rankChallenge,
      @Nullable RankChallengeProgression rankChallengeProgression) {
    Preconditions.checkState(
        rankChallengeProgression == null
            || rankChallenge.getMaterial()
                == rankChallengeProgression.getChallengeMaterial(),
        "The challenge and the associated progression must both concern a same item.");

    boolean isChallengeCompleted = isChallengeCompleted(rankChallenge, rankChallengeProgression);

    Component itemName = getName(rankChallenge);
    List<Component> itemLore =
        getLore(rankChallenge, rankChallengeProgression, isChallengeCompleted);

    return ItemBuilder.from(rankChallenge.getMaterial())
        .name(itemName)
        .lore(itemLore)
        .asGuiItem(onClick(rank, rankChallenge, isChallengeCompleted));
  }

  private @NotNull GuiAction<InventoryClickEvent> onClick(
      @NotNull Rank rank, @NotNull RankChallenge rankChallenge, boolean isChallengeCompleted) {
    return event -> {
      Player whoClicked = (Player) event.getWhoClicked();
      ItemStack clickedItem = event.getCurrentItem();
      GiveActionType giveActionType = giveActionTypeConverter.convert(event.getClick());

      if (giveActionType == null) {
        return;
      }

      if (clickedItem == null) {
        throw new NullPointerException(
            "The current item involved in the InventoryClickEvent can't be null.");
      }
      // TODO: move controller logic... in controller!

      int nbItemsInInventory = countItem(whoClicked.getInventory(), clickedItem.getType());

      rankUpChallengesController.giveItemChallenge(
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
        >= rankChallenge.getAmount();
  }

  private int countItem(Inventory inventory, Material material) {
    return Arrays.stream(inventory.getContents())
        .filter(item -> item != null && item.getType().equals(material))
        .mapToInt(ItemStack::getAmount)
        .sum();
  }

  private @NotNull Component getName(@NotNull RankChallenge rankChallenge) {
    return miniMessage
        .deserialize(
            resourceBundle.getString("diagonia.rankup.challenges.item.name"),
            TemplateResolver.templates(
                Template.template(
                    "diag_challenge_name", rankChallenge.getMaterial().name())))
        .decoration(TextDecoration.ITALIC, false);
  }

  private @NotNull @UnmodifiableView List<Component> getLore(
      @NotNull RankChallenge rankChallenge,
      @Nullable RankChallengeProgression rankChallengeProgression,
      boolean isChallengeCompleted) {
    List<Component> lore = new ArrayList<>();
    lore.addAll(getProgressLorePart(rankChallenge, rankChallengeProgression, isChallengeCompleted));
    lore.add(Component.empty());
    lore.addAll(isChallengeCompleted ? getCompletedLorePart() : getActionLorePart());
    return Collections.unmodifiableList(lore);
  }

  private @NotNull @UnmodifiableView List<Component> getProgressLorePart(
      @NotNull RankChallenge rankChallenge,
      @Nullable RankChallengeProgression rankChallengeProgression,
      boolean isChallengeCompleted) {
    int amountGiven =
        rankChallengeProgression != null ? rankChallengeProgression.getChallengeAmountGiven() : 0;

    return List.of(
        miniMessage
            .deserialize(
                resourceBundle.getString("diagonia.rankup.challenges.item.lore.progress"),
                TemplateResolver.templates(
                    Template.template(
                        "diag_amount_given",
                        getCurrentProgression(isChallengeCompleted, String.valueOf(amountGiven))),
                    Template.template(
                        "diag_amount_required",
                        String.valueOf(rankChallenge.getAmount()))))
            .decoration(TextDecoration.ITALIC, false));
  }

  private @NotNull Component getCurrentProgression(
      boolean isChallengeCompleted, @NotNull String value) {
    String templateMessageKey = "diagonia.rankup.challenges.item.lore.progress.incomplete";

    if (isChallengeCompleted) {
      templateMessageKey = "diagonia.rankup.challenges.item.lore.progress.completed";
    }

    return miniMessage.deserialize(
        resourceBundle.getString(templateMessageKey),
        TemplateResolver.templates(Template.template("diag_current_progression", value)));
  }

  private @NotNull @UnmodifiableView List<Component> getActionLorePart() {
    return List.of(
        miniMessage
            .deserialize(
                resourceBundle.getString("diagonia.rankup.challenges.item.lore.action.left_click"))
            .decoration(TextDecoration.ITALIC, false),
        miniMessage
            .deserialize(
                resourceBundle.getString("diagonia.rankup.challenges.item.lore.action.right_click"))
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
            .decoration(TextDecoration.ITALIC, false));
  }

  private @NotNull @UnmodifiableView List<Component> getCompletedLorePart() {
    return List.of(
        miniMessage
            .deserialize(resourceBundle.getString("diagonia.rankup.challenges.item.lore.completed"))
            .decoration(TextDecoration.ITALIC, false));
  }
}
