package com.hei.school.models;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.*;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
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
