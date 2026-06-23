package com.hei.school.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
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
  @Column(name = "id")
  private UUID id;

  @Column(name = "book_id", nullable = false)
  private UUID bookId;

  @Column(name = "library_id", nullable = false)
  private UUID libraryId;

  @Enumerated(EnumType.STRING)
  @Column(name = "format", nullable = false, length = 50)
  private BookFormat format;

  @Column(name = "isbn", length = 255)
  private String isbn;

  @Column(name = "selling_price", nullable = false, precision = 10, scale = 2)
  private BigDecimal sellingPrice;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 20)
  private CopyStatus status;
}