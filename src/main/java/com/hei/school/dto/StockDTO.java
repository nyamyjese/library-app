package com.hei.school.dto;

import java.util.UUID;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockDTO {

    private UUID bookId;
    private UUID libraryId;
    private long totalCopies;
    private long availableCopies;
    private long soldCopies;
    private long damagedCopies;
    private long lostCopies;
}