package fr.voltariuss.diagonia.view.item.rankup;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import fr.voltariuss.diagonia.model.config.RankConfig;
import java.util.Collections;
import java.util.ResourceBundle;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Singleton
public class RankChallengeItem {

  private final MiniMessage miniMessage;
  private final ResourceBundle resourceBundle;

  @Inject
  public RankChallengeItem(
      @NotNull MiniMessage miniMessage, @NotNull ResourceBundle resourceBundle) {
    this.miniMessage = miniMessage;
    this.resourceBundle = resourceBundle;
  }

  public @NotNull GuiItem createItem(RankConfig.RankChallenge rankChallenge, int amountGiven) {
    return ItemBuilder.from(rankChallenge.getChallengeItemMaterial())
        .name(
            miniMessage
                .deserialize(
                    String.format(
                        resourceBundle.getString("diagonia.rankup.rankup.challenge.name"),
                        rankChallenge.getChallengeItemMaterial().name()))
                .decoration(TextDecoration.ITALIC, false))
        .lore(
            Collections.singletonList(
                miniMessage
                    .deserialize(
                        String.format(
                            resourceBundle.getString("diagonia.rankup.rankup.challenge.progress"),
                            amountGiven,
                            rankChallenge.getChallengeItemAmount()))
                    .decoration(TextDecoration.ITALIC, false)))
        .asGuiItem();
  }
}
