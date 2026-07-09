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
  private Arrival arrival;
  private BookCopy bookCopy;
  private ArrivalResponse response;

  @BeforeEach
  void setUp() {
    Library library = new Library();
    library.setId(UUID.randomUUID());

    Book book = new Book();
    book.setId(UUID.randomUUID());
    book.setTitle("Test Book");

    bookCopy =
        BookCopy.builder()
            .id(bookCopyId)
            .format(BookCopyFormat.PHYSICAL)
            .isbn("978-1234567890")
            .sellingPrice(BigDecimal.valueOf(20.00))
            .status(BookCopyStatus.AVAILABLE)
            .book(book)
            .library(library)
            .build();

    arrival = new Arrival();
    arrival.setId(arrivalId);
    arrival.setQuantity(10);
    arrival.setUnitPrice(BigDecimal.valueOf(15.00));
    arrival.setArrivalDate(Instant.now());
    arrival.setBookCopy(bookCopy);

    response =
        new ArrivalResponse(
            arrivalId,
            bookCopyId,
            "Test Book",
            "978-1234567890",
            10,
            BigDecimal.valueOf(15.00),
            BigDecimal.valueOf(150.00),
            arrival.getArrivalDate());
  }

  @Test
  void create_Success() {
    var request =
        new CreateArrivalRequest(bookCopyId, 10, BigDecimal.valueOf(15.00), Instant.now());
    when(bookCopyRepository.findById(bookCopyId)).thenReturn(Optional.of(bookCopy));
    when(arrivalMapper.toEntity(request, bookCopy)).thenReturn(arrival);
    when(arrivalRepository.save(arrival)).thenReturn(arrival);
    when(arrivalMapper.toResponse(arrival)).thenReturn(response);

    var result = arrivalService.create(request);

    assertThat(result.quantity()).isEqualTo(10);
    verify(stockMovementRepository).save(any());
  }

  @Test
  void create_BookCopyNotFound() {
    var request =
        new CreateArrivalRequest(bookCopyId, 10, BigDecimal.valueOf(15.00), Instant.now());
    when(bookCopyRepository.findById(bookCopyId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> arrivalService.create(request)).isInstanceOf(NotFoundException.class);
  }

  @Test
  void update_Success() {
    var updateRequest = new UpdateArrivalRequest(20, BigDecimal.valueOf(12.00), Instant.now());
    when(arrivalRepository.findById(arrivalId)).thenReturn(Optional.of(arrival));
    when(arrivalRepository.save(arrival)).thenReturn(arrival);
    when(arrivalMapper.toResponse(arrival)).thenReturn(response);

    var result = arrivalService.update(arrivalId, updateRequest);

    assertThat(result).isNotNull();
  }

  @Test
  void update_NotFound() {
    var updateRequest = new UpdateArrivalRequest(20, BigDecimal.valueOf(12.00), Instant.now());
    when(arrivalRepository.findById(arrivalId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> arrivalService.update(arrivalId, updateRequest))
        .isInstanceOf(NotFoundException.class);
  }

  @Test
  void getById_Success() {
    when(arrivalRepository.findById(arrivalId)).thenReturn(Optional.of(arrival));
    when(arrivalMapper.toResponse(arrival)).thenReturn(response);

    var result = arrivalService.getById(arrivalId);

    assertThat(result.id()).isEqualTo(arrivalId);
  }

  @Test
  void getById_NotFound() {
    when(arrivalRepository.findById(arrivalId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> arrivalService.getById(arrivalId))
        .isInstanceOf(NotFoundException.class);
  }

  @Test
  void getAll() {
    when(arrivalRepository.findAll()).thenReturn(List.of(arrival));
    when(arrivalMapper.toResponse(arrival)).thenReturn(response);

    var result = arrivalService.getAll(null, null, null);

    assertThat(result).hasSize(1);
  }

  @Test
  void getAll_ByBookCopy() {
    when(arrivalRepository.findByBookCopyId(bookCopyId)).thenReturn(List.of(arrival));
    when(arrivalMapper.toResponse(arrival)).thenReturn(response);

    var result = arrivalService.getAll(bookCopyId, null, null);

    assertThat(result).hasSize(1);
  }

  @Test
  void delete_Success() {
    when(arrivalRepository.findById(arrivalId)).thenReturn(Optional.of(arrival));

    arrivalService.delete(arrivalId);

    verify(arrivalRepository).deleteById(arrivalId);
  }

  @Test
  void delete_NotFound() {
    when(arrivalRepository.findById(arrivalId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> arrivalService.delete(arrivalId))
        .isInstanceOf(NotFoundException.class);
  }
}
