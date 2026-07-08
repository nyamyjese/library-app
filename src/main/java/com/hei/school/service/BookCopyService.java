package com.hei.school.service;

import com.hei.school.dto.request.CreateBookCopyRequest;
import com.hei.school.dto.request.LowStockResponse;
import com.hei.school.dto.request.UpdateBookCopyRequest;
import com.hei.school.dto.response.BookCopyResponse;
import com.hei.school.entity.Book;
import com.hei.school.entity.BookCopy;
import com.hei.school.entity.Library;
import com.hei.school.entity.enums.BookCopyStatus;
import com.hei.school.exception.NotFoundException;
import com.hei.school.mapper.BookCopyMapper;
import com.hei.school.repository.BookCopyRepository;
import com.hei.school.repository.BookRepository;
import com.hei.school.repository.LibraryRepository;
import com.hei.school.repository.StockMovementRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookCopyService {

  private final BookCopyRepository bookCopyRepository;
  private final BookRepository bookRepository;
  private final LibraryRepository libraryRepository;
  private final StockMovementRepository stockMovementRepository;
  private final BookCopyMapper bookCopyMapper;

  public BookCopyResponse create(CreateBookCopyRequest request) {
    Book book =
        bookRepository
            .findById(request.bookId())
            .orElseThrow(
                () -> new NotFoundException("Book with id " + request.bookId() + " not found"));
    Library library =
        libraryRepository
            .findById(request.libraryId())
            .orElseThrow(
                () ->
                    new NotFoundException("Library with id " + request.libraryId() + " not found"));
    BookCopy bookCopy = bookCopyMapper.toEntity(request, book, library);
    BookCopy saved = bookCopyRepository.save(bookCopy);
    return bookCopyMapper.toResponse(saved);
  }

  public List<BookCopyResponse> getAll(UUID bookId, UUID libraryId, BookCopyStatus status) {
    if (bookId != null) {
      return bookCopyRepository.findByBookId(bookId).stream()
          .map(bookCopyMapper::toResponse)
          .toList();
    }
    if (libraryId != null) {
      return bookCopyRepository.findByLibraryId(libraryId).stream()
          .map(bookCopyMapper::toResponse)
          .toList();
    }
    if (status != null) {
      return bookCopyRepository.findByStatus(status).stream()
          .map(bookCopyMapper::toResponse)
          .toList();
    }
    return bookCopyRepository.findAll().stream().map(bookCopyMapper::toResponse).toList();
  }

  public BookCopyResponse getById(UUID id) {
    BookCopy bookCopy =
        bookCopyRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("BookCopy with id " + id + " not found"));
    return bookCopyMapper.toResponse(bookCopy);
  }

  public BookCopyResponse update(UUID id, UpdateBookCopyRequest request) {
    BookCopy bookCopy =
        bookCopyRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("BookCopy with id " + id + " not found"));
    bookCopy.setFormat(request.format());
    bookCopy.setSellingPrice(request.sellingPrice());
    bookCopy.setStatus(request.status());
    BookCopy saved = bookCopyRepository.save(bookCopy);
    return bookCopyMapper.toResponse(saved);
  }

  public void delete(UUID id) {
    bookCopyRepository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("BookCopy with id " + id + " not found"));
    bookCopyRepository.deleteById(id);
  }

  public List<LowStockResponse> getLowStock(Integer threshold) {
    int limit = threshold != null ? threshold : 3;
    List<UUID> lowStockIds = stockMovementRepository.findBookCopyIdsWithLowStock(limit);
    return lowStockIds.stream()
        .map(
            bookCopyId -> {
              BookCopy bookCopy =
                  bookCopyRepository
                      .findById(bookCopyId)
                      .orElseThrow(
                          () ->
                              new NotFoundException(
                                  "BookCopy with id " + bookCopyId + " not found"));
              Integer currentStock = stockMovementRepository.getTotalStockByBookCopyId(bookCopyId);
              return new LowStockResponse(
                  bookCopy.getId(),
                  bookCopy.getBook().getId(),
                  bookCopy.getBook().getTitle(),
                  bookCopy.getFormat(),
                  bookCopy.getIsbn(),
                  currentStock);
            })
        .toList();
  }
}
