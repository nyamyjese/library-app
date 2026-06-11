package com.hei.school.entity;

import java.time.Instant;
import java.util.UUID;

import com.hei.school.entity.enums.Sexe;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "sexe", nullable = true)
    private Sexe sexe = Sexe.M;

    @Column(name = "create_date", nullable = false)
    private Instant createdDate;

    @Column(name = "update_date", nullable = false)
    private Instant updatedDate;
}