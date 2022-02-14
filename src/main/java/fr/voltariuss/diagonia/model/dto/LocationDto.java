package fr.voltariuss.diagonia.model.dto;

import lombok.Data;
import lombok.NonNull;

@Data
public class LocationDto {

  @NonNull private String worldName;
  @NonNull private Double x;
  @NonNull private Double y;
  @NonNull private Double z;
  @NonNull private Float pitch;
  @NonNull private Float yaw;
}
