package fr.voltariuss.diagonia.controller;

import fr.voltariuss.diagonia.DiagoniaLogger;
import fr.voltariuss.diagonia.model.config.rank.Rank;
import fr.voltariuss.diagonia.model.dto.RankUpProgression;
import fr.voltariuss.diagonia.model.entity.RankChallengeProgression;
import fr.voltariuss.diagonia.model.service.EconomyService;
import fr.voltariuss.diagonia.model.service.JobsService;
import fr.voltariuss.diagonia.model.service.RankChallengeProgressionService;
import fr.voltariuss.diagonia.model.service.RankConfigService;
import fr.voltariuss.diagonia.model.service.RankService;
import fr.voltariuss.diagonia.view.gui.RankListGui;
import fr.voltariuss.diagonia.view.gui.RankUpGui;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.UUID;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.luckperms.api.track.PromotionResult;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Singleton
public class RankUpController {

  private final DiagoniaLogger logger;
  private final EconomyService economyService;
  private final JobsService jobsService;
  private final MiniMessage miniMessage;
  private final RankChallengeProgressionService rankChallengeProgressionService;
  private final RankConfigService rankConfigService;
  private final RankService rankService;
  private final ResourceBundle resourceBundle;

  private final Provider<RankListGui> rankListGuiProvider;
  private final Provider<RankUpGui> rankUpGuiProvider;

  @Inject
  public RankUpController(
      @NotNull EconomyService economyService,
      @NotNull JobsService jobsService,
      @NotNull DiagoniaLogger logger,
      @NotNull MiniMessage miniMessage,
      @NotNull RankChallengeProgressionService rankChallengeProgressionService,
      @NotNull RankConfigService rankConfigService,
      @NotNull RankService rankService,
      @NotNull ResourceBundle resourceBundle,
      @NotNull Provider<RankListGui> rankListGuiProvider,
      @NotNull Provider<RankUpGui> rankUpGuiProvider) {
    this.economyService = economyService;
    this.jobsService = jobsService;
    this.logger = logger;
    this.miniMessage = miniMessage;
    this.rankChallengeProgressionService = rankChallengeProgressionService;
    this.rankConfigService = rankConfigService;
    this.rankService = rankService;
    this.resourceBundle = resourceBundle;
    this.rankListGuiProvider = rankListGuiProvider;
    this.rankUpGuiProvider = rankUpGuiProvider;
  }

  public void openRankListGui(@NotNull Player whoOpen) {
    logger.info("Open the list of ranks GUI for player {}", whoOpen.getName());
    rankListGuiProvider.get().open(whoOpen);
  }

  public void openRankUpGui(@NotNull Player whoOpen, @NotNull Rank rank) {
    logger.info("Open the rank up GUI {} for player {}", rank.getId(), whoOpen.getName());
    int totalJobsLevels = jobsService.getTotalLevels(whoOpen);
    double currentBalance = economyService.getBalance(whoOpen);
    RankUpProgression rankUpProgression =
        rankService.getRankUpProgression(whoOpen, rank, totalJobsLevels, currentBalance);
    Objects.requireNonNull(rankUpProgression);
    // TODO: what about if rank isn't properly defined? (e.g. an unknown ID rank because of
    // a wrong call of the developer or a bad configuration of LuckPerms by server admin)
    rankUpGuiProvider.get().open(whoOpen, rank, rankUpProgression);
  }

  public int giveItemChallenge(
      @NotNull Player targetPlayer,
      @NotNull String rankId,
      @NotNull Material material,
      int givenAmount) {
    return rankChallengeProgressionService.giveItemChallenge(
        targetPlayer.getUniqueId(), rankId, material, givenAmount);
  }

  public @NotNull Optional<RankChallengeProgression> findChallenge(
      @NotNull UUID playerUuid, @NotNull String rankId, @NotNull Material material) {
    return rankChallengeProgressionService.find(playerUuid, rankId, material);
  }

  public @NotNull List<RankChallengeProgression> getRankUpProgression(
      @NotNull Player targetPlayer, @NotNull String rankId) {
    return rankChallengeProgressionService.find(targetPlayer.getUniqueId(), rankId);
  }

  // TODO: remove these three following methods
  public boolean isRankOwned(@NotNull Player player, @NotNull String rankId) {
    return rankService.isRankOwned(player, rankId);
  }

  public boolean isCurrentRank(@NotNull Player player, @NotNull String rankId) {
    return rankService.isCurrentRank(player, rankId);
  }

  public boolean isUnlockableRank(@NotNull Player player, @NotNull String rankId) {
    return rankService.isUnlockableRank(player, rankId);
  }

  public void onRankUpRequested(
      @NotNull Player player, @NotNull RankUpProgression rankUpProgression) {
    // TODO: move message to view
    if (rankUpProgression.canRankUp()) {
      PromotionResult promotionResult = rankService.promote(player);

      if (!promotionResult.wasSuccessful()) {
        player.sendMessage(
            miniMessage
                .deserialize(resourceBundle.getString("diagonia.rankup.rankup.failure"))
                .decoration(TextDecoration.ITALIC, false));
      }

      Rank newRank =
          rankConfigService.findById(promotionResult.getGroupTo().orElseThrow()).orElseThrow();

      player.sendMessage(
          miniMessage
              .deserialize(
                  String.format(
                      resourceBundle.getString("diagonia.rankup.rankup.success"), newRank.getId()))
              .decoration(TextDecoration.ITALIC, false)
              .append(
                  Component.text(newRank.getName())
                      .color(newRank.getColor())
                      .decoration(TextDecoration.ITALIC, false)));
    } else {
      player.sendMessage(
          miniMessage
              .deserialize(
                  resourceBundle.getString(
                      "diagonia.rankup.rankup.cost.prerequisites_not_respected"))
              .decoration(TextDecoration.ITALIC, false));
    }
    openRankListGui(player);
  }
}
