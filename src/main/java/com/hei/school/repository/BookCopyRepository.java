package com.hei.school.repository;

import com.hei.school.entity.BookCopy;
import com.hei.school.entity.enums.BookCopyStatus;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookCopyRepository extends JpaRepository<BookCopy, UUID> {

    List<BookCopy> findAllByStatus(BookCopyStatus status);

    List<BookCopy> findAllByBook_Id(UUID bookId);

    List<BookCopy> findAllByLibrary_Id(UUID libraryId);

    List<BookCopy> findAllByBook_IdAndStatus(UUID bookId, BookCopyStatus status);

    List<BookCopy> findAllByLibrary_IdAndStatus(UUID libraryId, BookCopyStatus status);

    long countByBook_IdAndStatus(UUID bookId, BookCopyStatus status);
}