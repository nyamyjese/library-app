package com.hei.school.dto.response;

import com.hei.school.entity.enums.BookCopyFormat;
import com.hei.school.entity.enums.BookCopyStatus;
import java.math.BigDecimal;
import java.util.UUID;

public record BookCopyResponse(
    UUID id,
    UUID bookId,
    String bookTitle,
    UUID libraryId,
    String libraryName,
    BookCopyFormat format,
    String isbn,
    BigDecimal sellingPrice,
    BookCopyStatus status) {}
