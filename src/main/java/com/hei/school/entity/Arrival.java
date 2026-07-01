package com.hei.school.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "\"arrivals\"")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Arrival {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @NotNull(message = "The quantity is mandatory")
  @Positive(message = "Quantity must be positive")
  @Column(name = "quantity", nullable = false)
  private Integer quantity;

  @NotNull(message = "The unit price is mandatory")
  @Positive(message = "Unit price must be positive")
  @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
  private BigDecimal unitPrice;

  @NotNull(message = "The arrival date is mandatory")
  @Column(name = "arrival_date", nullable = false)
  private Instant arrivalDate;

  @ManyToOne
  @JoinColumn(name = "book_copy_id", nullable = false)
  private BookCopy bookCopy;
}
