package fr.voltariuss.diagonia.model.service;

import net.luckperms.api.model.group.Group;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Rank service interface.
 *
 * @author Voltariuss
 */
// TODO: refactor - make used objects more generic that those of LuckPerms
public interface RankService {

  /**
   * Recovers the current rank of the given player.
   *
   * <p>A newbie player may not have rank.
   *
   * @param player The player.
   * @return The current rank of the specified player.
   */
  @Nullable
  Group getCurrentRank(@NotNull Player player);

  /**
   * Gets the unlockable rank if the player doesn't have the last rank yet. Otherwise, the returned
   * value will be null.
   *
   * @param player The player.
   * @return The unlockable rank if the player doesn't have the last rank yet. Otherwise, the
   *     returned value will be null.
   */
  @Nullable
  Group getUnlockableRank(@NotNull Player player);

  /**
   * Recovers the owned ranks of the given player.
   *
   * <p>A newbie player may not have rank.
   *
   * @param player The player.
   * @return The owned ranks of the specified player.
   */
  @NotNull
  List<Group> getOwnedRanks(@NotNull Player player);

  /**
   * Checks if the specified rank correspond to the current one of the given player.
   *
   * @param player The player.
   * @param rankId The rank's ID.
   * @return "true" if the specified rank correspond to the current one of the given player, "false"
   *     otherwise.
   */
  boolean isCurrentRank(@NotNull Player player, @NotNull String rankId);

  /**
   * Checks if the specified rank correspond to the unlockable one for the given player.
   *
   * @param player The player.
   * @param rankId The rank's ID.
   * @return "true" if the specified rank correspond to the unlockable one for the given player,
   *     "false" otherwise.
   */
  boolean isUnlockableRank(@NotNull Player player, @NotNull String rankId);

  /**
   * Checks if the specified rank is owned by the given player.
   *
   * @param player The player.
   * @param rankId The rank's ID.
   * @return "true" if the specified rank is owned by the given player, "false" otherwise.
   */
  boolean isRankOwned(@NotNull Player player, @NotNull String rankId);
}
