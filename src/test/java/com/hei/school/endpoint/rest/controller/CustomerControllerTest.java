package com.hei.school.endpoint.rest.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hei.school.dto.request.CreateCustomerRequest;
import com.hei.school.dto.request.UpdateCustomerRequest;
import com.hei.school.dto.response.CustomerResponse;
import com.hei.school.exception.BadRequestException;
import com.hei.school.exception.NotFoundException;
import com.hei.school.service.CustomerService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private CustomerService customerService;

  private final UUID customerId = UUID.randomUUID();
  private final Instant now = Instant.now();

  private final CustomerResponse customerResponse =
      new CustomerResponse(
          customerId, "Rakoto", "Andriantsoa", "rakoto@gmail.com", "0341112233", now, now);

  // ==================== CREATE ====================

  @Test
  void create_shouldReturn201() throws Exception {
    CreateCustomerRequest request =
        new CreateCustomerRequest("Rakoto", "Andriantsoa", "rakoto@gmail.com", "0341112233");

    when(customerService.create(any(CreateCustomerRequest.class))).thenReturn(customerResponse);

    mockMvc
        .perform(
            post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(customerId.toString()))
        .andExpect(jsonPath("$.firstName").value("Rakoto"))
        .andExpect(jsonPath("$.lastName").value("Andriantsoa"))
        .andExpect(jsonPath("$.email").value("rakoto@gmail.com"))
        .andExpect(jsonPath("$.phone").value("0341112233"))
        .andExpect(jsonPath("$.createdAt").exists())
        .andExpect(jsonPath("$.updatedAt").exists());
  }

  @Test
  void create_shouldReturn400WhenFirstNameMissing() throws Exception {
    CreateCustomerRequest request =
        new CreateCustomerRequest("", "Andriantsoa", "rakoto@gmail.com", "0341112233");

    mockMvc
        .perform(
            post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_shouldReturn400WhenLastNameMissing() throws Exception {
    CreateCustomerRequest request =
        new CreateCustomerRequest("Rakoto", "", "rakoto@gmail.com", "0341112233");

    mockMvc
        .perform(
            post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_shouldReturn400WhenEmailInvalid() throws Exception {
    CreateCustomerRequest request =
        new CreateCustomerRequest("Rakoto", "Andriantsoa", "invalid-email", "0341112233");

    mockMvc
        .perform(
            post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_shouldReturn400WhenPhoneMissing() throws Exception {
    CreateCustomerRequest request =
        new CreateCustomerRequest("Rakoto", "Andriantsoa", "rakoto@gmail.com", "");

    mockMvc
        .perform(
            post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_shouldReturn400WhenEmailAlreadyExists() throws Exception {
    CreateCustomerRequest request =
        new CreateCustomerRequest("Rakoto", "Andriantsoa", "rakoto@gmail.com", "0341112233");

    when(customerService.create(any(CreateCustomerRequest.class)))
        .thenThrow(new BadRequestException("Email rakoto@gmail.com is already used"));

    mockMvc
        .perform(
            post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Email rakoto@gmail.com is already used"));
  }

  @Test
  void create_shouldReturn400WhenPhoneAlreadyExists() throws Exception {
    CreateCustomerRequest request =
        new CreateCustomerRequest("Rakoto", "Andriantsoa", "rakoto@gmail.com", "0341112233");

    when(customerService.create(any(CreateCustomerRequest.class)))
        .thenThrow(new BadRequestException("Phone 0341112233 is already used"));

    mockMvc
        .perform(
            post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Phone 0341112233 is already used"));
  }

  // ==================== GET ALL ====================

  @Test
  void getAll_shouldReturnList() throws Exception {
    when(customerService.getAll()).thenReturn(List.of(customerResponse));

    mockMvc
        .perform(get("/customers"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].id").value(customerId.toString()))
        .andExpect(jsonPath("$[0].firstName").value("Rakoto"))
        .andExpect(jsonPath("$[0].lastName").value("Andriantsoa"));
  }

  @Test
  void getAll_shouldReturnEmptyList() throws Exception {
    when(customerService.getAll()).thenReturn(List.of());

    mockMvc
        .perform(get("/customers"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$").isEmpty());
  }

  // ==================== GET BY ID ====================

  @Test
  void getById_shouldReturnCustomer() throws Exception {
    when(customerService.getById(customerId)).thenReturn(customerResponse);

    mockMvc
        .perform(get("/customers/{id}", customerId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(customerId.toString()))
        .andExpect(jsonPath("$.firstName").value("Rakoto"))
        .andExpect(jsonPath("$.lastName").value("Andriantsoa"))
        .andExpect(jsonPath("$.email").value("rakoto@gmail.com"))
        .andExpect(jsonPath("$.phone").value("0341112233"));
  }

  @Test
  void getById_shouldReturn404WhenNotFound() throws Exception {
    when(customerService.getById(customerId))
        .thenThrow(new NotFoundException("Customer with id " + customerId + " not found"));

    mockMvc
        .perform(get("/customers/{id}", customerId))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Customer with id " + customerId + " not found"));
  }

  // ==================== UPDATE ====================

  @Test
  void update_shouldReturnUpdatedCustomer() throws Exception {
    UpdateCustomerRequest request =
        new UpdateCustomerRequest(
            "Rakoto", "Andriantsoa", "rakoto.updated@gmail.com", "0341112233");

    CustomerResponse updatedResponse =
        new CustomerResponse(
            customerId,
            "Rakoto",
            "Andriantsoa",
            "rakoto.updated@gmail.com",
            "0341112233",
            now,
            now);

    when(customerService.update(any(UUID.class), any(UpdateCustomerRequest.class)))
        .thenReturn(updatedResponse);

    mockMvc
        .perform(
            put("/customers/{id}", customerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(customerId.toString()))
        .andExpect(jsonPath("$.firstName").value("Rakoto"))
        .andExpect(jsonPath("$.email").value("rakoto.updated@gmail.com"));
  }

  @Test
  void update_shouldReturn400WhenFirstNameMissing() throws Exception {
    UpdateCustomerRequest request =
        new UpdateCustomerRequest("", "Andriantsoa", "rakoto.updated@gmail.com", "0341112233");

    mockMvc
        .perform(
            put("/customers/{id}", customerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void update_shouldReturn400WhenEmailInvalid() throws Exception {
    UpdateCustomerRequest request =
        new UpdateCustomerRequest("Rakoto", "Andriantsoa", "invalid-email", "0341112233");

    mockMvc
        .perform(
            put("/customers/{id}", customerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void update_shouldReturn404WhenNotFound() throws Exception {
    UpdateCustomerRequest request =
        new UpdateCustomerRequest(
            "Rakoto", "Andriantsoa", "rakoto.updated@gmail.com", "0341112233");

    when(customerService.update(any(UUID.class), any(UpdateCustomerRequest.class)))
        .thenThrow(new NotFoundException("Customer with id " + customerId + " not found"));

    mockMvc
        .perform(
            put("/customers/{id}", customerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Customer with id " + customerId + " not found"));
  }

  @Test
  void update_shouldReturn400WhenEmailAlreadyUsed() throws Exception {
    UpdateCustomerRequest request =
        new UpdateCustomerRequest(
            "Rakoto", "Andriantsoa", "rakoto.updated@gmail.com", "0341112233");

    when(customerService.update(any(UUID.class), any(UpdateCustomerRequest.class)))
        .thenThrow(new BadRequestException("Email rakoto.updated@gmail.com is already used"));

    mockMvc
        .perform(
            put("/customers/{id}", customerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Email rakoto.updated@gmail.com is already used"));
  }

  @Test
  void update_shouldReturn400WhenPhoneAlreadyUsed() throws Exception {
    UpdateCustomerRequest request =
        new UpdateCustomerRequest("Rakoto", "Andriantsoa", "rakoto@gmail.com", "0341112234");

    when(customerService.update(any(UUID.class), any(UpdateCustomerRequest.class)))
        .thenThrow(new BadRequestException("Phone 0341112234 is already used"));

    mockMvc
        .perform(
            put("/customers/{id}", customerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Phone 0341112234 is already used"));
  }

  // ==================== DELETE ====================

  @Test
  void delete_shouldReturn204() throws Exception {
    doNothing().when(customerService).delete(customerId);

    mockMvc.perform(delete("/customers/{id}", customerId)).andExpect(status().isNoContent());
  }

  @Test
  void delete_shouldReturn404WhenNotFound() throws Exception {
    doThrow(new NotFoundException("Customer with id " + customerId + " not found"))
        .when(customerService)
        .delete(customerId);

    mockMvc
        .perform(delete("/customers/{id}", customerId))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Customer with id " + customerId + " not found"));
  }
}
