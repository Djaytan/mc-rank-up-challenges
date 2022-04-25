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

package fr.voltariuss.diagonia.view.message;

import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.NotNull;

@Singleton
public class EnchantsMessage {

  private final MiniMessage miniMessage;
  private final ResourceBundle resourceBundle;

  @Inject
  public EnchantsMessage(@NotNull MiniMessage miniMessage, @NotNull ResourceBundle resourceBundle) {
    this.miniMessage = miniMessage;
    this.resourceBundle = resourceBundle;
  }

  public @NotNull Component removedBlacklistedEnchants(
      @NotNull Set<Enchantment> removedBlacklistedEnchants) {
    if (removedBlacklistedEnchants.size() == 1) {
      Enchantment removedBlacklistedEnchantment = removedBlacklistedEnchants.iterator().next();

      return miniMessage.deserialize(
          resourceBundle.getString("diagonia.enchants.message.removed_enchant"),
          TagResolver.resolver(
              Placeholder.component(
                  "diag_enchant", Component.translatable(removedBlacklistedEnchantment))));
    }

    return miniMessage.deserialize(
        resourceBundle.getString("diagonia.enchants.message.removed_enchants"),
        TagResolver.resolver(
            Placeholder.component(
                "diag_enchants",
                Component.join(
                    JoinConfiguration.separators(
                        miniMessage.deserialize(
                            resourceBundle.getString("diagonia.common.separator.list")),
                        miniMessage.deserialize(
                            resourceBundle.getString("diagonia.common.separator.list.last"))),
                    removedBlacklistedEnchants.stream()
                        .map(Component::translatable)
                        .collect(Collectors.toSet())))));
  }
}
