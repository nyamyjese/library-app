package com.hei.school.repository;

import com.hei.school.entity.BookCopy;
import com.hei.school.entity.enums.BookCopyStatus;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookCopyRepository extends JpaRepository<BookCopy, UUID> {

  List<BookCopy> findByStatus(BookCopyStatus status);

  List<BookCopy> findByBookId(UUID bookId);

  List<BookCopy> findByLibraryId(UUID libraryId);

  List<BookCopy> findAllByLibrary_Id(UUID libraryId);

  List<BookCopy> findAllByBook_IdAndStatus(UUID bookId, BookCopyStatus status);

  List<BookCopy> findAllByLibrary_IdAndStatus(UUID libraryId, BookCopyStatus status);

  long countByBook_IdAndStatus(UUID bookId, BookCopyStatus status);

  @Query("SELECT DISTINCT bc.book.id FROM BookCopy bc")
  List<UUID> findDistinctBookIds();

  List<BookCopy> findAllByIsbn(String isbn);

  @Query(
      "SELECT bc.book.id FROM BookCopy bc WHERE bc.status = 'AVAILABLE' GROUP BY bc.book.id HAVING"
          + " COUNT(bc) < :threshold")
  List<UUID> findBookIdsWithLowStock(@Param("threshold") long threshold);
}
