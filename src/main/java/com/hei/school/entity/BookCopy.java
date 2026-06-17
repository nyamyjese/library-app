package com.hei.school.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
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
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "copy_id")
  private UUID copyId;

  @Column(name = "book_id", nullable = false)
  private UUID bookId;

  @Column(name = "library_id", nullable = false)
  private UUID libraryId;

  @Enumerated(EnumType.STRING)
  @Column(name = "format", nullable = false, length = 50)
  private BookFormat format;

  @Column(name = "copy_price", nullable = false, precision = 10, scale = 2)
  private BigDecimal copyPrice;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 20)
  private CopyStatus status;

  @Column(name = "acquisition_date", nullable = false)
  private Instant acquisitionDate;
}