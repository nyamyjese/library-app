package com.hei.school.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.hei.school.dto.request.CreateSaleItemRequest;
import com.hei.school.dto.request.CreateSaleRequest;
import com.hei.school.dto.request.UpdateSaleRequest;
import com.hei.school.dto.response.SaleResponse;
import com.hei.school.entity.Book;
import com.hei.school.entity.BookCopy;
import com.hei.school.entity.Customer;
import com.hei.school.entity.Library;
import com.hei.school.entity.Sale;
import com.hei.school.entity.SaleItem;
import com.hei.school.entity.enums.BookCopyFormat;
import com.hei.school.entity.enums.BookCopyStatus;
import com.hei.school.exception.NotFoundException;
import com.hei.school.mapper.SaleItemMapper;
import com.hei.school.mapper.SaleMapper;
import com.hei.school.repository.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
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
class SaleServiceTest {

  @Mock private SaleRepository saleRepository;

  @Mock private SaleItemRepository saleItemRepository;

  @Mock private CustomerRepository customerRepository;

  @Mock private LibraryRepository libraryRepository;

  @Mock private BookCopyRepository bookCopyRepository;

  @Mock private StockMovementRepository stockMovementRepository;

  @Mock private SaleMapper saleMapper;

  @Mock private SaleItemMapper saleItemMapper;

  @InjectMocks private SaleService saleService;

  private final UUID saleId = UUID.randomUUID();
  private final UUID customerId = UUID.randomUUID();
  private final UUID libraryId = UUID.randomUUID();
  private final UUID bookCopyId = UUID.randomUUID();

  private Customer customer;
  private Library library;
  private BookCopy bookCopy;
  private Sale sale;
  private SaleItem saleItem;
  private SaleResponse saleResponse;

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
    book.setId(UUID.randomUUID());
    book.setTitle("Test Book");

    bookCopy = new BookCopy();
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

    List<SaleItem> saleItems = new ArrayList<>();
    saleItems.add(saleItem);

    sale = new Sale();
    sale.setId(saleId);
    sale.setCustomer(customer);
    sale.setLibrary(library);
    sale.setSaleDate(Instant.parse("2024-01-15T10:00:00Z"));
    sale.setTotalAmount(new BigDecimal("84000"));
    sale.setSaleItems(saleItems);

    saleResponse =
        new SaleResponse(
            saleId,
            customerId,
            "Rakoto",
            "Andriantsoa",
            libraryId,
            "Central Library",
            Instant.parse("2024-01-15T10:00:00Z"),
            new BigDecimal("84000"),
            new ArrayList<>(),
            Instant.now(),
            Instant.now());
  }

  // ==================== CREATE ====================

  @Test
  void create_Success() {
    List<CreateSaleItemRequest> itemRequests = new ArrayList<>();
    itemRequests.add(new CreateSaleItemRequest(bookCopyId, 3, new BigDecimal("28000")));

    CreateSaleRequest request =
        new CreateSaleRequest(
            customerId, libraryId, Instant.parse("2024-01-15T10:00:00Z"), itemRequests);

    when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
    when(libraryRepository.findById(libraryId)).thenReturn(Optional.of(library));
    when(saleMapper.toEntity(request, customer, library)).thenReturn(sale);
    when(bookCopyRepository.findById(bookCopyId)).thenReturn(Optional.of(bookCopy));
    when(saleItemMapper.toEntity(any(), any(), any())).thenReturn(saleItem);
    when(saleRepository.save(any(Sale.class))).thenReturn(sale);
    when(saleMapper.toResponse(sale)).thenReturn(saleResponse);

    SaleResponse result = saleService.create(request);

    assertThat(result).isNotNull();
    assertThat(result.id()).isEqualTo(saleId);

    verify(customerRepository).findById(customerId);
    verify(libraryRepository).findById(libraryId);
    verify(bookCopyRepository).findById(bookCopyId);
    verify(saleRepository).save(any(Sale.class));
    verify(stockMovementRepository, atLeastOnce()).save(any());
  }

  @Test
  void create_shouldThrowNotFoundWhenCustomerNotFound() {
    List<CreateSaleItemRequest> itemRequests = new ArrayList<>();
    itemRequests.add(new CreateSaleItemRequest(bookCopyId, 3, new BigDecimal("28000")));

    CreateSaleRequest request =
        new CreateSaleRequest(
            customerId, libraryId, Instant.parse("2024-01-15T10:00:00Z"), itemRequests);

    when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> saleService.create(request))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("Customer with id " + customerId + " not found");
  }

  @Test
  void create_shouldThrowNotFoundWhenLibraryNotFound() {
    List<CreateSaleItemRequest> itemRequests = new ArrayList<>();
    itemRequests.add(new CreateSaleItemRequest(bookCopyId, 3, new BigDecimal("28000")));

    CreateSaleRequest request =
        new CreateSaleRequest(
            customerId, libraryId, Instant.parse("2024-01-15T10:00:00Z"), itemRequests);

    when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
    when(libraryRepository.findById(libraryId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> saleService.create(request))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("Library with id " + libraryId + " not found");
  }

  @Test
  void create_shouldThrowNotFoundWhenBookCopyNotFound() {
    List<CreateSaleItemRequest> itemRequests = new ArrayList<>();
    itemRequests.add(new CreateSaleItemRequest(bookCopyId, 3, new BigDecimal("28000")));

    CreateSaleRequest request =
        new CreateSaleRequest(
            customerId, libraryId, Instant.parse("2024-01-15T10:00:00Z"), itemRequests);

    when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
    when(libraryRepository.findById(libraryId)).thenReturn(Optional.of(library));
    when(saleMapper.toEntity(request, customer, library)).thenReturn(sale);
    when(bookCopyRepository.findById(bookCopyId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> saleService.create(request))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("BookCopy with id " + bookCopyId + " not found");
  }

  // ==================== GET ALL ====================

  @Test
  void getAll_NoFilters() {
    when(saleRepository.findAll()).thenReturn(List.of(sale));
    when(saleMapper.toResponse(sale)).thenReturn(saleResponse);

    List<SaleResponse> result = saleService.getAll(null, null, null, null);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).id()).isEqualTo(saleId);
  }

  @Test
  void getAll_FilterByCustomerId() {
    when(saleRepository.findByCustomerId(customerId)).thenReturn(List.of(sale));
    when(saleMapper.toResponse(sale)).thenReturn(saleResponse);

    List<SaleResponse> result = saleService.getAll(customerId, null, null, null);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).customerId()).isEqualTo(customerId);
  }

  @Test
  void getAll_FilterByLibraryId() {
    when(saleRepository.findByLibraryId(libraryId)).thenReturn(List.of(sale));
    when(saleMapper.toResponse(sale)).thenReturn(saleResponse);

    List<SaleResponse> result = saleService.getAll(null, libraryId, null, null);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).libraryId()).isEqualTo(libraryId);
  }

  @Test
  void getAll_FilterByDateRange() {
    Instant from = Instant.parse("2024-01-01T00:00:00Z");
    Instant to = Instant.parse("2024-01-31T23:59:59Z");

    when(saleRepository.findBySaleDateBetween(from, to)).thenReturn(List.of(sale));
    when(saleMapper.toResponse(sale)).thenReturn(saleResponse);

    List<SaleResponse> result = saleService.getAll(null, null, from, to);

    assertThat(result).hasSize(1);
  }

  // ==================== GET BY ID ====================

  @Test
  void getById_Success() {
    when(saleRepository.findById(saleId)).thenReturn(Optional.of(sale));
    when(saleMapper.toResponse(sale)).thenReturn(saleResponse);

    SaleResponse result = saleService.getById(saleId);

    assertThat(result).isNotNull();
    assertThat(result.id()).isEqualTo(saleId);
  }

  @Test
  void getById_shouldThrowNotFound() {
    when(saleRepository.findById(saleId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> saleService.getById(saleId))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("Sale with id " + saleId + " not found");
  }

  // ==================== UPDATE ====================

  @Test
  void update_Success() {
    List<CreateSaleItemRequest> itemRequests = new ArrayList<>();
    itemRequests.add(new CreateSaleItemRequest(bookCopyId, 2, new BigDecimal("30000")));

    UpdateSaleRequest request =
        new UpdateSaleRequest(Instant.parse("2024-01-20T10:00:00Z"), itemRequests);

    List<SaleItem> saleItems = new ArrayList<>();
    saleItems.add(saleItem);
    sale.setSaleItems(saleItems);

    when(saleRepository.findById(saleId)).thenReturn(Optional.of(sale));
    when(bookCopyRepository.findById(bookCopyId)).thenReturn(Optional.of(bookCopy));
    when(saleItemMapper.toEntity(any(), any(), any())).thenReturn(saleItem);
    when(saleItemRepository.saveAll(any())).thenReturn(List.of(saleItem));
    when(saleMapper.toResponse(any(Sale.class))).thenReturn(saleResponse);

    SaleResponse result = saleService.update(saleId, request);

    assertThat(result).isNotNull();
    verify(saleItemRepository).deleteBySaleId(saleId);
    verify(saleItemRepository).saveAll(any());
  }

  @Test
  void update_shouldThrowNotFoundWhenSaleNotFound() {
    List<CreateSaleItemRequest> itemRequests = new ArrayList<>();
    itemRequests.add(new CreateSaleItemRequest(bookCopyId, 2, new BigDecimal("30000")));

    UpdateSaleRequest request =
        new UpdateSaleRequest(Instant.parse("2024-01-20T10:00:00Z"), itemRequests);

    when(saleRepository.findById(saleId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> saleService.update(saleId, request))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("Sale with id " + saleId + " not found");

    verify(saleItemRepository, never()).deleteBySaleId(any());
  }

  @Test
  void update_shouldThrowNotFoundWhenBookCopyNotFound() {
    List<CreateSaleItemRequest> itemRequests = new ArrayList<>();
    itemRequests.add(new CreateSaleItemRequest(bookCopyId, 2, new BigDecimal("30000")));

    UpdateSaleRequest request =
        new UpdateSaleRequest(Instant.parse("2024-01-20T10:00:00Z"), itemRequests);

    when(saleRepository.findById(saleId)).thenReturn(Optional.of(sale));
    when(bookCopyRepository.findById(bookCopyId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> saleService.update(saleId, request))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("BookCopy with id " + bookCopyId + " not found");

    verify(saleItemRepository).deleteBySaleId(saleId);
    verify(saleItemRepository, never()).saveAll(any());
  }

  // ==================== DELETE ====================

  @Test
  void delete_Success() {
    when(saleRepository.findById(saleId)).thenReturn(Optional.of(sale));

    saleService.delete(saleId);

    verify(saleItemRepository).deleteBySaleId(saleId);
    verify(saleRepository).deleteById(saleId);
  }

  @Test
  void delete_shouldThrowNotFound() {
    when(saleRepository.findById(saleId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> saleService.delete(saleId))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("Sale with id " + saleId + " not found");

    verify(saleItemRepository, never()).deleteBySaleId(any());
    verify(saleRepository, never()).deleteById(any());
  }
}
