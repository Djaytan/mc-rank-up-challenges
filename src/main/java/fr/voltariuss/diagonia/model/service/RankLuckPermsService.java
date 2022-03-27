package fr.voltariuss.diagonia.model.service;

import com.google.common.base.Preconditions;
import fr.voltariuss.diagonia.DiagoniaLogger;
import fr.voltariuss.diagonia.model.config.rank.Rank;
import fr.voltariuss.diagonia.model.config.rank.RankUpPrerequisites;
import fr.voltariuss.diagonia.model.dto.RankUpProgression;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
import net.luckperms.api.track.TrackManager;
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

  private static final String TRACK_NAME = "ranks"; // TODO: make it configurable

  private final DiagoniaLogger logger;
  private final GroupManager groupManager;
  private final RankChallengeProgressionService rankChallengeProgressionService;
  private final RankConfigService rankConfigService;
  private final UserManager userManager;

  private Track track;

  /**
   * Constructor.
   *
   * @param logger The logger.
   * @param groupManager The group manager of LuckPerms API.
   * @param rankChallengeProgressionService The rank challenge progression service.
   * @param rankConfigService The rank config service.
   * @param trackManager The track manager of LuckPerms API.
   * @param userManager The user manager of LuckPerms API.
   */
  @Inject
  public RankLuckPermsService(
      @NotNull DiagoniaLogger logger,
      @NotNull GroupManager groupManager,
      @NotNull RankChallengeProgressionService rankChallengeProgressionService,
      @NotNull RankConfigService rankConfigService,
      @NotNull TrackManager trackManager,
      @NotNull UserManager userManager) {
    this.logger = logger;
    this.groupManager = groupManager;
    this.rankChallengeProgressionService = rankChallengeProgressionService;
    this.rankConfigService = rankConfigService;
    this.userManager = userManager;

    this.track = trackManager.getTrack(TRACK_NAME);
  }

  @Override
  public @Nullable Group getCurrentRank(@NotNull Player player) {
    Preconditions.checkNotNull(player);

    User user = userManager.getUser(player.getUniqueId());
    Objects.requireNonNull(user);

    Group currentGroup =
        user.getInheritedGroups(QueryOptions.nonContextual()).stream()
            .filter(track::containsGroup)
            .findFirst()
            .orElse(null);

    if (currentGroup == null) {
      logger.debug("The player don't have any rank yet: player={}", player.getName());
      return null;
    }

    Rank currentRank = rankConfigService.findById(currentGroup.getName()).orElseThrow();

    logger.debug(
        "Current player rank: player={}, currentRank={}",
        player.getName(),
        currentRank != null ? currentRank.getName() : null);

    return currentGroup;
  }

  @Override
  public @Nullable Group getUnlockableRank(@NotNull Player player) {
    Group unlockableRank;

    Group currentRank = getCurrentRank(player);

    if (currentRank != null) {
      String unlockableRankStr = track.getNext(currentRank);
      unlockableRank = unlockableRankStr != null ? groupManager.getGroup(unlockableRankStr) : null;
    } else {
      unlockableRank = getFirstRank();
    }

    logger.debug(
        "Unlockable player rank: player={}, unlockableRank={}",
        player.getName(),
        unlockableRank != null ? unlockableRank.getName() : null);

    return unlockableRank;
  }

  @Override
  public @NotNull List<Group> getOwnedRanks(@NotNull Player player) {
    List<Group> ownedRanks = getOwnedRanks(getCurrentRank(player));

    logger.debug(
        "Owned player ranks: player={}, ownedRanks={}",
        player.getName(),
        ownedRanks.stream().map(Group::getName).toList());

    return ownedRanks;
  }

  @Override
  public boolean isRankOwned(@NotNull Player player, @NotNull String rankId) {
    boolean isRankOwned = false;

    Group currentRank = getCurrentRank(player);

    if (currentRank != null) {
      if (currentRank.getName().equals(rankId)) {
        isRankOwned = true;
      } else {
        List<Group> ownedRanks = getOwnedRanks(player);
        isRankOwned = ownedRanks.contains(groupManager.getGroup(rankId));
      }
    }

    logger.debug(
        "Is rank owned by player: player={}, rankId={}, isRankOwned={}",
        player.getName(),
        rankId,
        isRankOwned);

    return isRankOwned;
  }

  @Override
  public boolean isCurrentRank(@NotNull Player player, @NotNull String rankId) {
    Group currentRank = getCurrentRank(player);
    boolean isCurrentRank = currentRank != null && currentRank.getName().equals(rankId);

    logger.debug(
        "Is current player rank: player={}, rankId={}, isCurrentRank={}",
        player.getName(),
        rankId,
        isCurrentRank);

    return isCurrentRank;
  }

  @Override
  public boolean isUnlockableRank(@NotNull Player player, @NotNull String rankId) {
    Group unlockableRank = getUnlockableRank(player);
    boolean isUnlockableRank = unlockableRank != null && unlockableRank.getName().equals(rankId);

    logger.debug(
        "Is unlockable player rank: player={}, rankId={}, isUnlockableRank={}",
        player.getName(),
        rankId,
        isUnlockableRank);

    return isUnlockableRank;
  }

  @Override
  public @NotNull PromotionResult promote(@NotNull Player player) {
    User user = Objects.requireNonNull(userManager.getUser(player.getUniqueId()));
    return track.promote(user, ImmutableContextSet.empty());
    // TODO: manage status and raise errors if needed
  }

  @Override
  public @Nullable RankUpProgression getRankUpProgression(
      @NotNull Player player,
      @NotNull Rank targetedRank,
      int totalJobsLevels,
      double currentBalance) {
    Preconditions.checkArgument(
        totalJobsLevels >= 0, "The total jobs levels must be higher or equals to 0.");
    // TODO: what about economy?

    Rank unlockableRank = rankConfigService.findById(targetedRank.getId()).orElseThrow();
    RankUpPrerequisites prerequisites = unlockableRank.getRankUpPrerequisites();

    if (prerequisites == null) {
      throw new IllegalStateException(
          String.format(
              "Unlockable rank '%s' don't have any challenge to accomplish which is not allowed.",
              unlockableRank.getId()),
          new NullPointerException("Prerequisites list of unlockable rank is null."));
    }

    int currentXpLevel = player.getLevel();
    int requiredXpLevel = prerequisites.getTotalMcExpLevels();
    boolean isXpLevelPrerequisiteDone = currentXpLevel >= requiredXpLevel;

    int requiredTotalJobsLevel = prerequisites.getTotalJobsLevel();
    boolean isTotalJobsLevelsPrerequisiteDone = totalJobsLevels >= requiredTotalJobsLevel;

    double price = prerequisites.getMoneyPrice();
    boolean isMoneyPrerequisiteDone = currentBalance >= price;

    boolean isChallengesPrerequisiteDone =
        rankChallengeProgressionService.areChallengesCompleted(player, unlockableRank);

    boolean isRankOwned = isRankOwned(player, unlockableRank.getId());

    return RankUpProgression.builder()
        .currentXpLevel(currentXpLevel)
        .isXpLevelPrerequisiteDone(isXpLevelPrerequisiteDone)
        .totalJobsLevels(totalJobsLevels)
        .isTotalJobsLevelsPrerequisiteDone(isTotalJobsLevelsPrerequisiteDone)
        .currentBalance(currentBalance)
        .isMoneyPrerequisiteDone(isMoneyPrerequisiteDone)
        .isChallengesPrerequisiteDone(isChallengesPrerequisiteDone)
        .isRankOwned(isRankOwned)
        .build();
  }

  private @NotNull List<Group> getOwnedRanks(@Nullable Group currentRank) {
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

  private @NotNull Group getFirstRank() {
    return Objects.requireNonNull(groupManager.getGroup(track.getGroups().get(0)));
  }
}
