package com.hei.school.repository;

import com.hei.school.entity.BookCopy;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StockRepository extends JpaRepository<BookCopy, UUID> {

    @Query("SELECT DISTINCT b.bookId FROM BookCopy b")
    List<UUID> findDistinctBookIds();

    @Query("SELECT DISTINCT b.libraryId FROM BookCopy b")
    List<UUID> findDistinctLibraryIds();
}