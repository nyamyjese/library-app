package com.hei.school.repository;

import com.hei.school.entity.BookCopy;
import com.hei.school.entity.enums.BookCopyStatus;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookCopyRepository extends JpaRepository<BookCopy, UUID> {

  List<BookCopy> findByStatus(BookCopyStatus status);

  List<BookCopy> findByBookId(UUID bookId);

  List<BookCopy> findByLibraryId(UUID libraryId);
}
