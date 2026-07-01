package com.hei.school.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "library")
public class Library {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID libraryId;

  @Column(nullable = false)
  private String name;

  private String address;
  private String phone;

  @OneToMany(mappedBy = "library", cascade = CascadeType.ALL)
  private List<Book> books = new ArrayList<>();

  @OneToMany(mappedBy = "library")
  private List<BookCopy> copies;
}
