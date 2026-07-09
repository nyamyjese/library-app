package com.hei.school.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.hei.school.dto.request.CreateArrivalRequest;
import com.hei.school.dto.request.UpdateArrivalRequest;
import com.hei.school.dto.response.ArrivalResponse;
import com.hei.school.entity.Arrival;
import com.hei.school.entity.Book;
import com.hei.school.entity.BookCopy;
import com.hei.school.entity.Library;
import com.hei.school.entity.enums.BookCopyFormat;
import com.hei.school.entity.enums.BookCopyStatus;
import com.hei.school.exception.NotFoundException;
import com.hei.school.mapper.ArrivalMapper;
import com.hei.school.repository.ArrivalRepository;
import com.hei.school.repository.BookCopyRepository;
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
class ArrivalServiceTest {

  @Mock private ArrivalRepository arrivalRepository;
  @Mock private BookCopyRepository bookCopyRepository;
  @Mock private StockMovementRepository stockMovementRepository;
  @Mock private ArrivalMapper arrivalMapper;

  @InjectMocks private ArrivalService arrivalService;

  private final UUID arrivalId = UUID.randomUUID();
  private final UUID bookCopyId = UUID.randomUUID();
  private final UUID bookId = UUID.randomUUID();
  private final UUID libraryId = UUID.randomUUID();

  private Book book;
  private Library library;
  private BookCopy bookCopy;
  private Arrival arrival;
  private ArrivalResponse arrivalResponse;

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

    arrivalResponse =
        new ArrivalResponse(
            arrivalId,
            bookCopyId,
            "Test Book",
            "9781234567890",
            10,
            new BigDecimal("15.00"),
            new BigDecimal("150.00"),
            Instant.parse("2024-01-15T10:00:00Z"));
  }

  @Test
  void create_Success() {
    CreateArrivalRequest request =
        new CreateArrivalRequest(
            bookCopyId, 10, new BigDecimal("15.00"), Instant.parse("2024-01-15T10:00:00Z"));

    when(bookCopyRepository.findById(bookCopyId)).thenReturn(Optional.of(bookCopy));
    when(arrivalMapper.toEntity(request, bookCopy)).thenReturn(arrival);
    when(arrivalRepository.save(any())).thenReturn(arrival);
    when(arrivalMapper.toResponse(arrival)).thenReturn(arrivalResponse);

    ArrivalResponse result = arrivalService.create(request);

    assertThat(result).isEqualTo(arrivalResponse);
    verify(bookCopyRepository, times(1)).findById(bookCopyId);
    verify(arrivalRepository, times(1)).save(any());
    verify(stockMovementRepository, times(1)).save(any());
  }

  @Test
  void create_BookCopyNotFound() {
    CreateArrivalRequest request =
        new CreateArrivalRequest(
            bookCopyId, 10, new BigDecimal("15.00"), Instant.parse("2024-01-15T10:00:00Z"));

    when(bookCopyRepository.findById(bookCopyId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> arrivalService.create(request))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("BookCopy with id");
  }

  @Test
  void getAll_NoFilters() {
    when(arrivalRepository.findAll()).thenReturn(List.of(arrival));
    when(arrivalMapper.toResponse(arrival)).thenReturn(arrivalResponse);

    List<ArrivalResponse> result = arrivalService.getAll(null, null, null);

    assertThat(result).hasSize(1).contains(arrivalResponse);
  }

  @Test
  void getAll_WithBookCopyIdFilter() {
    when(arrivalRepository.findByBookCopyId(bookCopyId)).thenReturn(List.of(arrival));
    when(arrivalMapper.toResponse(arrival)).thenReturn(arrivalResponse);

    List<ArrivalResponse> result = arrivalService.getAll(bookCopyId, null, null);

    assertThat(result).hasSize(1).contains(arrivalResponse);
    verify(arrivalRepository, times(1)).findByBookCopyId(bookCopyId);
  }

  @Test
  void getAll_WithDateRangeFilter() {
    Instant from = Instant.parse("2024-01-01T00:00:00Z");
    Instant to = Instant.parse("2024-01-31T23:59:59Z");

    when(arrivalRepository.findByArrivalDateBetween(from, to)).thenReturn(List.of(arrival));
    when(arrivalMapper.toResponse(arrival)).thenReturn(arrivalResponse);

    List<ArrivalResponse> result = arrivalService.getAll(null, from, to);

    assertThat(result).hasSize(1).contains(arrivalResponse);
    verify(arrivalRepository, times(1)).findByArrivalDateBetween(from, to);
  }

  @Test
  void getById_Success() {
    when(arrivalRepository.findById(arrivalId)).thenReturn(Optional.of(arrival));
    when(arrivalMapper.toResponse(arrival)).thenReturn(arrivalResponse);

    ArrivalResponse result = arrivalService.getById(arrivalId);

    assertThat(result).isEqualTo(arrivalResponse);
  }

  @Test
  void getById_NotFound() {
    when(arrivalRepository.findById(arrivalId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> arrivalService.getById(arrivalId))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("Arrival with id");
  }

  @Test
  void update_Success() {
    UpdateArrivalRequest updateRequest =
        new UpdateArrivalRequest(
            20, new BigDecimal("16.00"), Instant.parse("2024-01-20T10:00:00Z"));

    Arrival updatedArrival = new Arrival();
    updatedArrival.setId(arrivalId);
    updatedArrival.setBookCopy(bookCopy);
    updatedArrival.setQuantity(20);
    updatedArrival.setUnitPrice(new BigDecimal("16.00"));
    updatedArrival.setArrivalDate(Instant.parse("2024-01-20T10:00:00Z"));

    ArrivalResponse updatedResponse =
        new ArrivalResponse(
            arrivalId,
            bookCopyId,
            "Test Book",
            "9781234567890",
            20,
            new BigDecimal("16.00"),
            new BigDecimal("320.00"),
            Instant.parse("2024-01-20T10:00:00Z"));

    when(arrivalRepository.findById(arrivalId)).thenReturn(Optional.of(arrival));
    when(arrivalRepository.save(any())).thenReturn(updatedArrival);
    when(arrivalMapper.toResponse(updatedArrival)).thenReturn(updatedResponse);

    ArrivalResponse result = arrivalService.update(arrivalId, updateRequest);

    assertThat(result).isEqualTo(updatedResponse);
    assertThat(result.quantity()).isEqualTo(20);
  }

  @Test
  void update_NotFound() {
    UpdateArrivalRequest updateRequest =
        new UpdateArrivalRequest(
            20, new BigDecimal("16.00"), Instant.parse("2024-01-20T10:00:00Z"));

    when(arrivalRepository.findById(arrivalId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> arrivalService.update(arrivalId, updateRequest))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("Arrival with id");
  }

  @Test
  void delete_Success() {
    when(arrivalRepository.findById(arrivalId)).thenReturn(Optional.of(arrival));

    arrivalService.delete(arrivalId);

    verify(arrivalRepository, times(1)).deleteById(arrivalId);
  }

  @Test
  void delete_NotFound() {
    when(arrivalRepository.findById(arrivalId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> arrivalService.delete(arrivalId))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("Arrival with id");
  }
}
