package fr.voltariuss.diagonia.model.service;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.JobsPlayer;
import javax.inject.Singleton;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Singleton
public class JobsRebornService implements JobsService {

  @Override
  public int getTotalLevels(@NotNull Player player) {
    JobsPlayer jobsPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
    return jobsPlayer.getTotalLevels();
  }
}
