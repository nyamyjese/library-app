package com.hei.school.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hei.school.entity.Genre;

public interface GenreRepository extends JpaRepository<Genre, UUID> {
    List<Genre> findByNameContainingIgnoreCase(String name);
}
