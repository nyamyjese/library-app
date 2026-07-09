package com.hei.school.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.hei.school.dto.BookCopyDTO;
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

  @Test
  void toDTO_shouldMapAllFields() {
    BookCopy bookCopy = createBookCopy();
    BookCopyDTO dto = mapper.toDTO(bookCopy);

    assertThat(dto.getId()).isEqualTo(bookCopyId);
    assertThat(dto.getBookId()).isEqualTo(bookId);
    assertThat(dto.getLibraryId()).isEqualTo(libraryId);
    assertThat(dto.getFormat()).isEqualTo(BookCopyFormat.PHYSICAL);
    assertThat(dto.getIsbn()).isEqualTo("9781234567890");
    assertThat(dto.getSellingPrice()).isEqualByComparingTo(new BigDecimal("29.99"));
    assertThat(dto.getStatus()).isEqualTo(BookCopyStatus.AVAILABLE);
  }

  @Test
  void toDTO_shouldHandleNullBookAndLibrary() {
    BookCopy bookCopy = new BookCopy();
    bookCopy.setId(bookCopyId);
    bookCopy.setFormat(BookCopyFormat.DIGITAL);
    bookCopy.setIsbn("9781234567890");
    bookCopy.setSellingPrice(new BigDecimal("19.99"));
    bookCopy.setStatus(BookCopyStatus.AVAILABLE);

    BookCopyDTO dto = mapper.toDTO(bookCopy);

    assertThat(dto.getBookId()).isNull();
    assertThat(dto.getLibraryId()).isNull();
  }

  @Test
  void toEntity_shouldMapRequestToEntity() {
    CreateBookCopyRequest request =
        new CreateBookCopyRequest(
            bookId,
            libraryId,
            BookCopyFormat.POCKET,
            "9781234567890",
            new BigDecimal("14.99"),
            BookCopyStatus.AVAILABLE);
    Book book = createBook();
    Library library = createLibrary();

    BookCopy bookCopy = mapper.toEntity(request, book, library);

    assertThat(bookCopy.getBook()).isEqualTo(book);
    assertThat(bookCopy.getLibrary()).isEqualTo(library);
    assertThat(bookCopy.getFormat()).isEqualTo(BookCopyFormat.POCKET);
    assertThat(bookCopy.getIsbn()).isEqualTo("9781234567890");
    assertThat(bookCopy.getSellingPrice()).isEqualByComparingTo(new BigDecimal("14.99"));
    assertThat(bookCopy.getStatus()).isEqualTo(BookCopyStatus.AVAILABLE);
  }

  @Test
  void toResponse_shouldMapAllFields() {
    BookCopy bookCopy = createBookCopy();

    BookCopyResponse response = mapper.toResponse(bookCopy);

    assertThat(response.id()).isEqualTo(bookCopyId);
    assertThat(response.bookId()).isEqualTo(bookId);
    assertThat(response.bookTitle()).isEqualTo("Test Book");
    assertThat(response.libraryId()).isEqualTo(libraryId);
    assertThat(response.libraryName()).isEqualTo("Test Library");
    assertThat(response.format()).isEqualTo(BookCopyFormat.PHYSICAL);
    assertThat(response.isbn()).isEqualTo("9781234567890");
    assertThat(response.sellingPrice()).isEqualByComparingTo(new BigDecimal("29.99"));
    assertThat(response.status()).isEqualTo(BookCopyStatus.AVAILABLE);
  }

  @Test
  void toResponse_shouldMapNullBookAndLibrary() {
    BookCopy bookCopy = new BookCopy();
    bookCopy.setId(bookCopyId);
    bookCopy.setFormat(BookCopyFormat.DIGITAL);
    bookCopy.setIsbn("9781234567890");
    bookCopy.setSellingPrice(new BigDecimal("19.99"));
    bookCopy.setStatus(BookCopyStatus.AVAILABLE);

    BookCopyResponse response = mapper.toResponse(bookCopy);

    assertThat(response.bookId()).isNull();
    assertThat(response.libraryId()).isNull();
  }
}
