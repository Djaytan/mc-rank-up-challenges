package fr.voltariuss.diagonia.model;

import com.google.gson.Gson;
import fr.voltariuss.diagonia.model.dto.LocationDto;
import javax.annotation.Nullable;
import javax.persistence.AttributeConverter;
import org.jetbrains.annotations.NotNull;

public class LocationDtoConverter implements AttributeConverter<LocationDto, String> {

  @Override
  public @NotNull String convertToDatabaseColumn(@Nullable LocationDto locationDto) {
    Gson gson = new Gson();
    return gson.toJson(locationDto);
  }

  @Override
  public @Nullable LocationDto convertToEntityAttribute(@Nullable String locationJson) {
    Gson gson = new Gson();
    return gson.fromJson(locationJson, LocationDto.class);
  }
}
