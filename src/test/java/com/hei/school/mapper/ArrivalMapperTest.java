package com.hei.school.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.hei.school.dto.request.CreateArrivalRequest;
import com.hei.school.dto.response.ArrivalResponse;
import com.hei.school.entity.Arrival;
import com.hei.school.entity.Book;
import com.hei.school.entity.BookCopy;
import com.hei.school.entity.Library;
import com.hei.school.entity.enums.BookCopyFormat;
import com.hei.school.entity.enums.BookCopyStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ArrivalMapperTest {

  private final ArrivalMapper mapper = new ArrivalMapper();

  private final UUID arrivalId = UUID.randomUUID();
  private final UUID bookCopyId = UUID.randomUUID();
  private final UUID bookId = UUID.randomUUID();
  private final UUID libraryId = UUID.randomUUID();

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

  @Test
  void toEntity_shouldMapRequestToEntity() {
    Instant arrivalDate = Instant.parse("2024-01-15T10:00:00Z");
    CreateArrivalRequest request =
        new CreateArrivalRequest(bookCopyId, 10, new BigDecimal("15.00"), arrivalDate);
    BookCopy bookCopy = createBookCopy();

    Arrival arrival = mapper.toEntity(request, bookCopy);

    assertThat(arrival.getBookCopy()).isEqualTo(bookCopy);
    assertThat(arrival.getQuantity()).isEqualTo(10);
    assertThat(arrival.getUnitPrice()).isEqualByComparingTo(new BigDecimal("15.00"));
    assertThat(arrival.getArrivalDate()).isEqualTo(arrivalDate);
  }

  @Test
  void toResponse_shouldMapAllFields() {
    Arrival arrival = createArrival();

    ArrivalResponse response = mapper.toResponse(arrival);

    assertThat(response.id()).isEqualTo(arrivalId);
    assertThat(response.bookCopyId()).isEqualTo(bookCopyId);
    assertThat(response.bookTitle()).isEqualTo("Test Book");
    assertThat(response.bookCopyIsbn()).isEqualTo("9781234567890");
    assertThat(response.quantity()).isEqualTo(10);
    assertThat(response.unitPrice()).isEqualByComparingTo(new BigDecimal("15.00"));
    assertThat(response.totalPrice()).isEqualByComparingTo(new BigDecimal("150.00"));
    assertThat(response.arrivalDate()).isEqualTo(Instant.parse("2024-01-15T10:00:00Z"));
  }
}
