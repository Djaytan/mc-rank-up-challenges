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

package fr.voltariuss.diagonia.model.service;

import com.google.common.base.Preconditions;
import fr.voltariuss.diagonia.RemakeBukkitLogger;
import fr.voltariuss.diagonia.model.config.Rank;
import fr.voltariuss.diagonia.model.config.RankUpPrerequisites;
import fr.voltariuss.diagonia.model.dto.RankUpProgression;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.luckperms.api.context.ImmutableContextSet;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.group.GroupManager;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.query.QueryOptions;
import net.luckperms.api.track.PromotionResult;
import net.luckperms.api.track.Track;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The rank service implementation class which use the LuckPerms API.
 *
 * @author Voltariuss
 * @since 1.1.0
 */
@Singleton
public class RankLuckPermsService implements RankService {

  private final RemakeBukkitLogger logger;
  private final GroupManager groupManager;
  private final RankChallengeProgressionService rankChallengeProgressionService;
  private final RankConfigService rankConfigService;
  private final Track track;
  private final UserManager userManager;

  /**
   * Constructor.
   *
   * @param logger The logger.
   * @param groupManager The group manager of LuckPerms API.
   * @param rankChallengeProgressionService The rank challenge progression service.
   * @param rankConfigService The rank config service.
   * @param track The LuckPerms' track for ranks.
   * @param userManager The user manager of LuckPerms API.
   */
  @Inject
  public RankLuckPermsService(
      @NotNull RemakeBukkitLogger logger,
      @NotNull GroupManager groupManager,
      @NotNull RankChallengeProgressionService rankChallengeProgressionService,
      @NotNull RankConfigService rankConfigService,
      @NotNull Track track,
      @NotNull UserManager userManager) {
    this.logger = logger;
    this.groupManager = groupManager;
    this.rankChallengeProgressionService = rankChallengeProgressionService;
    this.rankConfigService = rankConfigService;
    this.track = track;
    this.userManager = userManager;
  }

  @Override
  public @Nullable Rank getCurrentRank(@NotNull Player player) {
    Preconditions.checkNotNull(player);

    Group currentGroup = getCurrentGroup(player);

    if (currentGroup == null) {
      logger.debug("The player don't have any rank yet: playerName={}", player.getName());
      return null;
    }

    Optional<Rank> currentRank = rankConfigService.findById(currentGroup.getName());

    if (currentRank.isEmpty()) {
      throw new IllegalStateException(
          String.format(
              "Failed to found the rank '%1$s' from configurations: make sure that all defined"
                  + " ranks in configs are well bound with LuckPerms' track ranks.",
              currentGroup.getName()));
    }

    logger.debug(
        "Current player rank: playerName={}, currentRankId={}",
        player.getName(),
        currentRank.get().getId());

    return currentRank.get();
  }

  @Override
  public @Nullable Rank getUnlockableRank(@NotNull Player player) {
    Rank unlockableRank;
    Group currentGroup = getCurrentGroup(player);

    if (currentGroup == null) {
      unlockableRank = rankConfigService.findById(getFirstGroup().getName()).orElseThrow();
    } else {
      String unlockableGroupId = track.getNext(currentGroup);

      unlockableRank =
          unlockableGroupId != null
              ? rankConfigService.findById(unlockableGroupId).orElseThrow()
              : null;
    }

    logger.debug(
        "Unlockable player group: playerName={}, unlockableGroupId={}",
        player.getName(),
        unlockableRank != null ? unlockableRank.getId() : null);

    return unlockableRank;
  }

  @Override
  public @NotNull List<Rank> getOwnedRanks(@NotNull Player player) {
    List<Group> ownedGroups = getOwnedGroups(getCurrentGroup(player));

    logger.debug(
        "Owned player groups: playerName={}, ownedGroupsNames={}",
        player.getName(),
        ownedGroups.stream().map(Group::getName).toList());

    return ownedGroups.stream()
        .map(Group::getName)
        .map(rankConfigService::findById)
        .map(Optional::orElseThrow)
        .toList();
  }

  @Override
  public boolean isRankOwned(@NotNull Player player, @NotNull String rankId) {
    List<Group> ownedGroups = getOwnedGroups(getCurrentGroup(player));

    boolean isRankOwned = ownedGroups.contains(groupManager.getGroup(rankId));

    logger.debug(
        "Ranks owned by a player: playerName={}, rankId={}, isRankOwned={}",
        player.getName(),
        rankId,
        isRankOwned);

    return isRankOwned;
  }

  @Override
  public boolean isCurrentRank(@NotNull Player player, @NotNull String rankId) {
    Group currentGroup = getCurrentGroup(player);
    boolean isCurrentRank = currentGroup != null && currentGroup.getName().equals(rankId);

    logger.debug(
        "Is current player rank: player={}, rankId={}, isCurrentRank={}",
        player.getName(),
        rankId,
        isCurrentRank);

    return isCurrentRank;
  }

  @Override
  public boolean isUnlockableRank(@NotNull Player player, @NotNull String rankId) {
    Rank unlockableRank = getUnlockableRank(player);
    boolean isUnlockableRank = unlockableRank != null && unlockableRank.getId().equals(rankId);

    logger.debug(
        "Is unlockable player rank: player={}, rankId={}, isUnlockableRank={}",
        player.getName(),
        rankId,
        isUnlockableRank);

    return isUnlockableRank;
  }

  @Override
  public @NotNull PromotionResult promote(@NotNull Player player) {
    User user = getUser(player);
    PromotionResult promotionResult = track.promote(user, ImmutableContextSet.empty());
    userManager.saveUser(user);
    return promotionResult;
  }

  @Override
  public @NotNull RankUpProgression getRankUpProgression(
      @NotNull Player player,
      @NotNull Rank targetedRank,
      int totalJobsLevels,
      double currentBalance) {
    Preconditions.checkArgument(
        totalJobsLevels >= 0, "The total jobs levels must be higher or equals to 0.");

    Rank unlockableRank =
        rankConfigService
            .findById(targetedRank.getId())
            .orElseThrow(
                () ->
                    new IllegalStateException(
                        "The specified rank ID isn't associated with any registered rank in"
                            + " configuration."));

    RankUpPrerequisites prerequisites = unlockableRank.getRankUpPrerequisites();

    if (prerequisites == null) {
      throw new IllegalStateException(
          String.format(
              "Unlockable rank '%s' don't have any challenge to accomplish which is not allowed.",
              unlockableRank.getId()),
          new NullPointerException("Prerequisites list of unlockable rank is null."));
    }

    int currentEnchantingLevels = player.getLevel();
    int requiredEnchantingLevels = prerequisites.getEnchantingLevelsCost();
    boolean isEnchantingLevelsPrerequisiteDone =
        currentEnchantingLevels >= requiredEnchantingLevels;

    int requiredJobsLevels = prerequisites.getJobsLevels();
    boolean isJobsLevelsPrerequisiteDone = totalJobsLevels >= requiredJobsLevels;

    double moneyCost = prerequisites.getMoneyCost();
    boolean isMoneyPrerequisiteDone = currentBalance >= moneyCost;

    boolean isChallengesPrerequisiteDone =
        rankChallengeProgressionService.areChallengesCompleted(player, unlockableRank);

    boolean isRankOwned = isRankOwned(player, unlockableRank.getId());

    return RankUpProgression.builder()
        .currentXpLevel(currentEnchantingLevels)
        .isXpLevelPrerequisiteDone(isEnchantingLevelsPrerequisiteDone)
        .totalJobsLevels(totalJobsLevels)
        .isTotalJobsLevelsPrerequisiteDone(isJobsLevelsPrerequisiteDone)
        .currentBalance(currentBalance)
        .isMoneyPrerequisiteDone(isMoneyPrerequisiteDone)
        .isChallengesPrerequisiteDone(isChallengesPrerequisiteDone)
        .isRankOwned(isRankOwned)
        .build();
  }

  private @NotNull User getUser(@NotNull Player player) {
    User user = userManager.getUser(player.getUniqueId());

    if (user == null) {
      throw new IllegalStateException(
          "Failed to found LuckPerms' user from an online player. This isn't supposed to happen.");
    }

    return user;
  }

  private @Nullable Group getCurrentGroup(@NotNull Player player) {
    User user = getUser(player);

    // A user have only one group of the track at once: the current rank of the player
    // (even if it can have staff groups or other ones which will be ignored tho)
    return user.getInheritedGroups(QueryOptions.nonContextual()).stream()
        .filter(track::containsGroup)
        .findFirst()
        .orElse(null);
  }

  private @NotNull List<Group> getOwnedGroups(@Nullable Group currentRank) {
    List<Group> ownedGroups = new ArrayList<>();

    Group currentGroup = currentRank;

    while (currentGroup != null) {
      ownedGroups.add(currentGroup);
      String prevGroupStr = track.getPrevious(currentGroup);
      currentGroup =
          prevGroupStr != null ? Objects.requireNonNull(groupManager.getGroup(prevGroupStr)) : null;
    }

    return ownedGroups;
  }

  private @NotNull Group getFirstGroup() {
    return Objects.requireNonNull(groupManager.getGroup(track.getGroups().get(0)));
  }
}
