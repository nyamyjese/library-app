package com.hei.school.models;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.*;
import jakarta.persistence.ManyToMany;
import lombok.*;

@Entity
@Table(name="authors")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Author {

    @Id
    private UUID id_author;

    @Column(name = "first_name", nullable = false, length  = 255)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 150)
    private String lastName;


    @ManyToMany(mappedBy = "authors", fetch = FetchType.LAZY)
    private List<Book> books;
}
