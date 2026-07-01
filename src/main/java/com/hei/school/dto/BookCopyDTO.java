package com.hei.school.dto;

import java.math.BigDecimal;
import java.util.UUID;

import com.hei.school.entity.enums.BookCopyFormat;
import com.hei.school.entity.enums.BookCopyStatus;
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
    private BookCopyFormat format;
    private String isbn;
    private BigDecimal sellingPrice;
    private BookCopyStatus status;
}