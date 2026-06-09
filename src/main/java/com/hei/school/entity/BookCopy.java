package com.hei.school.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.*;

@Entity
@Table(name = "\"BookCopy\"")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookCopy {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "copy_id")
  private Integer copyId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "book_id", nullable = false)
  private Book book;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "library_id", nullable = false)
  private Library library;

  @Enumerated(EnumType.STRING)
  @Column(name = "format", nullable = false, length = 50)
  private BookFormat format;

  @Column(name = "copy_price", nullable = false, precision = 10, scale = 2)
  private BigDecimal copyPrice;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 20)
  private CopyStatus status;

  @Column(name = "acquisition_date", nullable = false)
  private LocalDate acquisitionDate;
}
