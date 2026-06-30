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
  @Column(name = "id")
  private UUID id;

  @Column(name = "book_copy_id", nullable = false)
  private UUID bookCopyId;

  @Column(name = "quantity", nullable = false)
  private Integer quantity;

  @Column(name = "unit_cost", nullable = false, precision = 10, scale = 2)
  private BigDecimal unitCost;

  @Column(name = "arrival_date", nullable = false)
  private Instant arrivalDate;
}