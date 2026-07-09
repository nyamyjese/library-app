package com.hei.school.dto.response;

import java.util.UUID;

public record LowStockResponse(UUID bookId, String bookTitle, long availableCopies) {}
