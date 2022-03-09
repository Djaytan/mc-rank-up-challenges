package fr.voltariuss.diagonia.view.item.rankup;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.google.common.base.Preconditions;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.GuiItem;
import fr.voltariuss.diagonia.controller.RankUpController;
import fr.voltariuss.diagonia.model.config.RankConfig;
import fr.voltariuss.diagonia.view.EconomyFormatter;
import java.util.*;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

@Singleton
public class RankUpItem {

  private final EconomyFormatter economyFormatter;
  private final Logger logger;
  private final MiniMessage miniMessage;
  private final RankUpController rankUpController;
  private final ResourceBundle resourceBundle;

  @Inject
  public RankUpItem(
      @NotNull EconomyFormatter economyFormatter,
      @NotNull Logger logger,
      @NotNull MiniMessage miniMessage,
      @NotNull RankUpController rankUpController,
      @NotNull ResourceBundle resourceBundle) {
    this.economyFormatter = economyFormatter;
    this.logger = logger;
    this.miniMessage = miniMessage;
    this.rankUpController = rankUpController;
    this.resourceBundle = resourceBundle;
  }

  public @NotNull GuiItem createItem(
      @NotNull Player whoOpen,
      @NotNull RankConfig.RankInfo rankInfo,
      boolean isRankOwned,
      double currentBalance) {
    Preconditions.checkNotNull(rankInfo.getRankUpPrerequisite());
    RankConfig.RankUpPrerequisite rankUpPrerequisite = rankInfo.getRankUpPrerequisite();
    JobsPlayer jobsPlayer = Jobs.getPlayerManager().getJobsPlayer(whoOpen);

    // TODO: business logic to put into model
    int currentXpLevel = whoOpen.getLevel();
    int requiredXpLevel = rankUpPrerequisite.getTotalMcExpLevels();
    boolean isXpLevelPrerequisiteDone = currentXpLevel >= requiredXpLevel;

    int currentTotalJobsLevel = jobsPlayer.getTotalLevels();
    int requiredTotalJobsLevel = rankUpPrerequisite.getTotalJobsLevel();
    boolean isTotalJobsLevelPrerequisiteDone = currentTotalJobsLevel >= requiredTotalJobsLevel;

    double price = rankUpPrerequisite.getMoneyPrice();
    boolean isMoneyPrerequisiteDone = currentBalance >= price;

    logger.info("isRankOwned={}", isRankOwned);

    boolean isRankable =
        isXpLevelPrerequisiteDone
            && isTotalJobsLevelPrerequisiteDone
            && isMoneyPrerequisiteDone
            && rankUpController.isRankable(whoOpen, rankInfo);

    logger.info("isRankable={}", isRankable);

    ItemBuilder itemBuilder =
        ItemBuilder.from(Material.WRITABLE_BOOK)
            .name(
                isRankOwned
                    ? miniMessage
                        .deserialize(
                            resourceBundle.getString(
                                "diagonia.rankup.rankup.name.already_unlocked"))
                        .decoration(TextDecoration.ITALIC, false)
                    : miniMessage
                        .deserialize(resourceBundle.getString("diagonia.rankup.rankup.name"))
                        .decoration(TextDecoration.ITALIC, false))
            .lore(
                isRankOwned
                    ? Collections.emptyList()
                    : Arrays.asList(
                        miniMessage
                            .deserialize(
                                String.format(
                                    resourceBundle.getString(
                                        "diagonia.rankup.rankup.cost.minecraft_xp"),
                                    String.format(
                                        "%s%s",
                                        isXpLevelPrerequisiteDone ? "<green>" : "", currentXpLevel),
                                    rankUpPrerequisite.getTotalMcExpLevels()))
                            .decoration(TextDecoration.ITALIC, false),
                        miniMessage
                            .deserialize(
                                String.format(
                                    resourceBundle.getString(
                                        "diagonia.rankup.rankup.cost.jobs_levels"),
                                    String.format(
                                        "%s%s",
                                        isTotalJobsLevelPrerequisiteDone ? "<green>" : "",
                                        currentTotalJobsLevel),
                                    rankUpPrerequisite.getTotalJobsLevel()))
                            .decoration(TextDecoration.ITALIC, false),
                        miniMessage
                            .deserialize(
                                String.format(
                                    resourceBundle.getString("diagonia.rankup.rankup.cost.money"),
                                    String.format(
                                        "%s%s",
                                        isMoneyPrerequisiteDone ? "<green>" : "",
                                        economyFormatter.format(currentBalance)),
                                    economyFormatter.format(rankUpPrerequisite.getMoneyPrice())))
                            .decoration(TextDecoration.ITALIC, false),
                        Component.empty(),
                        isRankable
                            ? miniMessage
                                .deserialize(
                                    resourceBundle.getString("diagonia.rankup.rankup.unlock_rank"))
                                .decoration(TextDecoration.ITALIC, false)
                            : miniMessage
                                .deserialize(
                                    resourceBundle.getString(
                                        "diagonia.rankup.rankup.prerequisites_required"))
                                .decoration(TextDecoration.ITALIC, false)));
    return isRankOwned ? itemBuilder.asGuiItem() : itemBuilder.asGuiItem(onClick());
  }

  public @NotNull GuiAction<InventoryClickEvent> onClick() {
    return event -> {
      Player player = (Player) event.getWhoClicked();
      rankUpController.rankUp(player);
    };
  }
}
