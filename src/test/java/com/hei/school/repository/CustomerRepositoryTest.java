package com.hei.school.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.hei.school.entity.Customer;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class CustomerRepositoryTest {

  @Autowired private CustomerRepository customerRepository;

  private Customer customer;

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
  }

  // ==================== EXISTS BY EMAIL ====================

  @Test
  void existsByEmail_shouldReturnTrueWhenEmailExists() {
    boolean exists = customerRepository.existsByEmail("rakoto@gmail.com");

    assertThat(exists).isTrue();
  }

  @Test
  void existsByEmail_shouldReturnFalseWhenEmailDoesNotExist() {
    boolean exists = customerRepository.existsByEmail("nonexistent@gmail.com");

    assertThat(exists).isFalse();
  }

  // ==================== EXISTS BY PHONE ====================

  @Test
  void existsByPhone_shouldReturnTrueWhenPhoneExists() {
    boolean exists = customerRepository.existsByPhone("0341112233");

    assertThat(exists).isTrue();
  }

  @Test
  void existsByPhone_shouldReturnFalseWhenPhoneDoesNotExist() {
    boolean exists = customerRepository.existsByPhone("0000000000");

    assertThat(exists).isFalse();
  }

  // ==================== FIND BY EMAIL ====================

  @Test
  void findByEmail_shouldReturnCustomer() {
    Optional<Customer> found = customerRepository.findByEmail("rakoto@gmail.com");

    assertThat(found).isPresent();
    assertThat(found.get().getId()).isEqualTo(customer.getId());
    assertThat(found.get().getFirstName()).isEqualTo("Rakoto");
    assertThat(found.get().getLastName()).isEqualTo("Andriantsoa");
    assertThat(found.get().getEmail()).isEqualTo("rakoto@gmail.com");
    assertThat(found.get().getPhone()).isEqualTo("0341112233");
  }

  @Test
  void findByEmail_shouldReturnEmptyWhenEmailDoesNotExist() {
    Optional<Customer> found = customerRepository.findByEmail("nonexistent@gmail.com");

    assertThat(found).isNotPresent();
  }

  // ==================== FIND BY ID ====================

  @Test
  void findById_shouldReturnCustomer() {
    Optional<Customer> found = customerRepository.findById(customer.getId());

    assertThat(found).isPresent();
    assertThat(found.get().getId()).isEqualTo(customer.getId());
    assertThat(found.get().getFirstName()).isEqualTo("Rakoto");
    assertThat(found.get().getLastName()).isEqualTo("Andriantsoa");
  }

  @Test
  void findById_shouldReturnEmptyWhenIdDoesNotExist() {
    Optional<Customer> found = customerRepository.findById(UUID.randomUUID());

    assertThat(found).isNotPresent();
  }

  // ==================== SAVE ====================

  @Test
  void save_shouldPersistCustomer() {
    Customer newCustomer = new Customer();
    newCustomer.setFirstName("Jean");
    newCustomer.setLastName("Dupont");
    newCustomer.setEmail("jean@gmail.com");
    newCustomer.setPhone("0341112234");
    newCustomer.setCreatedAt(Instant.now());
    newCustomer.setUpdatedAt(Instant.now());

    Customer saved = customerRepository.save(newCustomer);

    assertThat(saved.getId()).isNotNull();
    assertThat(saved.getFirstName()).isEqualTo("Jean");
    assertThat(saved.getLastName()).isEqualTo("Dupont");
    assertThat(saved.getEmail()).isEqualTo("jean@gmail.com");
    assertThat(saved.getPhone()).isEqualTo("0341112234");
  }

  // ==================== UPDATE ====================

  @Test
  void update_shouldModifyCustomer() {
    customer.setFirstName("Rakoto Updated");
    customer.setLastName("Andriantsoa Updated");
    customer.setEmail("rakoto.updated@gmail.com");
    customer.setPhone("0341112234");

    Customer updated = customerRepository.save(customer);

    assertThat(updated.getId()).isEqualTo(customer.getId());
    assertThat(updated.getFirstName()).isEqualTo("Rakoto Updated");
    assertThat(updated.getLastName()).isEqualTo("Andriantsoa Updated");
    assertThat(updated.getEmail()).isEqualTo("rakoto.updated@gmail.com");
    assertThat(updated.getPhone()).isEqualTo("0341112234");
  }

  // ==================== DELETE ====================

  @Test
  void deleteById_shouldRemoveCustomer() {
    customerRepository.deleteById(customer.getId());

    Optional<Customer> found = customerRepository.findById(customer.getId());
    assertThat(found).isNotPresent();
  }

  // ==================== FIND ALL ====================

  @Test
  void findAll_shouldReturnAllCustomers() {
    Customer customer2 = new Customer();
    customer2.setFirstName("Jean");
    customer2.setLastName("Dupont");
    customer2.setEmail("jean@gmail.com");
    customer2.setPhone("0341112234");
    customer2.setCreatedAt(Instant.now());
    customer2.setUpdatedAt(Instant.now());
    customerRepository.save(customer2);

    List<Customer> result = customerRepository.findAll();

    assertThat(result).hasSize(2);
    assertThat(result)
        .extracting(Customer::getFirstName)
        .containsExactlyInAnyOrder("Rakoto", "Jean");
  }
}
