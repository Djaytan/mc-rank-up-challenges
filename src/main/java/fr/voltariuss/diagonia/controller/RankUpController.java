package fr.voltariuss.diagonia.controller;

import com.google.common.base.Preconditions;
import fr.voltariuss.diagonia.model.config.RankConfig;
import fr.voltariuss.diagonia.model.entity.RankChallengeProgression;
import fr.voltariuss.diagonia.model.service.RankChallengeProgressionService;
import fr.voltariuss.diagonia.view.gui.RankListGui;
import fr.voltariuss.diagonia.view.gui.RankUpGui;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import net.luckperms.api.LuckPerms;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

@Singleton
public class RankUpController {

  private final Logger logger;
  private final LuckPerms luckPerms;
  private final RankChallengeProgressionService rankChallengeProgressionService;

  private final Provider<RankListGui> rankListGuiProvider;
  private final Provider<RankUpGui> rankUpGuiProvider;

  @Inject
  public RankUpController(
      @NotNull Logger logger,
      @NotNull LuckPerms luckPerms,
      @NotNull RankChallengeProgressionService rankChallengeProgressionService,
      @NotNull Provider<RankListGui> rankListGuiProvider,
      @NotNull Provider<RankUpGui> rankUpGuiProvider) {
    this.logger = logger;
    this.luckPerms = luckPerms;
    this.rankChallengeProgressionService = rankChallengeProgressionService;
    this.rankListGuiProvider = rankListGuiProvider;
    this.rankUpGuiProvider = rankUpGuiProvider;
  }

  public void openRankListGui(@NotNull Player whoOpen) {
    logger.info("Open the list of ranks GUI for player {}", whoOpen.getName());
    rankListGuiProvider.get().open(whoOpen);
  }

  public void openRankUpGui(@NotNull Player whoOpen, @NotNull RankConfig.RankInfo rankInfo) {
    logger.info("Open the rank up GUI {} for player {}", rankInfo.getId(), whoOpen.getName());
    rankUpGuiProvider.get().open(whoOpen, rankInfo);
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

  public boolean isRankable(@NotNull Player targetPlayer, @NotNull RankConfig.RankInfo rankInfo) {
    Preconditions.checkNotNull(rankInfo.getRankUpChallenges());
    List<RankChallengeProgression> playerProgression =
        getRankUpProgression(targetPlayer, rankInfo.getId()).stream()
            .filter(
                rcp ->
                    rankInfo.getRankUpChallenges().stream()
                        .map(RankConfig.RankChallenge::getChallengeItemMaterial)
                        .toList()
                        .contains(rcp.getChallengeMaterial()))
            .toList();
    boolean isRankable = true;

    for (RankConfig.RankChallenge rankChallenge : rankInfo.getRankUpChallenges()) {
      int amount =
        playerProgression.stream()
          .filter(
            pp ->
              rankChallenge
                .getChallengeItemMaterial()
                .equals(pp.getChallengeMaterial()))
          .mapToInt(RankChallengeProgression::getChallengeAmountGiven)
          .sum();
      if (amount < rankChallenge.getChallengeItemAmount()) {
        isRankable = false;
        break;
      }
    }

    return isRankable;
  }
}
