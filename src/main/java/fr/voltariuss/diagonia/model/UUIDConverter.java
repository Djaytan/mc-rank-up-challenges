package fr.voltariuss.diagonia.model;

import java.util.UUID;
import javax.persistence.AttributeConverter;

public class UUIDConverter implements AttributeConverter<UUID, String> {

  @Override
  public String convertToDatabaseColumn(UUID uuid) {
    return uuid.toString();
  }

  @Override
  public UUID convertToEntityAttribute(String s) {
    return UUID.fromString(s);
  }
}
