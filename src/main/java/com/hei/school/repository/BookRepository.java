package com.hei.school.repository;

import com.hei.school.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository

public interface BookRepository extends JpaRepository<Book, UUID> {

    List<Book> findByTitleContainingIgnoreCase(String title);

    Optional<Book> findByIsbn(String isbn);

    List<Book> findByLibrary_LibraryId(Long libraryId);

    List<Book> findByPublicationYear(Integer year);

    List<Book> findAllByOrderByTitleAsc();

    List<Book> findAllByOrderByPriceAsc();
}
