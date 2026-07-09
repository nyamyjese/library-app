package com.hei.school.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.hei.school.dto.request.CreateCustomerRequest;
import com.hei.school.dto.request.UpdateCustomerRequest;
import com.hei.school.dto.response.CustomerResponse;
import com.hei.school.entity.Customer;
import com.hei.school.exception.BadRequestException;
import com.hei.school.exception.NotFoundException;
import com.hei.school.mapper.CustomerMapper;
import com.hei.school.repository.CustomerRepository;
import java.time.Instant;
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
class CustomerServiceTest {

  @Mock private CustomerRepository customerRepository;
  @Mock private CustomerMapper customerMapper;

  @InjectMocks private CustomerService customerService;

  private UUID customerId;
  private Customer customer;
  private CustomerResponse customerResponse;

  @BeforeEach
  void setUp() {
    customerId = UUID.randomUUID();

    customer = new Customer();
    customer.setId(customerId);
    customer.setFirstName("Rakoto");
    customer.setLastName("Andriantsoa");
    customer.setEmail("rakoto@gmail.com");
    customer.setPhone("0341112233");

    customerResponse =
        new CustomerResponse(
            customerId,
            "Rakoto",
            "Andriantsoa",
            "rakoto@gmail.com",
            "0341112233",
            Instant.now(),
            Instant.now());
  }

  // ─────────────────────────────────────────────
  // CREATE
  // ─────────────────────────────────────────────

  @Test
  void create_should_return_customer_response() {
    CreateCustomerRequest request =
        new CreateCustomerRequest("Rakoto", "Andriantsoa", "rakoto@gmail.com", "0341112233");

    when(customerRepository.existsByEmail(request.email())).thenReturn(false);
    when(customerRepository.existsByPhone(request.phone())).thenReturn(false);
    when(customerMapper.toEntity(request)).thenReturn(customer);
    when(customerRepository.save(customer)).thenReturn(customer);
    when(customerMapper.toResponse(customer)).thenReturn(customerResponse);

    CustomerResponse result = customerService.create(request);

    assertNotNull(result);
    assertEquals("rakoto@gmail.com", result.email());
    assertEquals("0341112233", result.phone());
    verify(customerRepository).save(customer);
  }

  @Test
  void create_should_throw_when_email_already_used() {
    CreateCustomerRequest request =
        new CreateCustomerRequest("Rakoto", "Andriantsoa", "rakoto@gmail.com", "0341112233");

    when(customerRepository.existsByEmail(request.email())).thenReturn(true);

    BadRequestException ex =
        assertThrows(BadRequestException.class, () -> customerService.create(request));

    assertTrue(ex.getMessage().contains("rakoto@gmail.com"));
    verify(customerRepository, never()).save(any());
  }

  @Test
  void create_should_throw_when_phone_already_used() {
    CreateCustomerRequest request =
        new CreateCustomerRequest("Rakoto", "Andriantsoa", "rakoto@gmail.com", "0341112233");

    when(customerRepository.existsByEmail(request.email())).thenReturn(false);
    when(customerRepository.existsByPhone(request.phone())).thenReturn(true);

    BadRequestException ex =
        assertThrows(BadRequestException.class, () -> customerService.create(request));

    assertTrue(ex.getMessage().contains("0341112233"));
    verify(customerRepository, never()).save(any());
  }

  // ─────────────────────────────────────────────
  // GET ALL
  // ─────────────────────────────────────────────

  @Test
  void getAll_should_return_list_of_customers() {
    when(customerRepository.findAll()).thenReturn(List.of(customer));
    when(customerMapper.toResponse(customer)).thenReturn(customerResponse);

    List<CustomerResponse> result = customerService.getAll();

    assertEquals(1, result.size());
    assertEquals("rakoto@gmail.com", result.get(0).email());
  }

  // ─────────────────────────────────────────────
  // GET BY ID
  // ─────────────────────────────────────────────

  @Test
  void getById_should_return_customer_response() {
    when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
    when(customerMapper.toResponse(customer)).thenReturn(customerResponse);

    CustomerResponse result = customerService.getById(customerId);

    assertNotNull(result);
    assertEquals(customerId, result.id());
  }

  @Test
  void getById_should_throw_when_not_found() {
    when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> customerService.getById(customerId));
  }

  // ─────────────────────────────────────────────
  // UPDATE
  // ─────────────────────────────────────────────

  @Test
  void update_should_return_updated_customer() {
    UpdateCustomerRequest request =
        new UpdateCustomerRequest("Rakoto", "Updated", "rakoto.updated@gmail.com", "0341112233");

    CustomerResponse updatedResponse =
        new CustomerResponse(
            customerId,
            "Rakoto",
            "Updated",
            "rakoto.updated@gmail.com",
            "0341112233",
            Instant.now(),
            Instant.now());

    when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
    when(customerRepository.existsByEmail(request.email())).thenReturn(false);
    when(customerRepository.save(customer)).thenReturn(customer);
    when(customerMapper.toResponse(customer)).thenReturn(updatedResponse);

    CustomerResponse result = customerService.update(customerId, request);

    assertNotNull(result);
    assertEquals("rakoto.updated@gmail.com", result.email());
  }

  @Test
  void update_should_throw_when_customer_not_found() {
    UpdateCustomerRequest request =
        new UpdateCustomerRequest("Rakoto", "Andriantsoa", "rakoto@gmail.com", "0341112233");

    when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> customerService.update(customerId, request));
  }

  @Test
  void update_should_throw_when_new_email_already_used() {
    UpdateCustomerRequest request =
        new UpdateCustomerRequest("Rakoto", "Andriantsoa", "other@gmail.com", "0341112233");

    when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
    when(customerRepository.existsByEmail("other@gmail.com")).thenReturn(true);

    assertThrows(BadRequestException.class, () -> customerService.update(customerId, request));
    verify(customerRepository, never()).save(any());
  }

  @Test
  void update_should_throw_when_new_phone_already_used() {
    UpdateCustomerRequest request =
        new UpdateCustomerRequest("Rakoto", "Andriantsoa", "rakoto@gmail.com", "0349999999");

    when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
    when(customerRepository.existsByPhone("0349999999")).thenReturn(true);

    assertThrows(BadRequestException.class, () -> customerService.update(customerId, request));
    verify(customerRepository, never()).save(any());
  }

  // ─────────────────────────────────────────────
  // DELETE
  // ─────────────────────────────────────────────

  @Test
  void delete_should_delete_customer() {
    when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

    customerService.delete(customerId);

    verify(customerRepository).deleteById(customerId);
  }

  @Test
  void delete_should_throw_when_not_found() {
    when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> customerService.delete(customerId));
    verify(customerRepository, never()).deleteById(any());
  }
}
