package com.hei.school.dto;

import com.hei.school.entity.BookFormat;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArrivalDTO {

  private Integer arrivalId;
  private Integer bookId;
  private Integer libraryId;
  private BookFormat format;
  private Integer quantity;
  private LocalDate arrivalDate;
  private BigDecimal unitCost;
}
