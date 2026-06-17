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
public class BookCopy {

  private UUID copyId;
  private UUID bookId;
  private UUID libraryId;
  private BookFormat format;
  private BigDecimal copyPrice;
  private CopyStatus status;
  private Instant acquisitionDate;
}