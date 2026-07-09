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

  private final UUID stockMovementId = UUID.randomUUID();
  private final UUID bookCopyId = UUID.randomUUID();
  private final UUID bookId = UUID.randomUUID();
  private final UUID libraryId = UUID.randomUUID();
  private final UUID arrivalId = UUID.randomUUID();
  private final UUID saleItemId = UUID.randomUUID();

  private Book createBook() {
    Book book = new Book();
    book.setId(bookId);
    book.setTitle("Test Book");
    return book;
  }

  private Library createLibrary() {
    Library library = new Library();
    library.setId(libraryId);
    library.setName("Test Library");
    return library;
  }

  private BookCopy createBookCopy() {
    return BookCopy.builder()
        .id(bookCopyId)
        .book(createBook())
        .library(createLibrary())
        .format(BookCopyFormat.PHYSICAL)
        .isbn("9781234567890")
        .sellingPrice(new BigDecimal("29.99"))
        .status(BookCopyStatus.AVAILABLE)
        .build();
  }

  private Arrival createArrival() {
    Arrival arrival = new Arrival();
    arrival.setId(arrivalId);
    arrival.setBookCopy(createBookCopy());
    arrival.setQuantity(10);
    arrival.setUnitPrice(new BigDecimal("15.00"));
    arrival.setArrivalDate(Instant.parse("2024-01-15T10:00:00Z"));
    return arrival;
  }

  private SaleItem createSaleItem() {
    SaleItem saleItem = new SaleItem();
    saleItem.setId(saleItemId);
    saleItem.setBookCopy(createBookCopy());
    saleItem.setQuantity(2);
    saleItem.setUnitPrice(new BigDecimal("29.99"));
    return saleItem;
  }

  private StockMovement createStockMovement() {
    StockMovement sm = new StockMovement();
    sm.setId(stockMovementId);
    sm.setQuantity(5);
    sm.setMovementType(MovementType.IN);
    sm.setReason(MovementReason.ARRIVAL);
    sm.setBookCopy(createBookCopy());
    sm.setArrival(createArrival());
    sm.setSaleItem(null);
    sm.setMovementDate(Instant.parse("2024-01-15T10:00:00Z"));
    return sm;
  }

  @Test
  void toEntity_shouldMapAllFields() {
    BookCopy bookCopy = createBookCopy();
    Arrival arrival = createArrival();
    SaleItem saleItem = createSaleItem();
    Instant movementDate = Instant.parse("2024-01-15T10:00:00Z");

    StockMovement sm =
        mapper.toEntity(
            5, MovementType.IN, MovementReason.ARRIVAL, bookCopy, arrival, saleItem, movementDate);

    assertThat(sm.getQuantity()).isEqualTo(5);
    assertThat(sm.getMovementType()).isEqualTo(MovementType.IN);
    assertThat(sm.getReason()).isEqualTo(MovementReason.ARRIVAL);
    assertThat(sm.getBookCopy()).isEqualTo(bookCopy);
    assertThat(sm.getArrival()).isEqualTo(arrival);
    assertThat(sm.getSaleItem()).isEqualTo(saleItem);
    assertThat(sm.getMovementDate()).isEqualTo(movementDate);
  }

  @Test
  void toResponse_shouldMapAllFields() {
    StockMovement sm = createStockMovement();

    StockMovementResponse response = mapper.toResponse(sm);

    assertThat(response.id()).isEqualTo(stockMovementId);
    assertThat(response.quantity()).isEqualTo(5);
    assertThat(response.movementType()).isEqualTo(MovementType.IN);
    assertThat(response.reason()).isEqualTo(MovementReason.ARRIVAL);
    assertThat(response.bookCopyId()).isEqualTo(bookCopyId);
    assertThat(response.bookTitle()).isEqualTo("Test Book");
    assertThat(response.bookCopyIsbn()).isEqualTo("9781234567890");
    assertThat(response.arrivalId()).isEqualTo(arrivalId);
    assertThat(response.saleItemId()).isNull();
    assertThat(response.movementDate()).isEqualTo(Instant.parse("2024-01-15T10:00:00Z"));
  }

  @Test
  void toResponse_shouldHandleNullArrivalAndSaleItem() {
    StockMovement sm = new StockMovement();
    sm.setId(stockMovementId);
    sm.setQuantity(3);
    sm.setMovementType(MovementType.OUT);
    sm.setReason(MovementReason.SALE);
    sm.setBookCopy(createBookCopy());
    sm.setArrival(null);
    sm.setSaleItem(null);
    sm.setMovementDate(Instant.parse("2024-02-01T12:00:00Z"));

    StockMovementResponse response = mapper.toResponse(sm);

    assertThat(response.id()).isEqualTo(stockMovementId);
    assertThat(response.quantity()).isEqualTo(3);
    assertThat(response.movementType()).isEqualTo(MovementType.OUT);
    assertThat(response.reason()).isEqualTo(MovementReason.SALE);
    assertThat(response.bookCopyId()).isEqualTo(bookCopyId);
    assertThat(response.bookTitle()).isEqualTo("Test Book");
    assertThat(response.arrivalId()).isNull();
    assertThat(response.saleItemId()).isNull();
  }
}
