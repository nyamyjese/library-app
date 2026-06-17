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
    private UUID idAuthor;

    @Column(name = "first_name", nullable = false, length  = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Sexe sexe = Sexe.M;

    @Builder.Default
    private Instant createdDate = Instant.now();

    @Builder.Default
    private Instant updatedDate = Instant.now();
}
