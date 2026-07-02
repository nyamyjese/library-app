package com.hei.school.dto;

<<<<<<< HEAD
import java.math.BigDecimal;
import java.util.UUID;

import com.hei.school.entity.enums.BookCopyFormat;
import com.hei.school.entity.enums.BookCopyStatus;
=======
import com.hei.school.entity.enums.BookCopyFormat;
import com.hei.school.entity.enums.BookCopyStatus;
import java.math.BigDecimal;
import java.util.UUID;
>>>>>>> preprod
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookCopyDTO {

<<<<<<< HEAD
    private UUID id;
    private UUID bookId;
    private UUID libraryId;
    private BookCopyFormat format;
    private String isbn;
    private BigDecimal sellingPrice;
    private BookCopyStatus status;
}
=======
  private UUID id;
  private UUID bookId;
  private UUID libraryId;
  private BookCopyFormat format;
  private String isbn;
  private BigDecimal sellingPrice;
  private BookCopyStatus status;
}
>>>>>>> preprod
