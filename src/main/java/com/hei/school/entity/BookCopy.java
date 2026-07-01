package com.hei.school.entity;

import com.hei.school.entity.enums.BookCopyFormat;
import com.hei.school.entity.enums.BookCopyStatus;
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
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id")
  private UUID id;

  @Enumerated(EnumType.STRING)
  @Column(name = "format", nullable = false, length = 50)
  private BookCopyFormat format;

  @Column(name = "isbn", length = 13, unique = true)
  private String isbn;

  @Column(name = "selling_price", nullable = false, precision = 10, scale = 2)
  private BigDecimal sellingPrice;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private BookCopyStatus status;

  @ManyToOne
  @JoinColumn(name = "book_id", nullable = false)
  private Book book;

  @ManyToOne
  @JoinColumn(name = "livrary_id", nullable = false)
  private Library library;
}
