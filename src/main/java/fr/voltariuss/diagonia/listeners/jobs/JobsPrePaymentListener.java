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

package fr.voltariuss.diagonia.listeners.jobs;

import com.gamingmesh.jobs.api.JobsPrePaymentEvent;
import fr.voltariuss.diagonia.controller.JobsController;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

@Singleton
public class JobsPrePaymentListener implements Listener {

  private final JobsController jobsController;

  @Inject
  public JobsPrePaymentListener(@NotNull JobsController jobsController) {
    this.jobsController = jobsController;
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onJobsPayment(@NotNull JobsPrePaymentEvent event) {
    if (jobsController.isPlaceAndBreakAction(event.getActionInfo().getType(), event.getBlock())) {
      event.setCancelled(true);
    }
  }
}
