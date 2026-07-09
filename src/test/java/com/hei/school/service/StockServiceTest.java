package com.hei.school.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.hei.school.dto.response.BookCopyStockResponse;
import com.hei.school.dto.response.BookStockStatusResponse;
import com.hei.school.dto.response.LowStockResponse;
import com.hei.school.entity.Book;
import com.hei.school.entity.BookCopy;
import com.hei.school.entity.Library;
import com.hei.school.entity.enums.BookCopyFormat;
import com.hei.school.entity.enums.BookCopyStatus;
import com.hei.school.repository.BookCopyRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

  @Mock private BookCopyRepository bookCopyRepository;

  @InjectMocks private StockService stockService;

  private final UUID bookId = UUID.randomUUID();
  private final UUID copyId = UUID.randomUUID();
  private Book book;
  private BookCopy availableCopy;
  private BookCopy damagedCopy;

  @BeforeEach
  void setUp() {
    Library library = new Library();
    library.setId(UUID.randomUUID());

    book = new Book();
    book.setId(bookId);
    book.setTitle("Test Book");

    availableCopy =
        BookCopy.builder()
            .id(copyId)
            .format(BookCopyFormat.PHYSICAL)
            .isbn("978-1234567890")
            .sellingPrice(BigDecimal.valueOf(20.00))
            .status(BookCopyStatus.AVAILABLE)
            .book(book)
            .library(library)
            .build();

    damagedCopy =
        BookCopy.builder()
            .id(UUID.randomUUID())
            .format(BookCopyFormat.PHYSICAL)
            .isbn("978-1234567890")
            .sellingPrice(BigDecimal.valueOf(15.00))
            .status(BookCopyStatus.DAMAGED)
            .book(book)
            .library(library)
            .build();
  }

  @Test
  void getStockByBook_WithCopies() {
    when(bookCopyRepository.findByBookId(bookId)).thenReturn(List.of(availableCopy, damagedCopy));
    BookStockStatusResponse result = stockService.getStockByBook(bookId);
    assertThat(result.bookId()).isEqualTo(bookId);
    assertThat(result.totalCopies()).isEqualTo(2);
    assertThat(result.availableCopies()).isEqualTo(1);
    assertThat(result.damagedCopies()).isEqualTo(1);
  }

  @Test
  void getStockByBook_Empty() {
    when(bookCopyRepository.findByBookId(bookId)).thenReturn(List.of());
    BookStockStatusResponse result = stockService.getStockByBook(bookId);
    assertThat(result.totalCopies()).isZero();
  }

  @Test
  void getAllBooksStock() {
    when(bookCopyRepository.findDistinctBookIds()).thenReturn(List.of(bookId));
    when(bookCopyRepository.findByBookId(bookId)).thenReturn(List.of(availableCopy));
    List<BookStockStatusResponse> result = stockService.getAllBooksStock();
    assertThat(result).hasSize(1);
  }

  @Test
  void getAllBooksStock_Empty() {
    when(bookCopyRepository.findDistinctBookIds()).thenReturn(List.of());
    List<BookStockStatusResponse> result = stockService.getAllBooksStock();
    assertThat(result).isEmpty();
  }

  @Test
  void getStockByIsbn_WithCopies() {
    when(bookCopyRepository.findAllByIsbn("978-1234567890"))
        .thenReturn(List.of(availableCopy, damagedCopy));
    BookCopyStockResponse result = stockService.getStockByIsbn("978-1234567890");
    assertThat(result.isbn()).isEqualTo("978-1234567890");
    assertThat(result.availableCopies()).isEqualTo(1);
    assertThat(result.damagedCopies()).isEqualTo(1);
  }

  @Test
  void getStockByIsbn_Empty() {
    when(bookCopyRepository.findAllByIsbn("978-0000000000")).thenReturn(List.of());
    BookCopyStockResponse result = stockService.getStockByIsbn("978-0000000000");
    assertThat(result.isbn()).isEqualTo("978-0000000000");
    assertThat(result.bookCopyId()).isNull();
  }

  @Test
  void getLowStockBooks_WithThreshold() {
    when(bookCopyRepository.findBookIdsWithLowStock(3)).thenReturn(List.of(bookId));
    when(bookCopyRepository.findAllByBook_IdAndStatus(bookId, BookCopyStatus.AVAILABLE))
        .thenReturn(List.of(availableCopy));
    List<LowStockResponse> result = stockService.getLowStockBooks(3);
    assertThat(result).hasSize(1);
    assertThat(result.get(0).bookId()).isEqualTo(bookId);
    assertThat(result.get(0).availableCopies()).isEqualTo(1);
  }

  @Test
  void getLowStockBooks_WithThreshold_EmptyAvailable() {
    when(bookCopyRepository.findBookIdsWithLowStock(3)).thenReturn(List.of(bookId));
    when(bookCopyRepository.findAllByBook_IdAndStatus(bookId, BookCopyStatus.AVAILABLE))
        .thenReturn(List.of());
    List<LowStockResponse> result = stockService.getLowStockBooks(3);
    assertThat(result).isEmpty();
  }

  @Test
  void getLowStockBooks_Default() {
    when(bookCopyRepository.findBookIdsWithLowStock(3)).thenReturn(List.of(bookId));
    when(bookCopyRepository.findAllByBook_IdAndStatus(bookId, BookCopyStatus.AVAILABLE))
        .thenReturn(List.of(availableCopy));
    List<LowStockResponse> result = stockService.getLowStockBooks();
    assertThat(result).hasSize(1);
  }
}
