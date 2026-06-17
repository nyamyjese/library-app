package com.hei.school.repository;

import com.hei.school.entity.BookAuthor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface BookAuthorRepository extends JpaRepository<BookAuthor, UUID> {

    @Query("SELECT ba FROM BookAuthor ba WHERE ba.bookId = :bookId")
    List<BookAuthor> findByBookId(@Param("bookId") UUID bookId);

    @Query("SELECT ba FROM BookAuthor ba WHERE ba.author.idAuthor = :authorId")
    List<BookAuthor> findByAuthorId(@Param("authorId") UUID authorId);

    @Query("SELECT CASE WHEN COUNT(ba) > 0 THEN true ELSE false END FROM BookAuthor ba WHERE ba.bookId = :bookId AND ba.author.idAuthor = :authorId")
    boolean existsByBookIdAndAuthorId(@Param("bookId") UUID bookId, @Param("authorId") UUID authorId);

    @Modifying
    @Transactional
    @Query("DELETE FROM BookAuthor ba WHERE ba.bookId = :bookId AND ba.author.idAuthor = :authorId")
    void deleteByBookIdAndAuthorId(@Param("bookId") UUID bookId, @Param("authorId") UUID authorId);

    @Query("SELECT ba FROM BookAuthor ba JOIN FETCH ba.author WHERE ba.bookId = :bookId")
    List<BookAuthor> findByBookIdWithAuthor(@Param("bookId") UUID bookId);
}