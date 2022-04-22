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

package fr.voltariuss.diagonia.utils;

import java.util.ResourceBundle;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

@Singleton
public class PredefinedItem {

  private final MiniMessage miniMessage;
  private final ResourceBundle resourceBundle;

  @Inject
  public PredefinedItem(@NotNull MiniMessage miniMessage, @NotNull ResourceBundle resourceBundle) {
    this.miniMessage = miniMessage;
    this.resourceBundle = resourceBundle;
  }

  public @NotNull ItemStack resultDeactivated() {
    ItemStack itemStack = new ItemStack(Material.BARRIER, 1);
    ItemMeta itemMeta = itemStack.getItemMeta();
    itemMeta.displayName(
        miniMessage
            .deserialize(resourceBundle.getString("diagonia.minecraft.item.result_deactivated"))
            .decoration(TextDecoration.ITALIC, false));
    itemStack.setItemMeta(itemMeta);
    return itemStack;
  }
}
