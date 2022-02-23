package fr.voltariuss.diagonia.model;

import com.google.gson.*;
import fr.voltariuss.diagonia.model.config.RankConfig;
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
    List<RankConfig.Rank> ranks = new ArrayList<>();

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
            List<RankConfig.RankChallenge> challenges = new ArrayList<>();
            rankJson
                .get("rankUpChallenges")
                .getAsJsonArray()
                .forEach(
                    challengeElem -> {
                      JsonObject challengeObject = challengeElem.getAsJsonObject();
                      challenges.add(
                          RankConfig.RankChallenge.builder()
                              .challengeType(
                                  RankChallengeType.valueOf(
                                      challengeObject
                                          .get("challengeType")
                                          .getAsString()
                                          .toUpperCase()))
                              .challengeItemMaterial(
                                  Material.getMaterial(
                                      challengeObject.get("challengeItemMaterial").getAsString()))
                              .challengeItemAmount(
                                  challengeObject.get("challengeItemAmount").getAsInt())
                              .build());
                    });

            JsonObject prerequisiteJson = rankJson.get("rankUpPrerequisites").getAsJsonObject();
            RankConfig.RankUpPrerequisite rankUpPrerequisite =
                RankConfig.RankUpPrerequisite.builder()
                    .moneyPrice(prerequisiteJson.get("moneyPrice").getAsDouble())
                    .totalMcExpLevels(prerequisiteJson.get("totalMcExpLevels").getAsInt())
                    .totalJobsLevel(prerequisiteJson.get("totalJobLevel").getAsInt())
                    .build();

            String rankUpTarget = rankJson.get("rankUpTarget").getAsString();

            ranks.add(
                RankConfig.Rank.builder()
                    .id(id)
                    .name(rankName)
                    .description(description)
                    .color(rankColor)
                    .profits(profits)
                    .isRankUpActivated(true)
                    .rankUpPrerequisite(rankUpPrerequisite)
                    .rankUpChallenges(challenges)
                    .rankUpTarget(rankUpTarget)
                    .build());
          } else {
            ranks.add(
                RankConfig.Rank.builder()
                    .id(id)
                    .name(rankName)
                    .description(description)
                    .color(rankColor)
                    .profits(profits)
                    .isRankUpActivated(false)
                    .build());
          }
        });

    return RankConfig.builder().ranks(ranks).build();
  }
}
