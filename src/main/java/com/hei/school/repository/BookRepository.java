package com.hei.school.repository;

import com.hei.school.entity.Book;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, UUID> {

  List<Book> findByTitleContainingIgnoreCase(String title);

  Optional<Book> findByIsbn(String isbn);

  List<Book> findByLibrary_LibraryId(UUID libraryId);

  List<Book> findByPublicationYear(Integer year);

  List<Book> findAllByOrderByTitleAsc();

  List<Book> findAllByOrderByPriceAsc();

  @Query("select b.id from Book b join b.authors a where a.id = :authorId")
  List<UUID> findBookIdsByAuthorId(@Param("authorId") UUID authorId);
}