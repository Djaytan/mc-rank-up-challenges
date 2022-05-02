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

package fr.voltariuss.diagonia.controller;

import fr.voltariuss.diagonia.controller.listener.bukkit.BlockPlaceListener;
import fr.voltariuss.diagonia.controller.listener.bukkit.EnchantItemListener;
import fr.voltariuss.diagonia.controller.listener.bukkit.EntityDeathListener;
import fr.voltariuss.diagonia.controller.listener.bukkit.EntityShootBowListener;
import fr.voltariuss.diagonia.controller.listener.bukkit.LootGenerateListener;
import fr.voltariuss.diagonia.controller.listener.bukkit.PiglinBarterListener;
import fr.voltariuss.diagonia.controller.listener.bukkit.PlayerFishListener;
import fr.voltariuss.diagonia.controller.listener.bukkit.PlayerItemMendListener;
import fr.voltariuss.diagonia.controller.listener.bukkit.PrepareAnvilListener;
import fr.voltariuss.diagonia.controller.listener.bukkit.PrepareItemEnchantListener;
import fr.voltariuss.diagonia.controller.listener.bukkit.VillagerAcquireTradeListener;
import fr.voltariuss.diagonia.controller.listener.jobs.JobsExpGainListener;
import fr.voltariuss.diagonia.controller.listener.jobs.JobsPrePaymentListener;
import javax.inject.Inject;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;

public class ListenerRegister {

  private final Plugin plugin;
  private final PluginManager pluginManager;

  private final BlockPlaceListener blockPlaceListener;
  private final EnchantItemListener enchantItemListener;
  private final EntityDeathListener entityDeathListener;
  private final EntityShootBowListener entityShootBowListener;
  private final JobsExpGainListener jobsExpGainListener;
  private final JobsPrePaymentListener jobsPrePaymentListener;
  private final LootGenerateListener lootGenerateListener;
  private final PiglinBarterListener piglinBarterListener;
  private final PlayerFishListener playerFishListener;
  private final PlayerItemMendListener playerItemMendListener;
  private final PrepareAnvilListener prepareAnvilListener;
  private final PrepareItemEnchantListener prepareItemEnchantListener;
  private final VillagerAcquireTradeListener villagerAcquireTradeListener;

  @Inject
  public ListenerRegister(
      @NotNull Plugin plugin,
      @NotNull PluginManager pluginManager,
      @NotNull BlockPlaceListener blockPlaceListener,
      @NotNull EnchantItemListener enchantItemListener,
      @NotNull EntityDeathListener entityDeathListener,
      @NotNull EntityShootBowListener entityShootBowListener,
      @NotNull JobsExpGainListener jobsExpGainListener,
      @NotNull JobsPrePaymentListener jobsPrePaymentListener,
      @NotNull LootGenerateListener lootGenerateListener,
      @NotNull PiglinBarterListener piglinBarterListener,
      @NotNull PlayerFishListener playerFishListener,
      @NotNull PlayerItemMendListener playerItemMendListener,
      @NotNull PrepareAnvilListener prepareAnvilListener,
      @NotNull PrepareItemEnchantListener prepareItemEnchantListener,
      @NotNull VillagerAcquireTradeListener villagerAcquireTradeListener) {
    this.plugin = plugin;
    this.pluginManager = pluginManager;

    this.blockPlaceListener = blockPlaceListener;
    this.enchantItemListener = enchantItemListener;
    this.entityDeathListener = entityDeathListener;
    this.entityShootBowListener = entityShootBowListener;
    this.jobsExpGainListener = jobsExpGainListener;
    this.jobsPrePaymentListener = jobsPrePaymentListener;
    this.lootGenerateListener = lootGenerateListener;
    this.piglinBarterListener = piglinBarterListener;
    this.playerFishListener = playerFishListener;
    this.playerItemMendListener = playerItemMendListener;
    this.prepareAnvilListener = prepareAnvilListener;
    this.prepareItemEnchantListener = prepareItemEnchantListener;
    this.villagerAcquireTradeListener = villagerAcquireTradeListener;
  }

  public void registerListeners() {
    pluginManager.registerEvents(blockPlaceListener, plugin);
    pluginManager.registerEvents(enchantItemListener, plugin);
    pluginManager.registerEvents(entityDeathListener, plugin);
    pluginManager.registerEvents(entityShootBowListener, plugin);
    pluginManager.registerEvents(jobsExpGainListener, plugin);
    pluginManager.registerEvents(jobsPrePaymentListener, plugin);
    pluginManager.registerEvents(lootGenerateListener, plugin);
    pluginManager.registerEvents(piglinBarterListener, plugin);
    pluginManager.registerEvents(playerFishListener, plugin);
    pluginManager.registerEvents(playerItemMendListener, plugin);
    pluginManager.registerEvents(prepareAnvilListener, plugin);
    pluginManager.registerEvents(prepareItemEnchantListener, plugin);
    pluginManager.registerEvents(villagerAcquireTradeListener, plugin);
  }
}
