package com.hei.school.entity;

import com.hei.school.entity.enums.MovementReason;
import com.hei.school.entity.enums.MovementType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.Instant;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "stock_movement")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StockMovement {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @NotNull(message = "The quantity is mandatory")
  @Positive(message = "Quantity must be positive")
  @Column(name = "quantity", nullable = false)
  private Integer quantity;

  @NotNull(message = "The movement type is mandatory")
  @Enumerated(EnumType.STRING)
  @Column(name = "movement_type", nullable = false)
  private MovementType movementType;

  @NotNull(message = "The reason is mandatory")
  @Enumerated(EnumType.STRING)
  @Column(name = "reason", nullable = false)
  private MovementReason reason;

  @ManyToOne
  @JoinColumn(name = "book_copy_id", nullable = false)
  private BookCopy bookCopy;

  @ManyToOne
  @JoinColumn(name = "arrival_id", nullable = true)
  private Arrival arrival;

  @ManyToOne
  @JoinColumn(name = "sale_item_id", nullable = true)
  private SaleItem saleItem;

  @NotNull(message = "The movement date is mandatory")
  @Column(name = "movement_date", nullable = false)
  private Instant movementDate;
}
