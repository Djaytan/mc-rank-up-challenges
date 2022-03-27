package fr.voltariuss.diagonia.view.item.rankup;

import com.google.common.base.Preconditions;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.GuiItem;
import fr.voltariuss.diagonia.controller.RankUpController;
import fr.voltariuss.diagonia.model.config.rank.Rank;
import fr.voltariuss.diagonia.model.config.rank.RankUpPrerequisites;
import fr.voltariuss.diagonia.model.dto.RankUpProgression;
import fr.voltariuss.diagonia.view.EconomyFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.ResourceBundle;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;
import net.kyori.adventure.text.minimessage.template.TemplateResolver;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

@Singleton
public class RankUpItem {

  private final EconomyFormatter economyFormatter;
  private final MiniMessage miniMessage;
  private final RankUpController rankUpController;
  private final ResourceBundle resourceBundle;

  @Inject
  public RankUpItem(
      @NotNull EconomyFormatter economyFormatter,
      @NotNull MiniMessage miniMessage,
      @NotNull RankUpController rankUpController,
      @NotNull ResourceBundle resourceBundle) {
    this.economyFormatter = economyFormatter;
    this.miniMessage = miniMessage;
    this.rankUpController = rankUpController;
    this.resourceBundle = resourceBundle;
  }

  public @NotNull GuiItem createItem(
      @NotNull Rank rank, @NotNull RankUpProgression rankUpProgression) {
    Preconditions.checkNotNull(rank.getRankUpPrerequisites());
    RankUpPrerequisites rankUpPrerequisites = rank.getRankUpPrerequisites();

    // TODO: simplify instructions
    ItemBuilder itemBuilder =
        ItemBuilder.from(Material.WRITABLE_BOOK)
            .name(
                rankUpProgression.isRankOwned()
                    ? miniMessage
                        .deserialize(
                            resourceBundle.getString(
                                "diagonia.rankup.rankup.name.already_unlocked"))
                        .decoration(TextDecoration.ITALIC, false)
                    : miniMessage
                        .deserialize(resourceBundle.getString("diagonia.rankup.rankup.name"))
                        .decoration(TextDecoration.ITALIC, false))
            .lore(
                rankUpProgression.isRankOwned()
                    ? Collections.emptyList()
                    : Arrays.asList(
                        miniMessage
                            .deserialize(
                                resourceBundle.getString(
                                    "diagonia.rankup.rankup.cost.minecraft_xp"),
                                TemplateResolver.templates(
                                    Template.template(
                                        "diag_current_level",
                                        Component.text(
                                            String.valueOf(rankUpProgression.getCurrentXpLevel()),
                                            rankUpProgression.isXpLevelPrerequisiteDone()
                                                ? NamedTextColor
                                                    .GREEN // TODO: transfer it into resourceBundle
                                                : NamedTextColor.GRAY)),
                                    Template.template(
                                        "diag_required_level",
                                        String.valueOf(rankUpPrerequisites.getTotalMcExpLevels()))))
                            .decoration(TextDecoration.ITALIC, false),
                        miniMessage
                            .deserialize(
                                resourceBundle.getString("diagonia.rankup.rankup.cost.jobs_levels"),
                                TemplateResolver.templates(
                                    Template.template(
                                        "diag_current_level",
                                        Component.text(
                                            String.valueOf(rankUpProgression.getTotalJobsLevels()),
                                            rankUpProgression.isTotalJobsLevelsPrerequisiteDone()
                                                ? NamedTextColor.GREEN
                                                : NamedTextColor.GRAY)),
                                    Template.template(
                                        "diag_required_level",
                                        String.valueOf(rankUpPrerequisites.getTotalJobsLevel()))))
                            .decoration(TextDecoration.ITALIC, false),
                        miniMessage
                            .deserialize(
                                resourceBundle.getString("diagonia.rankup.rankup.cost.money"),
                                TemplateResolver.templates(
                                    Template.template(
                                        "diag_current_balance",
                                        Component.text(
                                            economyFormatter.format(
                                                rankUpProgression.getCurrentBalance()),
                                            rankUpProgression.isMoneyPrerequisiteDone()
                                                ? NamedTextColor.GREEN
                                                : NamedTextColor.GRAY)),
                                    Template.template(
                                        "diag_rankup_price",
                                        economyFormatter.format(
                                            rankUpPrerequisites.getMoneyPrice()))))
                            .decoration(TextDecoration.ITALIC, false),
                        Component.empty(),
                        rankUpProgression.canRankUp()
                            ? miniMessage
                                .deserialize(
                                    resourceBundle.getString("diagonia.rankup.rankup.unlock_rank"))
                                .decoration(TextDecoration.ITALIC, false)
                            : miniMessage
                                .deserialize(
                                    resourceBundle.getString(
                                        "diagonia.rankup.rankup.prerequisites_required"))
                                .decoration(TextDecoration.ITALIC, false)));
    return rankUpProgression.isRankOwned()
        ? itemBuilder.asGuiItem()
        : itemBuilder.asGuiItem(onClick(rankUpProgression));
  }

  public @NotNull GuiAction<InventoryClickEvent> onClick(
      @NotNull RankUpProgression rankUpProgression) {
    // TODO: create a real event with Observer pattern or Bukkit API
    return event -> {
      Player player = (Player) event.getWhoClicked();
      rankUpController.onRankUpRequested(player, rankUpProgression);
    };
  }
}
