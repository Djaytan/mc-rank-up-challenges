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

import fr.voltariuss.diagonia.model.dto.LocationDto;
import fr.voltariuss.diagonia.model.dto.response.EconomyResponse;
import fr.voltariuss.diagonia.view.EconomyFormatter;
import java.util.ResourceBundle;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;
import net.kyori.adventure.text.minimessage.template.TemplateResolver;
import org.jetbrains.annotations.NotNull;

@Singleton
public class PlayerShopMessage {

  private final EconomyFormatter economyFormatter;
  private final MiniMessage miniMessage;
  private final ResourceBundle resourceBundle;

  @Inject
  public PlayerShopMessage(
      @NotNull EconomyFormatter economyFormatter,
      @NotNull MiniMessage miniMessage,
      @NotNull ResourceBundle resourceBundle) {
    this.economyFormatter = economyFormatter;
    this.miniMessage = miniMessage;
    this.resourceBundle = resourceBundle;
  }

  public @NotNull Component buySuccess(@NotNull EconomyResponse economyResponse) {
    return miniMessage.deserialize(
        resourceBundle.getString("diagonia.playershop.buy.successfully_bought"),
        TemplateResolver.templates(
            Template.template(
                "diag_price", economyFormatter.format(economyResponse.getModifiedAmount())),
            Template.template(
                "diag_new_balance", economyFormatter.format(economyResponse.getNewBalance()))));
  }

  public @NotNull Component teleportPointDefined(@NotNull LocationDto locationDto) {
    return miniMessage.deserialize(
        resourceBundle.getString("diagonia.playershop.config.define_teleport.defined"),
        TemplateResolver.templates(
            Template.template("diag_teleport_point", locationDto.toString())));
    // TODO: more user-friendly message by preventing usage of LocationDto#toString()
  }

  public @NotNull Component toggleShop(boolean isPlayerShopActive) {
    return miniMessage.deserialize(
        resourceBundle.getString("diagonia.playershop.config.activation.toggled"),
        TemplateResolver.templates(
            Template.template(
                "diag_activation_state",
                miniMessage.deserialize(
                    resourceBundle.getString(
                        isPlayerShopActive
                            ? "diagonia.playershop.config.activation.toggled.enabled"
                            : "diagonia.playershop.config.activation.toggled.disabled")))));
  }

  public @NotNull Component shopActivationRequireTeleportPointFirst() {
    return miniMessage.deserialize(
        resourceBundle.getString(
            "diagonia.playershop.config.activation.enabling.teleport_point_definition_required"));
  }

  public @NotNull Component insufficientFunds() {
    return miniMessage.deserialize(
        resourceBundle.getString("diagonia.playershop.buy.insufficient_funds"));
  }

  public @NotNull Component transactionFailed() {
    return miniMessage.deserialize(
        resourceBundle.getString("diagonia.playershop.buy.transaction_failed"));
  }

  public @NotNull Component noTeleportPointDefined() {
    return miniMessage.deserialize(
        resourceBundle.getString("diagonia.playershop.teleport.no_tp_defined_error"));
  }
}
