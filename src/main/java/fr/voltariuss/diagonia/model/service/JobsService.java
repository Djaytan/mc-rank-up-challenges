package fr.voltariuss.diagonia.model.service;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface JobsService {

  /**
   * Gets the total levels of all jobs exercised the specified player.
   *
   * @param player The player.
   * @return The total levels of all jobs exercised by the specified player.
   * @apiNote Jobs with levels when there are not actually exercised by the player are not taken
   *     into account.
   */
  int getTotalLevels(@NotNull Player player);
}
