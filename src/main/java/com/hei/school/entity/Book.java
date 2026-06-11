package com.hei.school.entity;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "books")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Book {

    @Id
    private UUID book_id;

    @Column(name = "title", length = 255, nullable = false)
    private String title;

    @Column(name = "isbn", length = 13)
    private String isbn;

    @Column(name = "date_publication", nullable = false)
    private LocalDate publication = LocalDate.now();

}
