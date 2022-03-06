package fr.voltariuss.diagonia.model.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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

  private final UserManager userManager;
  private final GroupManager groupManager;

  private Track track;

  /**
   * Constructor.
   *
   * @param userManager The user manager of LuckPerms API.
   * @param groupManager The group manager of LuckPerms API.
   * @param trackManager The track manager of LuckPerms API.
   */
  @Inject
  public RankServiceImpl(
      @NotNull UserManager userManager,
      @NotNull GroupManager groupManager,
      @NotNull TrackManager trackManager) {
    this.userManager = userManager;
    this.groupManager = groupManager;

    this.track = trackManager.getTrack(TRACK_NAME);
  }

  @Override
  public @NotNull Optional<Group> getCurrentRank(@NotNull Player player) {
    User user = Objects.requireNonNull(userManager.getUser(player.getUniqueId()));
    return user.getInheritedGroups(QueryOptions.nonContextual()).stream()
        .filter(track::containsGroup)
        .findFirst();
  }

  @Override
  public @Nullable Group getUnlockableRank(@NotNull Player player) {
    Group unlockableRank;

    Optional<Group> currentRank = getCurrentRank(player);

    if (currentRank.isPresent()) {
      String unlockableRankStr = track.getNext(currentRank.get());
      if (unlockableRankStr != null) {
        unlockableRank = groupManager.getGroup(unlockableRankStr);
      } else {
        unlockableRank = null;
      }
    } else {
      unlockableRank = getFirstRank();
    }

    return unlockableRank;
  }

  @Override
  public @NotNull List<Group> getOwnedRanks(@NotNull Player player) {
    return getOwnedRanks(getCurrentRank(player).orElse(null));
  }

  @Override
  public boolean isRankOwned(@NotNull Player player, @NotNull String rankName) {
    boolean isRankOwned = false;

    Optional<Group> currentRank = getCurrentRank(player);

    if (currentRank.isPresent()) {
      if (currentRank.get().getName().equals(rankName)) {
        isRankOwned = true;
      } else {
        List<Group> ownedRanks = getOwnedRanks(player);
        isRankOwned = ownedRanks.contains(groupManager.getGroup(rankName));
      }
    }

    return isRankOwned;
  }

  @Override
  public boolean isCurrentRank(@NotNull Player player, @NotNull String rankName) {
    Optional<Group> currentRank = getCurrentRank(player);
    return currentRank.isPresent() && currentRank.get().getName().equals(rankName);
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
