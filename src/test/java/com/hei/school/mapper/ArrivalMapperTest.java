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

  @Test
  void toEntity() {
    UUID bookCopyId = UUID.randomUUID();
    BookCopy bookCopy = BookCopy.builder().id(bookCopyId).build();
    Instant now = Instant.now();

    CreateArrivalRequest request = new CreateArrivalRequest(bookCopyId, 10,
        BigDecimal.valueOf(15.00), now);
    Arrival arrival = mapper.toEntity(request, bookCopy);

    assertThat(arrival.getBookCopy().getId()).isEqualTo(bookCopyId);
    assertThat(arrival.getQuantity()).isEqualTo(10);
    assertThat(arrival.getUnitPrice()).isEqualByComparingTo(BigDecimal.valueOf(15.00));
    assertThat(arrival.getArrivalDate()).isEqualTo(now);
  }

  @Test
  void toResponse() {
    UUID arrivalId = UUID.randomUUID();
    UUID bookCopyId = UUID.randomUUID();
    UUID bookId = UUID.randomUUID();
    Instant now = Instant.now();

    Book book = new Book();
    book.setId(bookId);
    book.setTitle("Test Book");

    Library library = new Library();
    library.setId(UUID.randomUUID());

    BookCopy bookCopy = BookCopy.builder()
        .id(bookCopyId)
        .book(book)
        .isbn("978-1234567890")
        .status(BookCopyStatus.AVAILABLE)
        .format(BookCopyFormat.PHYSICAL)
        .sellingPrice(BigDecimal.valueOf(20.00))
        .library(library)
        .build();

    Arrival arrival = new Arrival();
    arrival.setId(arrivalId);
    arrival.setQuantity(10);
    arrival.setUnitPrice(BigDecimal.valueOf(15.00));
    arrival.setArrivalDate(now);
    arrival.setBookCopy(bookCopy);

    ArrivalResponse response = mapper.toResponse(arrival);

    assertThat(response.id()).isEqualTo(arrivalId);
    assertThat(response.bookCopyId()).isEqualTo(bookCopyId);
    assertThat(response.bookTitle()).isEqualTo("Test Book");
    assertThat(response.bookCopyIsbn()).isEqualTo("978-1234567890");
    assertThat(response.quantity()).isEqualTo(10);
    assertThat(response.unitPrice()).isEqualByComparingTo(BigDecimal.valueOf(15.00));
    assertThat(response.totalPrice()).isEqualByComparingTo(BigDecimal.valueOf(150.00));
    assertThat(response.arrivalDate()).isEqualTo(now);
  }
}
