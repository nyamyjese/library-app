package com.hei.school.dto;

import com.hei.school.entity.BookFormat;
import com.hei.school.entity.CopyStatus;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookCopyDTO {

  private UUID id;
  private UUID bookId;
  private UUID libraryId;
  private BookFormat format;
  private String isbn;
  private BigDecimal sellingPrice;
  private CopyStatus status;
}