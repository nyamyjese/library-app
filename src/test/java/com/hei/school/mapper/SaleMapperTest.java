package com.hei.school.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.hei.school.dto.request.CreateSaleItemRequest;
import com.hei.school.dto.request.CreateSaleRequest;
import com.hei.school.dto.response.SaleItemResponse;
import com.hei.school.dto.response.SaleResponse;
import com.hei.school.entity.Book;
import com.hei.school.entity.BookCopy;
import com.hei.school.entity.Customer;
import com.hei.school.entity.Library;
import com.hei.school.entity.Sale;
import com.hei.school.entity.SaleItem;
import com.hei.school.entity.enums.BookCopyFormat;
import com.hei.school.entity.enums.BookCopyStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SaleMapperTest {

  @Mock private SaleItemMapper saleItemMapper;

  @InjectMocks private SaleMapper saleMapper;

  private final UUID saleId = UUID.randomUUID();
  private final UUID customerId = UUID.randomUUID();
  private final UUID libraryId = UUID.randomUUID();
  private final UUID bookCopyId = UUID.randomUUID();
  private final UUID bookId = UUID.randomUUID();

  private Customer customer;
  private Library library;
  private Sale sale;
  private SaleItem saleItem;

  @BeforeEach
  void setUp() {
    customer = new Customer();
    customer.setId(customerId);
    customer.setFirstName("Rakoto");
    customer.setLastName("Andriantsoa");

    library = new Library();
    library.setId(libraryId);
    library.setName("Central Library");

    Book book = new Book();
    book.setId(bookId);
    book.setTitle("Test Book");

    BookCopy bookCopy = new BookCopy();
    bookCopy.setId(bookCopyId);
    bookCopy.setBook(book);
    bookCopy.setFormat(BookCopyFormat.PHYSICAL);
    bookCopy.setIsbn("9781234567890");
    bookCopy.setSellingPrice(new BigDecimal("30000"));
    bookCopy.setStatus(BookCopyStatus.AVAILABLE);

    saleItem = new SaleItem();
    saleItem.setId(UUID.randomUUID());
    saleItem.setBookCopy(bookCopy);
    saleItem.setQuantity(3);
    saleItem.setUnitPrice(new BigDecimal("28000"));

    sale = new Sale();
    sale.setId(saleId);
    sale.setCustomer(customer);
    sale.setLibrary(library);
    sale.setSaleDate(Instant.parse("2024-01-15T10:00:00Z"));
    sale.setTotalAmount(new BigDecimal("84000"));
    sale.setSaleItems(List.of(saleItem));
    Instant now = Instant.now();
    sale.setCreatedAt(now);
    sale.setUpdatedAt(now);
  }

  @Test
  void toEntity_shouldMapAllFields() {
    CreateSaleItemRequest itemRequest =
        new CreateSaleItemRequest(bookCopyId, 3, new BigDecimal("28000"));
    CreateSaleRequest request =
        new CreateSaleRequest(
            customerId, libraryId, Instant.parse("2024-01-15T10:00:00Z"), List.of(itemRequest));

    Sale result = saleMapper.toEntity(request, customer, library);

    assertThat(result.getCustomer()).isEqualTo(customer);
    assertThat(result.getLibrary()).isEqualTo(library);
    assertThat(result.getSaleDate()).isEqualTo(Instant.parse("2024-01-15T10:00:00Z"));
    assertThat(result.getTotalAmount()).isEqualByComparingTo(BigDecimal.ZERO);
    assertThat(result.getSaleItems()).isEmpty();
  }

  @Test
  void toResponse_shouldMapAllFields() {
    SaleItemResponse mockSaleItemResponse =
        new SaleItemResponse(
            UUID.randomUUID(),
            bookCopyId,
            "Test Book",
            "9781234567890",
            3,
            new BigDecimal("28000"),
            new BigDecimal("84000"));

    when(saleItemMapper.toResponse(any(SaleItem.class))).thenReturn(mockSaleItemResponse);

    SaleResponse response = saleMapper.toResponse(sale);

    assertThat(response.id()).isEqualTo(saleId);
    assertThat(response.customerId()).isEqualTo(customerId);
    assertThat(response.customerFirstName()).isEqualTo("Rakoto");
    assertThat(response.customerLastName()).isEqualTo("Andriantsoa");
    assertThat(response.libraryId()).isEqualTo(libraryId);
    assertThat(response.libraryName()).isEqualTo("Central Library");
    assertThat(response.saleDate()).isEqualTo(Instant.parse("2024-01-15T10:00:00Z"));
    assertThat(response.totalAmount()).isEqualByComparingTo(new BigDecimal("84000"));
    assertThat(response.saleItems()).hasSize(1);
  }

  @Test
  void toResponse_shouldHandleNullSaleItems() {
    sale.setSaleItems(null);

    SaleResponse response = saleMapper.toResponse(sale);

    assertThat(response.saleItems()).isEmpty();
  }

  @Test
  void toResponse_shouldHandleEmptySaleItems() {
    sale.setSaleItems(List.of());

    SaleResponse response = saleMapper.toResponse(sale);

    assertThat(response.saleItems()).isEmpty();
  }
}
