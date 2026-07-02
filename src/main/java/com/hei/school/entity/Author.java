package com.hei.school.entity;

import com.hei.school.entity.enums.Sexe;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "authors")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Author {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "first_name", nullable = false, length = 100)
  private String firstName;

  @Column(name = "last_name", nullable = false, length = 100)
  private String lastName;

  @Enumerated(EnumType.STRING)
  @Builder.Default
  private Sexe sexe = Sexe.M;

  @Column(name = "created", nullable = false)
  @Builder.Default private Instant createdAt = Instant.now();

  @Column(name = "updated", nullable = false)
  @Builder.Default private Instant updatedAt = Instant.now();
}
