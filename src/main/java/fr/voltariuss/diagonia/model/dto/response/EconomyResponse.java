package fr.voltariuss.diagonia.model.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class EconomyResponse {
  @NonNull private final Double modifiedAmount;
  @NonNull private final Double newBalance;
}
