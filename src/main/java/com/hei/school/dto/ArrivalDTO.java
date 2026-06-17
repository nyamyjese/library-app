package com.hei.school.dto;

import com.hei.school.entity.BookFormat;
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

  private UUID arrivalId;
  private UUID bookId;
  private UUID libraryId;
  private BookFormat format;
  private Integer quantity;
  private Instant arrivalDate;
  private BigDecimal unitCost;
}