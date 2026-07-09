package com.hei.school.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.hei.school.dto.response.StockMovementResponse;
import com.hei.school.entity.*;
import com.hei.school.entity.enums.BookCopyFormat;
import com.hei.school.entity.enums.BookCopyStatus;
import com.hei.school.entity.enums.MovementReason;
import com.hei.school.entity.enums.MovementType;
import com.hei.school.exception.NotFoundException;
import com.hei.school.mapper.StockMovementMapper;
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
class StockMovementServiceTest {

  @Mock private StockMovementRepository stockMovementRepository;
  @Mock private BookCopyRepository bookCopyRepository;
  @Mock private ArrivalRepository arrivalRepository;
  @Mock private StockMovementMapper stockMovementMapper;

  @InjectMocks private StockMovementService stockMovementService;

  private final UUID movementId = UUID.randomUUID();
  private final UUID bookCopyId = UUID.randomUUID();
  private final UUID arrivalId = UUID.randomUUID();
  private StockMovement movement;
  private StockMovementResponse response;
  private BookCopy bookCopy;
  private Arrival arrival;

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

    movement = new StockMovement();
    movement.setId(movementId);
    movement.setQuantity(10);
    movement.setMovementType(MovementType.IN);
    movement.setReason(MovementReason.ARRIVAL);
    movement.setBookCopy(bookCopy);
    movement.setArrival(arrival);
    movement.setMovementDate(Instant.now());

    response =
        new StockMovementResponse(
            movementId,
            10,
            MovementType.IN,
            MovementReason.ARRIVAL,
            bookCopyId,
            "Test Book",
            "978-1234567890",
            arrivalId,
            null,
            movement.getMovementDate());
  }

  @Test
  void recordArrivalMovement_Success() {
    when(bookCopyRepository.findById(bookCopyId)).thenReturn(Optional.of(bookCopy));
    when(arrivalRepository.findById(arrivalId)).thenReturn(Optional.of(arrival));
    when(stockMovementMapper.toEntity(
            anyInt(),
            eq(MovementType.IN),
            eq(MovementReason.ARRIVAL),
            eq(bookCopy),
            eq(arrival),
            isNull(),
            any(Instant.class)))
        .thenReturn(movement);
    when(stockMovementRepository.save(movement)).thenReturn(movement);
    when(stockMovementMapper.toResponse(movement)).thenReturn(response);

    StockMovementResponse result =
        stockMovementService.recordArrivalMovement(bookCopyId, arrivalId);
    assertThat(result.movementType()).isEqualTo(MovementType.IN);
  }

  @Test
  void recordArrivalMovement_BookCopyNotFound() {
    when(bookCopyRepository.findById(bookCopyId)).thenReturn(Optional.empty());
    assertThatThrownBy(() -> stockMovementService.recordArrivalMovement(bookCopyId, arrivalId))
        .isInstanceOf(NotFoundException.class);
  }

  @Test
  void recordArrivalMovement_ArrivalNotFound() {
    when(bookCopyRepository.findById(bookCopyId)).thenReturn(Optional.of(bookCopy));
    when(arrivalRepository.findById(arrivalId)).thenReturn(Optional.empty());
    assertThatThrownBy(() -> stockMovementService.recordArrivalMovement(bookCopyId, arrivalId))
        .isInstanceOf(NotFoundException.class);
  }

  @Test
  void getById_Success() {
    when(stockMovementRepository.findById(movementId)).thenReturn(Optional.of(movement));
    when(stockMovementMapper.toResponse(movement)).thenReturn(response);
    StockMovementResponse result = stockMovementService.getById(movementId);
    assertThat(result.id()).isEqualTo(movementId);
  }

  @Test
  void getById_NotFound() {
    when(stockMovementRepository.findById(movementId)).thenReturn(Optional.empty());
    assertThatThrownBy(() -> stockMovementService.getById(movementId))
        .isInstanceOf(NotFoundException.class);
  }

  @Test
  void getAll() {
    when(stockMovementRepository.findAll()).thenReturn(List.of(movement));
    when(stockMovementMapper.toResponse(movement)).thenReturn(response);
    List<StockMovementResponse> result = stockMovementService.getAll(null, null, null, null, null);
    assertThat(result).hasSize(1);
  }

  @Test
  void getByBookCopyId() {
    when(stockMovementRepository.findByBookCopyId(bookCopyId)).thenReturn(List.of(movement));
    when(stockMovementMapper.toResponse(movement)).thenReturn(response);
    List<StockMovementResponse> result =
        stockMovementService.getAll(bookCopyId, null, null, null, null);
    assertThat(result).hasSize(1);
  }

  @Test
  void getByMovementType() {
    when(stockMovementRepository.findByMovementType(MovementType.IN)).thenReturn(List.of(movement));
    when(stockMovementMapper.toResponse(movement)).thenReturn(response);
    List<StockMovementResponse> result =
        stockMovementService.getAll(null, MovementType.IN, null, null, null);
    assertThat(result).hasSize(1);
  }

  @Test
  void getByReason() {
    when(stockMovementRepository.findByReason(MovementReason.ARRIVAL))
        .thenReturn(List.of(movement));
    when(stockMovementMapper.toResponse(movement)).thenReturn(response);
    List<StockMovementResponse> result =
        stockMovementService.getAll(null, null, MovementReason.ARRIVAL, null, null);
    assertThat(result).hasSize(1);
  }
}
