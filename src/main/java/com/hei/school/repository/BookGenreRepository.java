package com.hei.school.repository;

import com.hei.school.entity.BookGenre;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookGenreRepository extends JpaRepository<BookGenre, UUID> {

  List<BookGenre> findByBookId(UUID bookId);

  List<BookGenre> findByGenreId(UUID genreId);

  boolean existsByBookIdAndGenreId(UUID bookId, UUID genreId);

  void deleteByBookIdAndGenreId(UUID bookId, UUID genreId);

  @Query("SELECT bg FROM BookGenre bg JOIN FETCH bg.genre WHERE bg.bookId = :bookId")
  List<BookGenre> findByBookIdWithGenre(@Param("bookId") UUID bookId);
}
