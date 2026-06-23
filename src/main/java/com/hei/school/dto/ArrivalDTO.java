package com.hei.school.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArrivalDTO {

  private UUID id;
  private UUID copyId;
  private Integer quantity;
  private BigDecimal unitCost;
  private Instant arrivalDate;
}