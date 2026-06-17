package com.hei.school.dto;

import com.hei.school.entity.BookFormat;
import com.hei.school.entity.CopyStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookCopyDTO {

  private Integer copyId;
  private Integer bookId;
  private Integer libraryId;
  private BookFormat format;
  private BigDecimal copyPrice;
  private CopyStatus status;
  private LocalDate acquisitionDate;
}
