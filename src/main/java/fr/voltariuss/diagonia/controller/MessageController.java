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

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public interface MessageController {

  void sendInfoMessage(@NotNull CommandSender commandSender, @NotNull Component message);

  void sendSuccessMessage(@NotNull CommandSender commandSender, @NotNull Component message);

  void sendFailureMessage(@NotNull CommandSender commandSender, @NotNull Component message);

  void sendWarningMessage(@NotNull CommandSender commandSender, @NotNull Component message);

  void sendErrorMessage(@NotNull CommandSender commandSender, @NotNull Component message);

  void broadcastMessage(@NotNull Component component);
}
