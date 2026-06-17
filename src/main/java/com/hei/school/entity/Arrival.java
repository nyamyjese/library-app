package com.hei.school.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "\"Arrival\"")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Arrival {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "arrival_id")
  private UUID arrivalId;

  @Column(name = "book_id", nullable = false)
  private UUID bookId;

  @Column(name = "library_id", nullable = false)
  private UUID libraryId;

  @Enumerated(EnumType.STRING)
  @Column(name = "format", nullable = false, length = 50)
  private BookFormat format;

  @Column(name = "quantity", nullable = false)
  private Integer quantity;

  @Column(name = "arrival_date", nullable = false)
  private Instant arrivalDate;

  @Column(name = "unit_cost", nullable = false, precision = 10, scale = 2)
  private BigDecimal unitCost;
}