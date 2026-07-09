package com.hei.school.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.hei.school.dto.response.StockMovementResponse;
import com.hei.school.entity.*;
import com.hei.school.entity.enums.BookCopyFormat;
import com.hei.school.entity.enums.BookCopyStatus;
import com.hei.school.entity.enums.MovementReason;
import com.hei.school.entity.enums.MovementType;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class StockMovementMapperTest {

  private final StockMovementMapper mapper = new StockMovementMapper();

  @Test
  void toEntity() {
    BookCopy bookCopy = BookCopy.builder().id(UUID.randomUUID()).build();
    Arrival arrival = new Arrival();
    arrival.setId(UUID.randomUUID());
    SaleItem saleItem = new SaleItem();
    saleItem.setId(UUID.randomUUID());
    Instant now = Instant.now();

    StockMovement sm = mapper.toEntity(10, MovementType.IN, MovementReason.ARRIVAL,
        bookCopy, arrival, saleItem, now);

    assertThat(sm.getQuantity()).isEqualTo(10);
    assertThat(sm.getMovementType()).isEqualTo(MovementType.IN);
    assertThat(sm.getReason()).isEqualTo(MovementReason.ARRIVAL);
    assertThat(sm.getBookCopy().getId()).isEqualTo(bookCopy.getId());
    assertThat(sm.getArrival().getId()).isEqualTo(arrival.getId());
    assertThat(sm.getSaleItem().getId()).isEqualTo(saleItem.getId());
    assertThat(sm.getMovementDate()).isEqualTo(now);
  }

  @Test
  void toResponse() {
    UUID movementId = UUID.randomUUID();
    UUID bookCopyId = UUID.randomUUID();
    UUID bookId = UUID.randomUUID();
    UUID arrivalId = UUID.randomUUID();
    Instant now = Instant.now();

    Book book = new Book();
    book.setId(bookId);
    book.setTitle("Test Book");

    BookCopy bookCopy = BookCopy.builder()
        .id(bookCopyId)
        .book(book)
        .isbn("978-1234567890")
        .status(BookCopyStatus.AVAILABLE)
        .format(BookCopyFormat.PHYSICAL)
        .sellingPrice(BigDecimal.valueOf(20.00))
        .build();

    Arrival arrival = new Arrival();
    arrival.setId(arrivalId);

    StockMovement sm = new StockMovement();
    sm.setId(movementId);
    sm.setQuantity(5);
    sm.setMovementType(MovementType.OUT);
    sm.setReason(MovementReason.SALE);
    sm.setBookCopy(bookCopy);
    sm.setArrival(arrival);
    sm.setSaleItem(null);
    sm.setMovementDate(now);

    StockMovementResponse response = mapper.toResponse(sm);

    assertThat(response.id()).isEqualTo(movementId);
    assertThat(response.quantity()).isEqualTo(5);
    assertThat(response.movementType()).isEqualTo(MovementType.OUT);
    assertThat(response.reason()).isEqualTo(MovementReason.SALE);
    assertThat(response.bookCopyId()).isEqualTo(bookCopyId);
    assertThat(response.bookTitle()).isEqualTo("Test Book");
    assertThat(response.bookCopyIsbn()).isEqualTo("978-1234567890");
    assertThat(response.arrivalId()).isEqualTo(arrivalId);
    assertThat(response.saleItemId()).isNull();
    assertThat(response.movementDate()).isEqualTo(now);
  }

  @Test
  void toResponse_WithNullArrivalAndSaleItem() {
    StockMovement sm = new StockMovement();
    sm.setId(UUID.randomUUID());
    sm.setQuantity(1);
    sm.setMovementType(MovementType.IN);
    sm.setReason(MovementReason.ARRIVAL);
    sm.setBookCopy(BookCopy.builder()
        .id(UUID.randomUUID())
        .book(new Book())
        .build());
    sm.setMovementDate(Instant.now());

    StockMovementResponse response = mapper.toResponse(sm);

    assertThat(response.arrivalId()).isNull();
    assertThat(response.saleItemId()).isNull();
  }
}
