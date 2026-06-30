package com.hei.school.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "customer")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Customer {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @NotBlank(message = "The first name is mandatory")
  @Column(name = "first_name", nullable = false)
  private String firstName;

  @NotBlank(message = "The last name is mandatory")
  @Column(name = "last_name", nullable = false)
  private String lastName;

  @NotBlank(message = "The email is mandatory")
  @Email(message = "The email is invalid")
  @Column(name = "email", nullable = false, unique = true)
  private String email;

  @NotBlank(message = "The phone is mandatory")
  @Column(name = "phone", nullable = false, unique = true)
  private String phone;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  @PrePersist
  public void prePersist() {
    Instant now = Instant.now();
    this.createdAt = now;
    this.updatedAt = now;
  }

  @PreUpdate
  public void preUpdate() {
    this.updatedAt = Instant.now();
  }
}
