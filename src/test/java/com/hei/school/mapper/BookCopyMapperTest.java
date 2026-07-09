package com.hei.school.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.hei.school.dto.request.CreateBookCopyRequest;
import com.hei.school.dto.response.BookCopyResponse;
import com.hei.school.entity.Book;
import com.hei.school.entity.BookCopy;
import com.hei.school.entity.Library;
import com.hei.school.entity.enums.BookCopyFormat;
import com.hei.school.entity.enums.BookCopyStatus;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class BookCopyMapperTest {

  private final BookCopyMapper mapper = new BookCopyMapper();

  @Test
  void toEntity() {
    var bookId = UUID.randomUUID();
    var libraryId = UUID.randomUUID();
    var book = new Book();
    book.setId(bookId);
    var library = new Library();
    library.setId(libraryId);
    var request =
        new CreateBookCopyRequest(
            bookId,
            libraryId,
            BookCopyFormat.PHYSICAL,
            "978-1234567890",
            BigDecimal.valueOf(20.00),
            BookCopyStatus.AVAILABLE);

    var result = mapper.toEntity(request, book, library);

    assertThat(result.getFormat()).isEqualTo(BookCopyFormat.PHYSICAL);
    assertThat(result.getIsbn()).isEqualTo("978-1234567890");
    assertThat(result.getSellingPrice()).isEqualByComparingTo(BigDecimal.valueOf(20.00));
    assertThat(result.getStatus()).isEqualTo(BookCopyStatus.AVAILABLE);
    assertThat(result.getBook().getId()).isEqualTo(bookId);
    assertThat(result.getLibrary().getId()).isEqualTo(libraryId);
  }

  @Test
  void toResponse() {
    var copyId = UUID.randomUUID();
    var bookId = UUID.randomUUID();
    var libraryId = UUID.randomUUID();
    var book = new Book();
    book.setId(bookId);
    book.setTitle("Test Book");
    var library = new Library();
    library.setId(libraryId);
    library.setName("Main Library");

    var copy =
        BookCopy.builder()
            .id(copyId)
            .format(BookCopyFormat.PHYSICAL)
            .isbn("978-1234567890")
            .sellingPrice(BigDecimal.valueOf(20.00))
            .status(BookCopyStatus.AVAILABLE)
            .book(book)
            .library(library)
            .build();

    var result = mapper.toResponse(copy);

    assertThat(result.id()).isEqualTo(copyId);
    assertThat(result.bookId()).isEqualTo(bookId);
    assertThat(result.bookTitle()).isEqualTo("Test Book");
    assertThat(result.libraryId()).isEqualTo(libraryId);
    assertThat(result.libraryName()).isEqualTo("Main Library");
    assertThat(result.format()).isEqualTo(BookCopyFormat.PHYSICAL);
    assertThat(result.isbn()).isEqualTo("978-1234567890");
    assertThat(result.sellingPrice()).isEqualByComparingTo(BigDecimal.valueOf(20.00));
    assertThat(result.status()).isEqualTo(BookCopyStatus.AVAILABLE);
  }
}
