package com.hei.school.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.hei.school.dto.request.CreateSaleItemRequest;
import com.hei.school.dto.response.SaleItemResponse;
import com.hei.school.entity.Book;
import com.hei.school.entity.BookCopy;
import com.hei.school.entity.Library;
import com.hei.school.entity.Sale;
import com.hei.school.entity.SaleItem;
import com.hei.school.entity.enums.BookCopyFormat;
import com.hei.school.entity.enums.BookCopyStatus;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class SaleItemMapperTest {

  private final SaleItemMapper mapper = new SaleItemMapper();

  private final UUID saleItemId = UUID.randomUUID();
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
    BookCopy bookCopy = new BookCopy();
    bookCopy.setId(bookCopyId);
    bookCopy.setBook(createBook());
    bookCopy.setLibrary(createLibrary());
    bookCopy.setFormat(BookCopyFormat.PHYSICAL);
    bookCopy.setIsbn("9781234567890");
    bookCopy.setSellingPrice(new BigDecimal("30000"));
    bookCopy.setStatus(BookCopyStatus.AVAILABLE);
    return bookCopy;
  }

  @Test
  void toEntity_shouldMapAllFields() {
    CreateSaleItemRequest request =
        new CreateSaleItemRequest(bookCopyId, 3, new BigDecimal("28000"));
    Sale sale = new Sale();
    sale.setId(UUID.randomUUID());
    BookCopy bookCopy = createBookCopy();

    SaleItem result = mapper.toEntity(request, bookCopy, sale);

    assertThat(result.getBookCopy()).isEqualTo(bookCopy);
    assertThat(result.getSale()).isEqualTo(sale);
    assertThat(result.getQuantity()).isEqualTo(3);
    assertThat(result.getUnitPrice()).isEqualByComparingTo(new BigDecimal("28000"));
  }

  @Test
  void toResponse_shouldMapAllFields() {
    BookCopy bookCopy = createBookCopy();
    SaleItem saleItem = new SaleItem();
    saleItem.setId(saleItemId);
    saleItem.setBookCopy(bookCopy);
    saleItem.setQuantity(3);
    saleItem.setUnitPrice(new BigDecimal("28000"));

    SaleItemResponse response = mapper.toResponse(saleItem);

    assertThat(response.id()).isEqualTo(saleItemId);
    assertThat(response.bookCopyId()).isEqualTo(bookCopyId);
    assertThat(response.bookTitle()).isEqualTo("Test Book");
    assertThat(response.bookCopyIsbn()).isEqualTo("9781234567890");
    assertThat(response.quantity()).isEqualTo(3);
    assertThat(response.unitPrice()).isEqualByComparingTo(new BigDecimal("28000"));
    assertThat(response.totalPrice()).isEqualByComparingTo(new BigDecimal("84000"));
  }
}
