package com.hei.school.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Arrival {

  private UUID arrivalId;
  private UUID bookId;
  private UUID libraryId;
  private BookFormat format;
  private Integer quantity;
  private Instant arrivalDate;
  private BigDecimal unitCost;
}