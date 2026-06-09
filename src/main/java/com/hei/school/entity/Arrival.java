package com.hei.school.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
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
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "arrival_id")
  private Integer arrivalId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "book_id", nullable = false)
  private Book book;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "library_id", nullable = false)
  private Library library;

  @Enumerated(EnumType.STRING)
  @Column(name = "format", nullable = false, length = 50)
  private BookFormat format;

  @Column(name = "quantity", nullable = false)
  private Integer quantity;

  @Column(name = "arrival_date", nullable = false)
  private LocalDate arrivalDate;

  @Column(name = "unit_cost", nullable = false, precision = 10, scale = 2)
  private BigDecimal unitCost;
}
