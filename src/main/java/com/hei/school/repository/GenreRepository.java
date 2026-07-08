package com.hei.school.repository;

import com.hei.school.entity.Genre;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GenreRepository extends JpaRepository<Genre, UUID> {
  List<Genre> findByNameContainingIgnoreCase(String name);
}
