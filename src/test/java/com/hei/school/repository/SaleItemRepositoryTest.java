package com.hei.school.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.hei.school.entity.*;
import com.hei.school.entity.enums.BookCopyFormat;
import com.hei.school.entity.enums.BookCopyStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class SaleItemRepositoryTest {

  @Autowired private SaleItemRepository saleItemRepository;

  @Autowired private SaleRepository saleRepository;

  @Autowired private CustomerRepository customerRepository;

  @Autowired private LibraryRepository libraryRepository;

  @Autowired private BookRepository bookRepository;

  @Autowired private BookCopyRepository bookCopyRepository;

  @Autowired private GenreRepository genreRepository;

  private Sale sale;
  private BookCopy bookCopy;
  private SaleItem saleItem;
  private Genre genre;

  @BeforeEach
  void setUp() {
    Customer customer = new Customer();
    customer.setFirstName("Rakoto");
    customer.setLastName("Andriantsoa");
    customer.setEmail("rakoto@gmail.com");
    customer.setPhone("0341112233");
    customer.setCreatedAt(Instant.now());
    customer.setUpdatedAt(Instant.now());
    customer = customerRepository.save(customer);

    Library library = new Library();
    library.setName("Central Library");
    library.setAddress("Analakely");
    library.setPhone("0341234567");
    library = libraryRepository.save(library);

    sale = new Sale();
    sale.setCustomer(customer);
    sale.setLibrary(library);
    sale.setSaleDate(Instant.parse("2024-01-15T10:00:00Z"));
    sale.setTotalAmount(new BigDecimal("84000"));
    sale.setCreatedAt(Instant.now());
    sale.setUpdatedAt(Instant.now());
    sale = saleRepository.save(sale);

    Book book = new Book();
    book.setTitle("Test Book");
    book.setIsbn("9781234567890");
    book.setPublicationYear(2024);
    book.setPrice(new BigDecimal("30000"));
    book.setLibrary(library);
    book = bookRepository.save(book);

    genre = new Genre();
    genre.setName("Computer Science");
    genre = genreRepository.save(genre);

    List<Genre> genres = new ArrayList<>();
    genres.add(genre);
    book.setGenres(genres);
    book = bookRepository.save(book);

    bookCopy = new BookCopy();
    bookCopy.setBook(book);
    bookCopy.setLibrary(library);
    bookCopy.setFormat(BookCopyFormat.PHYSICAL);
    bookCopy.setIsbn("9781234567890");
    bookCopy.setSellingPrice(new BigDecimal("30000"));
    bookCopy.setStatus(BookCopyStatus.AVAILABLE);
    bookCopy = bookCopyRepository.save(bookCopy);

    saleItem = new SaleItem();
    saleItem.setSale(sale);
    saleItem.setBookCopy(bookCopy);
    saleItem.setQuantity(3);
    saleItem.setUnitPrice(new BigDecimal("28000"));
    saleItem = saleItemRepository.save(saleItem);
  }

  @Test
  void findBySaleId_shouldReturnSaleItems() {
    List<SaleItem> result = saleItemRepository.findBySaleId(sale.getId());
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getSale().getId()).isEqualTo(sale.getId());
    assertThat(result.get(0).getQuantity()).isEqualTo(3);
    assertThat(result.get(0).getUnitPrice()).isEqualByComparingTo(new BigDecimal("28000"));
  }

  @Test
  void findBySaleId_shouldReturnEmptyWhenNoSaleItems() {
    UUID newSaleId = UUID.randomUUID();
    List<SaleItem> result = saleItemRepository.findBySaleId(newSaleId);
    assertThat(result).isEmpty();
  }

  @Test
  void deleteBySaleId_shouldRemoveSaleItems() {
    saleItemRepository.deleteBySaleId(sale.getId());
    List<SaleItem> result = saleItemRepository.findBySaleId(sale.getId());
    assertThat(result).isEmpty();
  }

  @Test
  void deleteBySaleId_shouldDoNothingWhenNoSaleItems() {
    UUID newSaleId = UUID.randomUUID();
    saleItemRepository.deleteBySaleId(newSaleId);
    List<SaleItem> result = saleItemRepository.findBySaleId(sale.getId());
    assertThat(result).hasSize(1);
  }

  @Test
  void sumRevenueByGenreId_shouldReturnRevenue() {
    BigDecimal revenue = saleItemRepository.sumRevenueByGenreId(genre.getId());
    assertThat(revenue).isNotNull();
    assertThat(revenue).isEqualByComparingTo(new BigDecimal("84000"));
  }

  @Test
  void sumRevenueByGenreId_shouldReturnNullWhenNoSales() {
    UUID nonExistingGenreId = UUID.randomUUID();
    BigDecimal revenue = saleItemRepository.sumRevenueByGenreId(nonExistingGenreId);
    assertThat(revenue).isNull();
  }

  @Test
  void sumRevenueByGenreId_shouldReturnNullWhenNoSalesForGenre() {
    Genre emptyGenre = new Genre();
    emptyGenre.setName("Empty Genre");
    emptyGenre = genreRepository.save(emptyGenre);
    BigDecimal revenue = saleItemRepository.sumRevenueByGenreId(emptyGenre.getId());
    assertThat(revenue).isNull();
  }

  @Test
  void save_shouldPersistSaleItem() {
    SaleItem newSaleItem = new SaleItem();
    newSaleItem.setSale(sale);
    newSaleItem.setBookCopy(bookCopy);
    newSaleItem.setQuantity(2);
    newSaleItem.setUnitPrice(new BigDecimal("25000"));
    SaleItem saved = saleItemRepository.save(newSaleItem);
    assertThat(saved.getId()).isNotNull();
    assertThat(saved.getQuantity()).isEqualTo(2);
    assertThat(saved.getUnitPrice()).isEqualByComparingTo(new BigDecimal("25000"));
    assertThat(saved.getSale().getId()).isEqualTo(sale.getId());
    assertThat(saved.getBookCopy().getId()).isEqualTo(bookCopy.getId());
  }

  @Test
  void findById_shouldReturnSaleItem() {
    SaleItem found = saleItemRepository.findById(saleItem.getId()).orElse(null);
    assertThat(found).isNotNull();
    assertThat(found.getId()).isEqualTo(saleItem.getId());
    assertThat(found.getQuantity()).isEqualTo(3);
    assertThat(found.getUnitPrice()).isEqualByComparingTo(new BigDecimal("28000"));
  }

  @Test
  void findById_shouldReturnEmptyWhenIdDoesNotExist() {
    UUID nonExistingId = UUID.randomUUID();
    SaleItem found = saleItemRepository.findById(nonExistingId).orElse(null);
    assertThat(found).isNull();
  }

  @Test
  void deleteById_shouldRemoveSaleItem() {
    saleItemRepository.deleteById(saleItem.getId());
    SaleItem found = saleItemRepository.findById(saleItem.getId()).orElse(null);
    assertThat(found).isNull();
  }

  @Test
  void deleteById_shouldDoNothingWhenIdDoesNotExist() {
    UUID nonExistingId = UUID.randomUUID();
    saleItemRepository.deleteById(nonExistingId);
    SaleItem found = saleItemRepository.findById(saleItem.getId()).orElse(null);
    assertThat(found).isNotNull();
  }

  @Test
  void findAll_shouldReturnAllSaleItems() {
    List<SaleItem> result = saleItemRepository.findAll();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getId()).isEqualTo(saleItem.getId());
  }

  @Test
  void findAll_shouldReturnAllSaleItemsWhenMultiple() {
    SaleItem secondSaleItem = new SaleItem();
    secondSaleItem.setSale(sale);
    secondSaleItem.setBookCopy(bookCopy);
    secondSaleItem.setQuantity(5);
    secondSaleItem.setUnitPrice(new BigDecimal("20000"));
    saleItemRepository.save(secondSaleItem);
    List<SaleItem> result = saleItemRepository.findAll();
    assertThat(result).hasSize(2);
    assertThat(result).extracting(SaleItem::getQuantity).containsExactlyInAnyOrder(3, 5);
  }
}
