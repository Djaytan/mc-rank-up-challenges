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

package fr.voltariuss.diagonia.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import fr.voltariuss.diagonia.model.config.rank.Rank;
import fr.voltariuss.diagonia.model.config.rank.RankChallenge;
import fr.voltariuss.diagonia.model.config.rank.RankConfig;
import fr.voltariuss.diagonia.model.config.rank.RankUpPrerequisites;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Singleton;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;

@Singleton
public class RankConfigDeserializer implements JsonDeserializer<RankConfig> {

  @Override
  public RankConfig deserialize(
      JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext)
      throws JsonParseException {
    JsonObject root = jsonElement.getAsJsonObject();
    JsonArray ranksJson = root.get("ranks").getAsJsonArray();
    List<Rank> ranks = new ArrayList<>();

    ranksJson.forEach(
        rankElt -> {
          JsonObject rankJson = rankElt.getAsJsonObject();
          List<String> description = new ArrayList<>();
          List<String> profits = new ArrayList<>();

          String id = rankJson.get("id").getAsString();
          String rankName = rankJson.get("rankName").getAsString();
          TextColor rankColor = TextColor.fromHexString(rankJson.get("rankColor").getAsString());

          rankJson
              .get("rankDesc")
              .getAsJsonArray()
              .forEach(desc -> description.add(desc.getAsString()));
          rankJson
              .get("rankProfits")
              .getAsJsonArray()
              .forEach(profit -> profits.add(profit.getAsString()));

          boolean isRankUpActivated = rankJson.get("rankUpActivated").getAsBoolean();

          if (isRankUpActivated) {
            List<RankChallenge> challenges = new ArrayList<>();
            rankJson
                .get("rankUpChallenges")
                .getAsJsonArray()
                .forEach(
                    challengeElem -> {
                      JsonObject challengeObject = challengeElem.getAsJsonObject();
                      challenges.add(
                          RankChallenge.builder()
                              .material(
                                  Material.matchMaterial(
                                      challengeObject.get("challengeItemMaterial").getAsString()))
                              .amount(
                                  challengeObject.get("challengeItemAmount").getAsInt())
                              .build());
                    });

            JsonObject prerequisiteJson = rankJson.get("rankUpPrerequisites").getAsJsonObject();
            RankUpPrerequisites rankUpPrerequisites =
                RankUpPrerequisites.builder()
                    .moneyPrice(prerequisiteJson.get("moneyPrice").getAsDouble())
                    .totalMcExpLevels(prerequisiteJson.get("totalMcExpLevels").getAsInt())
                    .totalJobsLevel(prerequisiteJson.get("totalJobLevel").getAsInt())
                    .build();

            String rankUpTarget = rankJson.get("rankUpTarget").getAsString();

            ranks.add(
                Rank.builder()
                    .id(id)
                    .name(rankName)
                    .description(description)
                    .color(rankColor)
                    .profits(profits)
                    .isRankUpActivated(true)
                    .rankUpPrerequisites(rankUpPrerequisites)
                    .rankUpChallenges(challenges)
                    .rankUpTarget(rankUpTarget)
                    .build());
            return;
          }
          ranks.add(
              Rank.builder()
                  .id(id)
                  .name(rankName)
                  .description(description)
                  .color(rankColor)
                  .profits(profits)
                  .isRankUpActivated(false)
                  .build());
        });

    return RankConfig.builder().ranks(ranks).build();
  }
}
