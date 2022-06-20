/*
 * Rank-Up challenges plugin for Minecraft (Bukkit servers)
 * Copyright (C) 2022 - Loïc DUBOIS-TERMOZ
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package fr.djaytan.minecraft.rank_up_challenges.view.item.rankup;

import com.google.common.base.Preconditions;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.GuiItem;
import fr.djaytan.minecraft.rank_up_challenges.model.config.data.challenge.ChallengeConfig;
import fr.djaytan.minecraft.rank_up_challenges.model.config.data.challenge.ChallengeTier;
import fr.djaytan.minecraft.rank_up_challenges.controller.api.RankUpChallengesController;
import fr.djaytan.minecraft.rank_up_challenges.model.config.data.rank.Rank;
import fr.djaytan.minecraft.rank_up_challenges.model.entity.RankChallengeProgression;
import fr.djaytan.minecraft.rank_up_challenges.model.service.api.dto.GiveActionType;
import fr.djaytan.minecraft.rank_up_challenges.view.GiveActionTypeConverter;
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

  private final ChallengeConfig challengeConfig;
  private final GiveActionTypeConverter giveActionTypeConverter;
  private final MiniMessage miniMessage;
  private final RankUpChallengesController rankUpChallengesController;
  private final ResourceBundle resourceBundle;

  @Inject
  public RankChallengeItem(
      @NotNull ChallengeConfig challengeConfig,
      @NotNull GiveActionTypeConverter giveActionTypeConverter,
      @NotNull MiniMessage miniMessage,
      @NotNull RankUpChallengesController rankUpChallengesController,
      @NotNull ResourceBundle resourceBundle) {
    this.challengeConfig = challengeConfig;
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

    Component itemName = getName(rankChallengeProgression);
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

  private @NotNull Component getName(@NotNull RankChallengeProgression rankChallengeProgression) {
    ChallengeTier challengeTier =
        challengeConfig.getChallengeTier(rankChallengeProgression.getDifficultyTier());

    return miniMessage
        .deserialize(
            resourceBundle.getString("rank_up_challenges.rankup.challenges.item.name"),
            TagResolver.resolver(
                Placeholder.component(
                    "diag_challenge_name",
                    Component.translatable(
                        rankChallengeProgression.getChallengeMaterial().translationKey()))))
        .color(challengeTier.getColor())
        .decoration(TextDecoration.ITALIC, false);
  }

  private @NotNull @UnmodifiableView List<Component> getLore(
      @NotNull RankChallengeProgression rankChallengeProgression, boolean isChallengeCompleted) {
    List<Component> lore = new ArrayList<>();
    lore.add(getTierLorePart(rankChallengeProgression.getDifficultyTier()));
    lore.add(Component.empty());
    lore.addAll(getProgressLorePart(rankChallengeProgression, isChallengeCompleted));
    lore.add(Component.empty());
    lore.addAll(isChallengeCompleted ? getCompletedLorePart() : getActionLorePart());
    return Collections.unmodifiableList(lore);
  }

  private @NotNull Component getTierLorePart(int tier) {
    return miniMessage
        .deserialize(
            resourceBundle.getString("rank_up_challenges.rankup.challenges.item.lore.tier"),
            TagResolver.resolver(Placeholder.unparsed("diag_tier_value", toRomanNumber(tier))))
        .decoration(TextDecoration.ITALIC, false);
  }

  private @NotNull @UnmodifiableView List<Component> getProgressLorePart(
      @NotNull RankChallengeProgression rankChallengeProgression, boolean isChallengeCompleted) {
    return List.of(
        miniMessage
            .deserialize(
                resourceBundle.getString("rank_up_challenges.rankup.challenges.item.lore.progress"),
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
    String templateMessageKey = "rank_up_challenges.rankup.challenges.item.lore.progress.incomplete";

    if (isChallengeCompleted) {
      templateMessageKey = "rank_up_challenges.rankup.challenges.item.lore.progress.completed";
    }

    return miniMessage.deserialize(
        resourceBundle.getString(templateMessageKey),
        TagResolver.resolver(Placeholder.unparsed("diag_current_progression", value)));
  }

  private @NotNull @UnmodifiableView List<Component> getActionLorePart() {
    return List.of(
        miniMessage
            .deserialize(
                resourceBundle.getString("rank_up_challenges.rankup.challenges.item.lore.action.left_click"))
            .decoration(TextDecoration.ITALIC, false),
        miniMessage
            .deserialize(
                resourceBundle.getString("rank_up_challenges.rankup.challenges.item.lore.action.right_click"))
            .decoration(TextDecoration.ITALIC, false),
        miniMessage
            .deserialize(
                resourceBundle.getString(
                    "rank_up_challenges.rankup.challenges.item.lore.action.shift_right_click.1"))
            .decoration(TextDecoration.ITALIC, false),
        miniMessage
            .deserialize(
                resourceBundle.getString(
                    "rank_up_challenges.rankup.challenges.item.lore.action.shift_right_click.2"))
            .decoration(TextDecoration.ITALIC, false));
  }

  private @NotNull @UnmodifiableView List<Component> getCompletedLorePart() {
    return List.of(
        miniMessage
            .deserialize(resourceBundle.getString("rank_up_challenges.rankup.challenges.item.lore.completed"))
            .decoration(TextDecoration.ITALIC, false));
  }

  private @NotNull String toRomanNumber(int input) {
    if (input < 1 || input > 3999) return "Invalid Roman Number Value";
    StringBuilder s = new StringBuilder();
    while (input >= 1000) {
      s.append("M");
      input -= 1000;
    }
    while (input >= 900) {
      s.append("CM");
      input -= 900;
    }
    while (input >= 500) {
      s.append("D");
      input -= 500;
    }
    while (input >= 400) {
      s.append("CD");
      input -= 400;
    }
    while (input >= 100) {
      s.append("C");
      input -= 100;
    }
    while (input >= 90) {
      s.append("XC");
      input -= 90;
    }
    while (input >= 50) {
      s.append("L");
      input -= 50;
    }
    while (input >= 40) {
      s.append("XL");
      input -= 40;
    }
    while (input >= 10) {
      s.append("X");
      input -= 10;
    }
    while (input >= 9) {
      s.append("IX");
      input -= 9;
    }
    while (input >= 5) {
      s.append("V");
      input -= 5;
    }
    while (input >= 4) {
      s.append("IV");
      input -= 4;
    }
    while (input >= 1) {
      s.append("I");
      input -= 1;
    }
    return s.toString();
  }
}
