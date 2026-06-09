package com.hei.school.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.*;

@Entity
@Table(name = "\"Book\"")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "book_id")
  private Integer bookId;

  @Column(name = "title", nullable = false, length = 255)
  private String title;

  @Column(name = "isbn", length = 13)
  private String isbn;

  @Column(name = "publication_year")
  private Integer publicationYear;

  @Column(name = "base_price", precision = 10, scale = 2)
  private BigDecimal basePrice;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "library_id")
  private Library library;
}
