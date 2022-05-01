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

package fr.voltariuss.diagonia.model.config.serializers;

import javax.inject.Inject;
import javax.inject.Singleton;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

@Singleton
public class BukkitConfigSerializers {

  private final EnchantmentConfigSerializer enchantmentConfigSerializer;
  private final MaterialConfigSerializer materialConfigSerializer;

  @Inject
  public BukkitConfigSerializers(
      @NotNull EnchantmentConfigSerializer enchantmentConfigSerializer,
      @NotNull MaterialConfigSerializer materialConfigSerializer) {
    this.enchantmentConfigSerializer = enchantmentConfigSerializer;
    this.materialConfigSerializer = materialConfigSerializer;
  }

  public @NotNull TypeSerializerCollection collection() {
    return TypeSerializerCollection.builder()
        .registerExact(Enchantment.class, enchantmentConfigSerializer)
        .registerExact(Material.class, materialConfigSerializer)
        .build();
  }
}
