package com.hei.school.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "book")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long bookId;

    @Column(nullable = false)
    private String title;

    @Column(unique = true)
    private String isbn;
    private Integer publicationYear;

    @Column(nullable = false)
    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "library_id" , nullable = false)
    private Library library;
}
