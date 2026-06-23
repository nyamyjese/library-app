package com.hei.school.service;

import com.hei.school.dto.BookCopyDTO;
import com.hei.school.entity.BookCopy;
import com.hei.school.entity.CopyStatus;
import com.hei.school.repository.BookCopyRepository;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BookCopyService {

  private final BookCopyRepository bookCopyRepository;

  public BookCopyDTO getById(UUID id) {
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

  public List<BookCopyDTO> getByBook(UUID bookId) {
    return bookCopyRepository.findAllByBookId(bookId).stream().map(this::toDTO).toList();
  }

  public List<BookCopyDTO> getAvailableByBook(UUID bookId) {
    return bookCopyRepository.findAllByBookIdAndStatus(bookId, CopyStatus.AVAILABLE)
            .stream().map(this::toDTO).toList();
  }

  public List<BookCopyDTO> getByLibrary(UUID libraryId) {
    return bookCopyRepository.findAllByLibraryId(libraryId).stream().map(this::toDTO).toList();
  }

  public BookCopyDTO updateStatus(UUID id, CopyStatus newStatus) {
    BookCopy copy = bookCopyRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("BookCopy introuvable : id=" + id));
    copy.setStatus(newStatus);
    return toDTO(bookCopyRepository.save(copy));
  }

  public long countAvailableByBook(UUID bookId) {
    return bookCopyRepository.countByBookIdAndStatus(bookId, CopyStatus.AVAILABLE);
  }

  private BookCopyDTO toDTO(BookCopy c) {
    return BookCopyDTO.builder()
            .id(c.getId())
            .bookId(c.getBookId())
            .libraryId(c.getLibraryId())
            .format(c.getFormat())
            .isbn(c.getIsbn())
            .sellingPrice(c.getSellingPrice())
            .status(c.getStatus())
            .build();
  }
}