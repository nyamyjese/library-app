package com.hei.school.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "\"Library\"")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Library {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "library_id")
  private Integer libraryId;

  @Column(name = "name", nullable = false, length = 100)
  private String name;

  @Column(name = "address", length = 255)
  private String address;

  @Column(name = "phone", length = 20)
  private String phone;
}
