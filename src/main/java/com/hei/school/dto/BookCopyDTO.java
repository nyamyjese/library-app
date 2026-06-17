package com.hei.school.dto;

import com.hei.school.entity.BookFormat;
import com.hei.school.entity.CopyStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookCopyDTO {

  private UUID copyId;
  private UUID bookId;
  private UUID libraryId;
  private BookFormat format;
  private BigDecimal copyPrice;
  private CopyStatus status;
  private Instant acquisitionDate;
}