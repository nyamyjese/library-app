package com.hei.school.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "\"stock_movement\"")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Column(name = "copy_id")
    private UUID copyId;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "movement_date")
    private Instant movementDate;

    @Column(name = "arrival_id")
    private UUID arrivalId;

    @Column(name = "sale_item_id")
    private UUID saleItemId;
}