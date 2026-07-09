package com.hei.school.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.hei.school.dto.BookCopyDTO;
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
  void toDTO() {
    UUID copyId = UUID.randomUUID();
    UUID bookId = UUID.randomUUID();
    UUID libraryId = UUID.randomUUID();

    Library library = new Library();
    library.setId(libraryId);

    Book book = new Book();
    book.setId(bookId);

    BookCopy copy =
        BookCopy.builder()
            .id(copyId)
            .format(BookCopyFormat.PHYSICAL)
            .isbn("978-1234567890")
            .sellingPrice(BigDecimal.valueOf(20.00))
            .status(BookCopyStatus.AVAILABLE)
            .book(book)
            .library(library)
            .build();

    BookCopyDTO dto = mapper.toDTO(copy);

    assertThat(dto.getId()).isEqualTo(copyId);
    assertThat(dto.getBookId()).isEqualTo(bookId);
    assertThat(dto.getLibraryId()).isEqualTo(libraryId);
    assertThat(dto.getFormat()).isEqualTo(BookCopyFormat.PHYSICAL);
    assertThat(dto.getIsbn()).isEqualTo("978-1234567890");
    assertThat(dto.getSellingPrice()).isEqualByComparingTo(BigDecimal.valueOf(20.00));
    assertThat(dto.getStatus()).isEqualTo(BookCopyStatus.AVAILABLE);
  }

  @Test
  void toDTO_WithNullBookAndLibrary() {
    BookCopy copy =
        BookCopy.builder()
            .id(UUID.randomUUID())
            .format(BookCopyFormat.DIGITAL)
            .isbn("978-0000000000")
            .sellingPrice(BigDecimal.TEN)
            .status(BookCopyStatus.DAMAGED)
            .build();

    BookCopyDTO dto = mapper.toDTO(copy);

    assertThat(dto.getBookId()).isNull();
    assertThat(dto.getLibraryId()).isNull();
  }
}
