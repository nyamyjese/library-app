package com.hei.school.dto.request;

import com.hei.school.entity.enums.BookCopyFormat;
import java.util.UUID;

public record LowStockResponse(
    UUID bookCopyId,
    UUID bookId,
    String bookTitle,
    BookCopyFormat format,
    String isbn,
    Integer currentStock) {}
