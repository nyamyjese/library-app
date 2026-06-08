package com.hei.school.repository;

import com.hei.school.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository

public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findByTitleContainingIgnoreCase(String title);

    Optional<Book> findByIsbn(String isbn);

    List<Book> findByLibraryId(Long libraryId);

    List<Book> findByPublicationYear(Integer year);

    List<Book> findAllByOrderByTitleAsc();

    List<Book> findAllByOrderByBasePriceAsc();
}
