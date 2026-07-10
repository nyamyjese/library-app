package com.hei.school.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.hei.school.entity.Customer;
import com.hei.school.entity.Library;
import com.hei.school.entity.Sale;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class SaleRepositoryTest {

  @Autowired private SaleRepository saleRepository;

  @Autowired private CustomerRepository customerRepository;

  @Autowired private LibraryRepository libraryRepository;

  private Customer customer;
  private Library library;
  private Sale sale1;
  private Sale sale2;

  @BeforeEach
  void setUp() {
    customer = new Customer();
    customer.setFirstName("Rakoto");
    customer.setLastName("Andriantsoa");
    customer.setEmail("rakoto@gmail.com");
    customer.setPhone("0341112233");
    customer.setCreatedAt(Instant.now());
    customer.setUpdatedAt(Instant.now());
    customer = customerRepository.save(customer);

    library = new Library();
    library.setName("Central Library");
    library.setAddress("Analakely");
    library.setPhone("0341234567");
    library = libraryRepository.save(library);

    sale1 = new Sale();
    sale1.setCustomer(customer);
    sale1.setLibrary(library);
    sale1.setSaleDate(Instant.parse("2024-01-15T10:00:00Z"));
    sale1.setTotalAmount(new BigDecimal("84000"));
    sale1.setCreatedAt(Instant.now());
    sale1.setUpdatedAt(Instant.now());
    sale1 = saleRepository.save(sale1);

    sale2 = new Sale();
    sale2.setCustomer(customer);
    sale2.setLibrary(library);
    sale2.setSaleDate(Instant.parse("2024-02-15T10:00:00Z"));
    sale2.setTotalAmount(new BigDecimal("120000"));
    sale2.setCreatedAt(Instant.now());
    sale2.setUpdatedAt(Instant.now());
    sale2 = saleRepository.save(sale2);
  }

  @Test
  void findByCustomerId_shouldReturnSales() {
    List<Sale> result = saleRepository.findByCustomerId(customer.getId());

    assertThat(result).hasSize(2);
    assertThat(result.get(0).getCustomer().getId()).isEqualTo(customer.getId());
  }

  @Test
  void findByLibraryId_shouldReturnSales() {
    List<Sale> result = saleRepository.findByLibraryId(library.getId());

    assertThat(result).hasSize(2);
    assertThat(result.get(0).getLibrary().getId()).isEqualTo(library.getId());
  }

  @Test
  void findBySaleDateBetween_shouldReturnSales() {
    Instant from = Instant.parse("2024-01-01T00:00:00Z");
    Instant to = Instant.parse("2024-01-31T23:59:59Z");

    List<Sale> result = saleRepository.findBySaleDateBetween(from, to);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getId()).isEqualTo(sale1.getId());
  }

  @Test
  void findBySaleDateBetween_shouldReturnEmptyWhenNoMatch() {
    Instant from = Instant.parse("2024-03-01T00:00:00Z");
    Instant to = Instant.parse("2024-03-31T23:59:59Z");

    List<Sale> result = saleRepository.findBySaleDateBetween(from, to);

    assertThat(result).isEmpty();
  }

  @Test
  void save_shouldPersistSale() {
    Sale newSale = new Sale();
    newSale.setCustomer(customer);
    newSale.setLibrary(library);
    newSale.setSaleDate(Instant.now());
    newSale.setTotalAmount(new BigDecimal("50000"));
    newSale.setCreatedAt(Instant.now());
    newSale.setUpdatedAt(Instant.now());

    Sale saved = saleRepository.save(newSale);

    assertThat(saved.getId()).isNotNull();
    assertThat(saved.getTotalAmount()).isEqualByComparingTo(new BigDecimal("50000"));
  }

  @Test
  void findById_shouldReturnSale() {
    Sale found = saleRepository.findById(sale1.getId()).orElse(null);

    assertThat(found).isNotNull();
    assertThat(found.getId()).isEqualTo(sale1.getId());
    assertThat(found.getTotalAmount()).isEqualByComparingTo(new BigDecimal("84000"));
  }

  @Test
  void deleteById_shouldRemoveSale() {
    saleRepository.deleteById(sale1.getId());

    Sale found = saleRepository.findById(sale1.getId()).orElse(null);
    assertThat(found).isNull();
  }

  @Test
  void findAll_shouldReturnAllSales() {
    List<Sale> result = saleRepository.findAll();

    assertThat(result).hasSize(2);
  }
}
