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
import fr.voltariuss.diagonia.controller.api.RankUpChallengesController;
import fr.voltariuss.diagonia.model.config.data.rank.Rank;
import fr.voltariuss.diagonia.model.entity.RankChallengeProgression;
import fr.voltariuss.diagonia.model.service.api.dto.GiveActionType;
import fr.voltariuss.diagonia.view.GiveActionTypeConverter;
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
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

@Singleton
public class RankChallengeItem {

  private final GiveActionTypeConverter giveActionTypeConverter;
  private final MiniMessage miniMessage;
  private final RankUpChallengesController rankUpChallengesController;
  private final ResourceBundle resourceBundle;

  @Inject
  public RankChallengeItem(
      @NotNull GiveActionTypeConverter giveActionTypeConverter,
      @NotNull MiniMessage miniMessage,
      @NotNull RankUpChallengesController rankUpChallengesController,
      @NotNull ResourceBundle resourceBundle) {
    this.giveActionTypeConverter = giveActionTypeConverter;
    this.miniMessage = miniMessage;
    this.rankUpChallengesController = rankUpChallengesController;
    this.resourceBundle = resourceBundle;
  }

  public @NotNull GuiItem createItem(
      @NotNull Rank rank, @NotNull RankChallengeProgression rankChallengeProgression) {
    Preconditions.checkNotNull(rank);
    Preconditions.checkNotNull(rankChallengeProgression);

    boolean isChallengeCompleted = isChallengeCompleted(rankChallengeProgression);

    Component itemName = getName(rankChallengeProgression.getChallengeMaterial());
    List<Component> itemLore = getLore(rankChallengeProgression, isChallengeCompleted);

    return ItemBuilder.from(rankChallengeProgression.getChallengeMaterial())
        .name(itemName)
        .lore(itemLore)
        .asGuiItem(onClick(rank, rankChallengeProgression.getChallengeMaterial()));
  }

  private @NotNull GuiAction<InventoryClickEvent> onClick(
      @NotNull Rank rank, @NotNull Material challengeMaterial) {
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

      int nbItemsInInventory = countItem(whoClicked.getInventory(), clickedItem.getType());

      rankUpChallengesController.giveItemChallenge(
          whoClicked, rank, challengeMaterial, giveActionType, nbItemsInInventory);
    };
  }

  private boolean isChallengeCompleted(@NotNull RankChallengeProgression rankChallengeProgression) {
    return rankChallengeProgression.getChallengeAmountGiven()
        >= rankChallengeProgression.getChallengeAmountRequired();
  }

  private int countItem(Inventory inventory, Material material) {
    return Arrays.stream(inventory.getContents())
        .filter(item -> item != null && item.getType().equals(material))
        .mapToInt(ItemStack::getAmount)
        .sum();
  }

  private @NotNull Component getName(@NotNull Material challengeMaterial) {
    return miniMessage
        .deserialize(
            resourceBundle.getString("diagonia.rankup.challenges.item.name"),
            TagResolver.resolver(
                Placeholder.component(
                    "diag_challenge_name",
                    Component.translatable(challengeMaterial.translationKey()))))
        .decoration(TextDecoration.ITALIC, false);
  }

  private @NotNull @UnmodifiableView List<Component> getLore(
      @NotNull RankChallengeProgression rankChallengeProgression, boolean isChallengeCompleted) {
    List<Component> lore = new ArrayList<>();
    lore.addAll(getProgressLorePart(rankChallengeProgression, isChallengeCompleted));
    lore.add(Component.empty());
    lore.addAll(isChallengeCompleted ? getCompletedLorePart() : getActionLorePart());
    return Collections.unmodifiableList(lore);
  }

  private @NotNull @UnmodifiableView List<Component> getProgressLorePart(
      @NotNull RankChallengeProgression rankChallengeProgression, boolean isChallengeCompleted) {
    return List.of(
        miniMessage
            .deserialize(
                resourceBundle.getString("diagonia.rankup.challenges.item.lore.progress"),
                TagResolver.resolver(
                    Placeholder.component(
                        "diag_amount_given",
                        getCurrentProgression(
                            isChallengeCompleted,
                            String.valueOf(rankChallengeProgression.getChallengeAmountGiven()))),
                    Placeholder.unparsed(
                        "diag_amount_required",
                        String.valueOf(rankChallengeProgression.getChallengeAmountRequired()))))
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
        TagResolver.resolver(Placeholder.unparsed("diag_current_progression", value)));
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
