package fr.voltariuss.diagonia.model.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class EconomyBalanceDto {
  @NonNull private final Double amount;
  @NonNull private final String formattedAmount;
}
