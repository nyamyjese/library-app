package com.hei.school.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "sale_item")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SaleItem {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @NotNull(message = "The unit price is mandatory")
  @Positive(message = "Unit price must be positive")
  @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
  private BigDecimal unitPrice;

  @NotNull(message = "The quantity is mandatory")
  @Positive(message = "Quantity must be positive")
  @Column(name = "quantity", nullable = false)
  private Integer quantity;

  @ManyToOne
  @JoinColumn(name = "book_copy_id", nullable = false)
  private BookCopy bookCopy;

  @ManyToOne
  @JoinColumn(name = "sale_id", nullable = false)
  private Sale sale;
}
