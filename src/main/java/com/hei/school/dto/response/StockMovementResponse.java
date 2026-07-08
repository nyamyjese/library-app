package com.hei.school.dto.response;

import com.hei.school.entity.enums.MovementReason;
import com.hei.school.entity.enums.MovementType;
import java.time.Instant;
import java.util.UUID;

public record StockMovementResponse(
    UUID id,
    Integer quantity,
    MovementType movementType,
    MovementReason reason,
    UUID bookCopyId,
    String bookTitle,
    String bookCopyIsbn,
    UUID arrivalId,
    UUID saleItemId,
    Instant movementDate) {}
