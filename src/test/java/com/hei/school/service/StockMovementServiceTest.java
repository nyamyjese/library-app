package com.hei.school.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import com.hei.school.dto.response.BookStockResponse;
import com.hei.school.dto.response.BookTotalStockResponse;
import com.hei.school.dto.response.StockMovementResponse;
import com.hei.school.entity.Arrival;
import com.hei.school.entity.Book;
import com.hei.school.entity.BookCopy;
import com.hei.school.entity.Library;
import com.hei.school.entity.StockMovement;
import com.hei.school.entity.enums.BookCopyFormat;
import com.hei.school.entity.enums.BookCopyStatus;
import com.hei.school.entity.enums.MovementReason;
import com.hei.school.entity.enums.MovementType;
import com.hei.school.exception.ForbiddenException;
import com.hei.school.exception.NotFoundException;
import com.hei.school.mapper.StockMovementMapper;
import com.hei.school.repository.ArrivalRepository;
import com.hei.school.repository.BookCopyRepository;
import com.hei.school.repository.BookRepository;
import com.hei.school.repository.SaleItemRepository;
import com.hei.school.repository.StockMovementRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StockMovementServiceTest {

  @Mock private StockMovementRepository stockMovementRepository;
  @Mock private BookCopyRepository bookCopyRepository;
  @Mock private ArrivalRepository arrivalRepository;
  @Mock private SaleItemRepository saleItemRepository;
  @Mock private BookRepository bookRepository;
  @Mock private StockMovementMapper stockMovementMapper;

  @InjectMocks private StockMovementService stockMovementService;

  private final UUID stockMovementId = UUID.randomUUID();
  private final UUID bookCopyId = UUID.randomUUID();
  private final UUID bookId = UUID.randomUUID();
  private final UUID libraryId = UUID.randomUUID();
  private final UUID arrivalId = UUID.randomUUID();

  private Book book;
  private Library library;
  private BookCopy bookCopy;
  private Arrival arrival;
  private StockMovement stockMovement;
  private StockMovementResponse stockMovementResponse;

  @BeforeEach
  void setUp() {
    book = new Book();
    book.setId(bookId);
    book.setTitle("Test Book");

    library = new Library();
    library.setId(libraryId);
    library.setName("Test Library");

    bookCopy =
        BookCopy.builder()
            .id(bookCopyId)
            .book(book)
            .library(library)
            .format(BookCopyFormat.PHYSICAL)
            .isbn("9781234567890")
            .sellingPrice(new BigDecimal("29.99"))
            .status(BookCopyStatus.AVAILABLE)
            .build();

    arrival = new Arrival();
    arrival.setId(arrivalId);
    arrival.setBookCopy(bookCopy);
    arrival.setQuantity(10);
    arrival.setUnitPrice(new BigDecimal("15.00"));
    arrival.setArrivalDate(Instant.parse("2024-01-15T10:00:00Z"));

    stockMovement = new StockMovement();
    stockMovement.setId(stockMovementId);
    stockMovement.setQuantity(10);
    stockMovement.setMovementType(MovementType.IN);
    stockMovement.setReason(MovementReason.ARRIVAL);
    stockMovement.setBookCopy(bookCopy);
    stockMovement.setArrival(arrival);
    stockMovement.setMovementDate(Instant.parse("2024-01-15T10:00:00Z"));

    stockMovementResponse =
        new StockMovementResponse(
            stockMovementId,
            10,
            MovementType.IN,
            MovementReason.ARRIVAL,
            bookCopyId,
            "Test Book",
            "9781234567890",
            arrivalId,
            null,
            Instant.parse("2024-01-15T10:00:00Z"));
  }

  @Test
  void getAll_NoFilters() {
    when(stockMovementRepository.findAll()).thenReturn(List.of(stockMovement));
    when(stockMovementMapper.toResponse(stockMovement)).thenReturn(stockMovementResponse);

    List<StockMovementResponse> result = stockMovementService.getAll(null, null, null, null, null);

    assertThat(result).hasSize(1).contains(stockMovementResponse);
  }

  @Test
  void getAll_WithBookCopyIdFilter() {
    when(stockMovementRepository.findByBookCopyId(bookCopyId)).thenReturn(List.of(stockMovement));
    when(stockMovementMapper.toResponse(stockMovement)).thenReturn(stockMovementResponse);

    List<StockMovementResponse> result =
        stockMovementService.getAll(bookCopyId, null, null, null, null);

    assertThat(result).hasSize(1).contains(stockMovementResponse);
    verify(stockMovementRepository, times(1)).findByBookCopyId(bookCopyId);
  }

  @Test
  void getAll_WithMovementTypeFilter() {
    when(stockMovementRepository.findByMovementType(MovementType.IN))
        .thenReturn(List.of(stockMovement));
    when(stockMovementMapper.toResponse(stockMovement)).thenReturn(stockMovementResponse);

    List<StockMovementResponse> result =
        stockMovementService.getAll(null, MovementType.IN, null, null, null);

    assertThat(result).hasSize(1).contains(stockMovementResponse);
    verify(stockMovementRepository, times(1)).findByMovementType(MovementType.IN);
  }

  @Test
  void getAll_WithReasonFilter() {
    when(stockMovementRepository.findByReason(MovementReason.ARRIVAL))
        .thenReturn(List.of(stockMovement));
    when(stockMovementMapper.toResponse(stockMovement)).thenReturn(stockMovementResponse);

    List<StockMovementResponse> result =
        stockMovementService.getAll(null, null, MovementReason.ARRIVAL, null, null);

    assertThat(result).hasSize(1).contains(stockMovementResponse);
    verify(stockMovementRepository, times(1)).findByReason(MovementReason.ARRIVAL);
  }

  @Test
  void getAll_WithDateRangeFilter() {
    Instant from = Instant.parse("2024-01-01T00:00:00Z");
    Instant to = Instant.parse("2024-01-31T23:59:59Z");

    when(stockMovementRepository.findByMovementDateBetween(from, to))
        .thenReturn(List.of(stockMovement));
    when(stockMovementMapper.toResponse(stockMovement)).thenReturn(stockMovementResponse);

    List<StockMovementResponse> result = stockMovementService.getAll(null, null, null, from, to);

    assertThat(result).hasSize(1).contains(stockMovementResponse);
    verify(stockMovementRepository, times(1)).findByMovementDateBetween(from, to);
  }

  @Test
  void getById_Success() {
    when(stockMovementRepository.findById(stockMovementId)).thenReturn(Optional.of(stockMovement));
    when(stockMovementMapper.toResponse(stockMovement)).thenReturn(stockMovementResponse);

    StockMovementResponse result = stockMovementService.getById(stockMovementId);

    assertThat(result).isEqualTo(stockMovementResponse);
  }

  @Test
  void getById_NotFound() {
    when(stockMovementRepository.findById(stockMovementId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> stockMovementService.getById(stockMovementId))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("StockMovement with id");
  }

  @Test
  void update_shouldThrowForbiddenException() {
    assertThatThrownBy(() -> stockMovementService.update(stockMovementId))
        .isInstanceOf(ForbiddenException.class)
        .hasMessageContaining("cannot be updated")
        .hasMessageContaining("immutable");
  }

  @Test
  void delete_shouldThrowForbiddenException() {
    assertThatThrownBy(() -> stockMovementService.delete(stockMovementId))
        .isInstanceOf(ForbiddenException.class)
        .hasMessageContaining("cannot be deleted")
        .hasMessageContaining("immutable");
  }

  @Test
  void getStockByBookCopyId_Success() {
    when(bookCopyRepository.findById(bookCopyId)).thenReturn(Optional.of(bookCopy));
    when(stockMovementRepository.sumInQuantityByBookCopyId(bookCopyId)).thenReturn(50);
    when(stockMovementRepository.sumOutQuantityByBookCopyId(bookCopyId)).thenReturn(20);

    BookStockResponse result = stockMovementService.getStockByBookCopyId(bookCopyId);

    assertThat(result).isNotNull();
    assertThat(result.totalIn()).isEqualTo(50);
    assertThat(result.totalOut()).isEqualTo(20);
    assertThat(result.currentStock()).isEqualTo(30);
  }

  @Test
  void getStockByBookCopyId_BookCopyNotFound() {
    when(bookCopyRepository.findById(bookCopyId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> stockMovementService.getStockByBookCopyId(bookCopyId))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("BookCopy with id");
  }

  @Test
  void getTotalStockByBookId_Success() {
    when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
    when(stockMovementRepository.getTotalStockByBookId(bookId)).thenReturn(100);

    BookTotalStockResponse result = stockMovementService.getTotalStockByBookId(bookId);

    assertThat(result).isNotNull();
    assertThat(result.bookId()).isEqualTo(bookId);
    assertThat(result.bookTitle()).isEqualTo("Test Book");
    assertThat(result.totalStock()).isEqualTo(100);
  }

  @Test
  void getTotalStockByBookId_BookNotFound() {
    when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> stockMovementService.getTotalStockByBookId(bookId))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("Book with id");
  }
}
