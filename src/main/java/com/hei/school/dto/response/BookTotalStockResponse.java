package com.hei.school.dto.response;

import java.util.UUID;

public record BookTotalStockResponse(UUID bookId, String bookTitle, Integer totalStock) {}
