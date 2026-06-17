package com.hei.school.repository;

import com.hei.school.entity.Author;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author, UUID> {
  List<Author> findByLastNameContainingIgnoreCase(String lastName);
}
