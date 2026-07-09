package com.hei.school.service;

import com.hei.school.dto.response.BookCopyStockResponse;
import com.hei.school.dto.response.BookStockResponse;
import com.hei.school.dto.response.LowStockResponse;
import com.hei.school.entity.BookCopy;
import com.hei.school.entity.enums.BookCopyStatus;
import com.hei.school.repository.BookCopyRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class StockService {

  private final BookCopyRepository bookCopyRepository;
  private static final long DEFAULT_LOW_STOCK_THRESHOLD = 3;

  public BookStockResponse getStockByBook(UUID bookId) {
    List<BookCopy> copies = bookCopyRepository.findAllByBook_Id(bookId);
    if (copies.isEmpty()) {
      return new BookStockResponse(bookId, null, 0, 0, 0, 0, 0);
    }
    String bookTitle = copies.get(0).getBook().getTitle();
    return new BookStockResponse(
        bookId,
        bookTitle,
        copies.size(),
        countByStatus(copies, BookCopyStatus.AVAILABLE),
        countByStatus(copies, BookCopyStatus.DAMAGED),
        countByStatus(copies, BookCopyStatus.LOST),
        countByStatus(copies, BookCopyStatus.RESOLVED));
  }

  public List<BookStockResponse> getAllBooksStock() {
    List<UUID> bookIds = bookCopyRepository.findDistinctBookIds();
    List<BookStockResponse> result = new ArrayList<>();
    for (UUID bookId : bookIds) {
      result.add(getStockByBook(bookId));
    }
    return result;
  }

  public BookCopyStockResponse getStockByIsbn(String isbn) {
    List<BookCopy> copies = bookCopyRepository.findAllByIsbn(isbn);
    if (copies.isEmpty()) {
      return new BookCopyStockResponse(null, null, isbn, 0, 0, 0, 0);
    }
    BookCopy first = copies.get(0);
    return new BookCopyStockResponse(
        first.getId(),
        first.getBook().getTitle(),
        isbn,
        countByStatus(copies, BookCopyStatus.AVAILABLE),
        countByStatus(copies, BookCopyStatus.DAMAGED),
        countByStatus(copies, BookCopyStatus.LOST),
        countByStatus(copies, BookCopyStatus.RESOLVED));
  }

  public List<LowStockResponse> getLowStockBooks(long threshold) {
    List<UUID> bookIds = bookCopyRepository.findBookIdsWithLowStock(threshold);
    List<LowStockResponse> result = new ArrayList<>();
    for (UUID bookId : bookIds) {
      List<BookCopy> copies =
          bookCopyRepository.findAllByBook_IdAndStatus(bookId, BookCopyStatus.AVAILABLE);
      if (!copies.isEmpty()) {
        result.add(new LowStockResponse(bookId, copies.get(0).getBook().getTitle(), copies.size()));
      }
    }
    return result;
  }

  public List<LowStockResponse> getLowStockBooks() {
    return getLowStockBooks(DEFAULT_LOW_STOCK_THRESHOLD);
  }

  private long countByStatus(List<BookCopy> copies, BookCopyStatus status) {
    return copies.stream().filter(c -> c.getStatus() == status).count();
  }
}
