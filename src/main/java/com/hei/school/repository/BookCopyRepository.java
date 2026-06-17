package com.hei.school.repository;

import com.hei.school.entity.BookCopy;
import com.hei.school.entity.CopyStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookCopyRepository extends JpaRepository<BookCopy, Integer> {

  List<BookCopy> findAllByStatus(CopyStatus status);

  List<BookCopy> findAllByBook_BookId(Integer bookId);

  List<BookCopy> findAllByLibrary_LibraryId(Integer libraryId);

  List<BookCopy> findAllByBook_BookIdAndStatus(Integer bookId, CopyStatus status);

  List<BookCopy> findAllByLibrary_LibraryIdAndStatus(Integer libraryId, CopyStatus status);

  long countByBook_BookIdAndStatus(Integer bookId, CopyStatus status);
}
