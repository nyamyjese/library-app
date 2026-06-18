package com.hei.school.repository;

import com.hei.school.entity.Library;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LibraryRepository extends JpaRepository<Library, UUID> {
  List<Library> findByNameContainingIgnoreCase(String name);
}
