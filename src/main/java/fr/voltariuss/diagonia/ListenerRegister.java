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

package fr.voltariuss.diagonia;

import fr.voltariuss.diagonia.listeners.EnchantItemListener;
import fr.voltariuss.diagonia.listeners.EntityShootBowListener;
import fr.voltariuss.diagonia.listeners.InventoryClickListener;
import fr.voltariuss.diagonia.listeners.LootGenerateListener;
import fr.voltariuss.diagonia.listeners.PlayerItemMendListener;
import fr.voltariuss.diagonia.listeners.PrepareAnvilListener;
import fr.voltariuss.diagonia.listeners.PrepareItemEnchantListener;
import javax.inject.Inject;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class ListenerRegister {

  private final JavaPlugin plugin;
  private final PluginManager pluginManager;

  private final EnchantItemListener enchantItemListener;
  private final EntityShootBowListener entityShootBowListener;
  private final InventoryClickListener inventoryClickListener;
  private final LootGenerateListener lootGenerateListener;
  private final PlayerItemMendListener playerItemMendListener;
  private final PrepareAnvilListener prepareAnvilListener;
  private final PrepareItemEnchantListener prepareItemEnchantListener;

  @Inject
  public ListenerRegister(
      @NotNull JavaPlugin plugin,
      @NotNull PluginManager pluginManager,
      @NotNull EnchantItemListener enchantItemListener,
      @NotNull EntityShootBowListener entityShootBowListener,
      @NotNull InventoryClickListener inventoryClickListener,
      @NotNull LootGenerateListener lootGenerateListener,
      @NotNull PlayerItemMendListener playerItemMendListener,
      @NotNull PrepareAnvilListener prepareAnvilListener,
      @NotNull PrepareItemEnchantListener prepareItemEnchantListener) {
    this.plugin = plugin;
    this.pluginManager = pluginManager;
    this.enchantItemListener = enchantItemListener;
    this.entityShootBowListener = entityShootBowListener;
    this.inventoryClickListener = inventoryClickListener;
    this.lootGenerateListener = lootGenerateListener;
    this.playerItemMendListener = playerItemMendListener;
    this.prepareAnvilListener = prepareAnvilListener;
    this.prepareItemEnchantListener = prepareItemEnchantListener;
  }

  public void registerListeners() {
    pluginManager.registerEvents(enchantItemListener, plugin);
    pluginManager.registerEvents(entityShootBowListener, plugin);
    pluginManager.registerEvents(inventoryClickListener, plugin);
    pluginManager.registerEvents(lootGenerateListener, plugin);
    pluginManager.registerEvents(playerItemMendListener, plugin);
    pluginManager.registerEvents(prepareAnvilListener, plugin);
    pluginManager.registerEvents(prepareItemEnchantListener, plugin);
  }
}
