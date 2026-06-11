package com.hei.school.entity;

import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="authors")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Author {

    @Id 
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id_author;

    @Column(name = "first_name", nullable = false, length  = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

}