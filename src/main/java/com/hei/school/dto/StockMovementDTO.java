package com.hei.school.dto;

import java.time.Instant;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockMovementDTO {

    private UUID id;
    private UUID copyId;
    private Integer quantity;
    private Instant movementDate;
    private UUID arrivalId;
    private UUID saleItemId;
}