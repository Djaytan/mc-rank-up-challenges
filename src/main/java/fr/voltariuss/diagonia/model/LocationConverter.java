package fr.voltariuss.diagonia.model;

import javax.annotation.Nullable;
import javax.persistence.AttributeConverter;

import com.google.gson.Gson;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public class LocationConverter implements AttributeConverter<Location, String> {

  @Override
  public @NotNull String convertToDatabaseColumn(@Nullable Location locationEntity) {
    Gson gson = new Gson();
    return gson.toJson(locationEntity);
  }

  @Override
  public @Nullable Location convertToEntityAttribute(@Nullable String locationJson) {
    Gson gson = new Gson();
    return gson.fromJson(locationJson, Location.class);
  }
}
