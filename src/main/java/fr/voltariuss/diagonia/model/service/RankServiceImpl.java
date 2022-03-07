package fr.voltariuss.diagonia.model.service;

import fr.voltariuss.diagonia.Debugger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.group.GroupManager;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.query.QueryOptions;
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
public class RankServiceImpl implements RankService {

  private static final String TRACK_NAME = "ranks"; // TODO: make it configurable

  private final Debugger debugger;
  private final GroupManager groupManager;
  private final UserManager userManager;

  private Track track;

  /**
   * Constructor.
   *
   * @param debugger The debugger logger.
   * @param groupManager The group manager of LuckPerms API.
   * @param userManager The user manager of LuckPerms API.
   * @param trackManager The track manager of LuckPerms API.
   */
  @Inject
  public RankServiceImpl(
      @NotNull Debugger debugger,
      @NotNull GroupManager groupManager,
      @NotNull UserManager userManager,
      @NotNull TrackManager trackManager) {
    this.debugger = debugger;
    this.groupManager = groupManager;
    this.userManager = userManager;

    this.track = trackManager.getTrack(TRACK_NAME);
  }

  @Override
  public @Nullable Group getCurrentRank(@NotNull Player player) {
    User user = Objects.requireNonNull(userManager.getUser(player.getUniqueId()));

    Group currentRank =
        user.getInheritedGroups(QueryOptions.nonContextual()).stream()
            .filter(track::containsGroup)
            .findFirst()
            .orElse(null);

    debugger.debug(
        "Current player rank: player={}, currentRank={}",
        player.getName(),
        currentRank != null ? currentRank.getName() : null);

    return currentRank;
  }

  @Override
  public @Nullable Group getUnlockableRank(@NotNull Player player) {
    Group unlockableRank;

    Group currentRank = getCurrentRank(player);

    if (currentRank != null) {
      String unlockableRankStr = track.getNext(currentRank);
      if (unlockableRankStr != null) {
        unlockableRank = groupManager.getGroup(unlockableRankStr);
      } else {
        unlockableRank = null;
      }
    } else {
      unlockableRank = getFirstRank();
    }

    debugger.debug(
        "Unlockable player rank: player={}, unlockableRank={}",
        player.getName(),
        unlockableRank != null ? unlockableRank.getName() : null);

    return unlockableRank;
  }

  @Override
  public @NotNull List<Group> getOwnedRanks(@NotNull Player player) {
    List<Group> ownedRanks = getOwnedRanks(getCurrentRank(player));

    debugger.debug(
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

    debugger.debug(
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

    debugger.debug(
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

    debugger.debug(
        "Is unlockable player rank: player={}, rankId={}, isUnlockableRank={}",
        player.getName(),
        rankId,
        isUnlockableRank);

    return isUnlockableRank;
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
