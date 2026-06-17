package com.hei.school.repository;

import com.hei.school.entity.BookCopy;
import com.hei.school.entity.CopyStatus;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookCopyRepository extends JpaRepository<BookCopy, UUID> {

  List<BookCopy> findAllByStatus(CopyStatus status);

  List<BookCopy> findAllByBookId(UUID bookId);

  List<BookCopy> findAllByLibraryId(UUID libraryId);

  List<BookCopy> findAllByBookIdAndStatus(UUID bookId, CopyStatus status);

  List<BookCopy> findAllByLibraryIdAndStatus(UUID libraryId, CopyStatus status);

  long countByBookIdAndStatus(UUID bookId, CopyStatus status);
}