package com.hei.school.service;

import com.hei.school.dto.BookCopyDTO;
import com.hei.school.entity.BookCopy;
import com.hei.school.entity.CopyStatus;
import com.hei.school.repository.BookCopyRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BookCopyService {

  private final BookCopyRepository bookCopyRepository;

  public BookCopyDTO getById(Integer id) {
    return toDTO(bookCopyRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("BookCopy introuvable : id=" + id)));
  }

  public List<BookCopyDTO> getAll() {
    return bookCopyRepository.findAll().stream().map(this::toDTO).toList();
  }

  public List<BookCopyDTO> getAvailable() {
    return bookCopyRepository.findAllByStatus(CopyStatus.AVAILABLE).stream().map(this::toDTO).toList();
  }

  public List<BookCopyDTO> getSold() {
    return bookCopyRepository.findAllByStatus(CopyStatus.SOLD).stream().map(this::toDTO).toList();
  }

  public List<BookCopyDTO> getByBook(Integer bookId) {
    return bookCopyRepository.findAllByBook_BookId(bookId).stream().map(this::toDTO).toList();
  }

  public List<BookCopyDTO> getAvailableByBook(Integer bookId) {
    return bookCopyRepository.findAllByBook_BookIdAndStatus(bookId, CopyStatus.AVAILABLE)
        .stream().map(this::toDTO).toList();
  }

  public List<BookCopyDTO> getByLibrary(Integer libraryId) {
    return bookCopyRepository.findAllByLibrary_LibraryId(libraryId).stream().map(this::toDTO).toList();
  }

  public List<BookCopyDTO> getAvailableByLibrary(Integer libraryId) {
    return bookCopyRepository.findAllByLibrary_LibraryIdAndStatus(libraryId, CopyStatus.AVAILABLE)
        .stream().map(this::toDTO).toList();
  }

  public BookCopyDTO updateStatus(Integer id, CopyStatus newStatus) {
    BookCopy copy = bookCopyRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("BookCopy introuvable : id=" + id));
    copy.setStatus(newStatus);
    return toDTO(bookCopyRepository.save(copy));
  }

  public long countAvailableByBook(Integer bookId) {
    return bookCopyRepository.countByBook_BookIdAndStatus(bookId, CopyStatus.AVAILABLE);
  }

  // ---- Mapper ----

  private BookCopyDTO toDTO(BookCopy c) {
    return BookCopyDTO.builder()
        .copyId(c.getCopyId())
        .bookId(c.getBook() != null ? c.getBook().getBookId() : null)
        .libraryId(c.getLibrary() != null ? c.getLibrary().getLibraryId() : null)
        .format(c.getFormat())
        .copyPrice(c.getCopyPrice())
        .status(c.getStatus())
        .acquisitionDate(c.getAcquisitionDate())
        .build();
  }
}
