package fr.voltariuss.diagonia.view.item.rankup;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.google.common.base.Preconditions;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.GuiItem;
import fr.voltariuss.diagonia.controller.RankUpController;
import fr.voltariuss.diagonia.model.config.RankConfig;
import java.util.*;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.InheritanceNode;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

@Singleton
public class RankUpItem {

  private final BukkitScheduler bukkitScheduler;
  private final Economy economy;
  private final JavaPlugin javaPlugin;
  private final Logger logger;
  private final LuckPerms luckPerms;
  private final MiniMessage miniMessage;
  private final RankUpController rankUpController;
  private final ResourceBundle resourceBundle;

  @Inject
  public RankUpItem(
      @NotNull BukkitScheduler bukkitScheduler,
      @NotNull Economy economy,
      @NotNull JavaPlugin javaPlugin,
      @NotNull Logger logger,
      @NotNull LuckPerms luckPerms,
      @NotNull MiniMessage miniMessage,
      @NotNull RankUpController rankUpController,
      @NotNull ResourceBundle resourceBundle) {
    this.bukkitScheduler = bukkitScheduler;
    this.economy = economy;
    this.javaPlugin = javaPlugin;
    this.logger = logger;
    this.luckPerms = luckPerms;
    this.miniMessage = miniMessage;
    this.rankUpController = rankUpController;
    this.resourceBundle = resourceBundle;
  }

  public @NotNull GuiItem createItem(
      @NotNull Player whoOpen, @NotNull RankConfig.RankInfo rankInfo) {
    Preconditions.checkNotNull(rankInfo.getRankUpPrerequisite());
    RankConfig.RankUpPrerequisite rankUpPrerequisite = rankInfo.getRankUpPrerequisite();
    JobsPlayer jobsPlayer = Jobs.getPlayerManager().getJobsPlayer(whoOpen);
    User userPlayer = luckPerms.getUserManager().getUser(whoOpen.getUniqueId());
    Group playerGroup = luckPerms.getGroupManager().getGroup(userPlayer.getPrimaryGroup());
    Group rankGroup = luckPerms.getGroupManager().getGroup(rankInfo.getId());

    int currentXpLevel = whoOpen.getLevel();
    int requiredXpLevel = rankUpPrerequisite.getTotalMcExpLevels();
    boolean isXpLevelPrerequisiteDone = currentXpLevel >= requiredXpLevel;

    int currentTotalJobsLevel = jobsPlayer.getTotalLevels();
    int requiredTotalJobsLevel = rankUpPrerequisite.getTotalJobsLevel();
    boolean isTotalJobsLevelPrerequisiteDone = currentTotalJobsLevel >= requiredTotalJobsLevel;

    double currentMoney = economy.getBalance(whoOpen);
    double price = rankUpPrerequisite.getMoneyPrice();
    boolean isMoneyPrerequisiteDone = currentMoney >= price;

    boolean isRankOwned =
        playerGroup.getWeight().orElseThrow() >= rankGroup.getWeight().orElseThrow();

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
                                        economy.format(currentMoney)),
                                    economy.format(rankUpPrerequisite.getMoneyPrice())))
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
    return isRankOwned
        ? itemBuilder.asGuiItem()
        : itemBuilder.asGuiItem(onClick(rankInfo, isRankable));
  }

  public @NotNull GuiAction<InventoryClickEvent> onClick(
      @NotNull RankConfig.RankInfo rankInfo, boolean isRankable) {
    return event -> {
      Player player = (Player) event.getWhoClicked();
      if (isRankable) {
        luckPerms
            .getUserManager()
            .modifyUser(
                player.getUniqueId(),
                user -> {
                  Group group = luckPerms.getGroupManager().getGroup(rankInfo.getId());
                  user.data().add(InheritanceNode.builder(group).build());
                  user.setPrimaryGroup(rankInfo.getId());
                })
            .whenCompleteAsync(
                (log, exception) -> {
                  if (exception != null) {
                    logger.error("LuckPerms exception with Diagonia rankup system", exception);
                  }
                  player.sendMessage(
                      miniMessage
                          .deserialize(
                              String.format(
                                  resourceBundle.getString("diagonia.rankup.rankup.success"),
                                  rankInfo.getId()))
                          .decoration(TextDecoration.ITALIC, false)
                          .append(
                              Component.text(rankInfo.getName())
                                  .color(rankInfo.getColor())
                                  .decoration(TextDecoration.ITALIC, false)));
                  rankUpController.openRankListGui(player);
                },
                runnable -> bukkitScheduler.runTask(javaPlugin, runnable));
      } else {
        player.sendMessage(
            miniMessage
                .deserialize(
                    resourceBundle.getString(
                        "diagonia.rankup.rankup.cost.prerequisites_not_respected"))
                .decoration(TextDecoration.ITALIC, false));
      }
    };
  }
}
