package com.hei.school.endpoint.rest.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hei.school.dto.request.CreateSaleItemRequest;
import com.hei.school.dto.request.CreateSaleRequest;
import com.hei.school.dto.request.UpdateSaleRequest;
import com.hei.school.dto.response.SaleResponse;
import com.hei.school.exception.NotFoundException;
import com.hei.school.service.SaleService;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SaleController.class)
class SaleControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private SaleService saleService;

  private final UUID saleId = UUID.randomUUID();
  private final UUID customerId = UUID.randomUUID();
  private final UUID libraryId = UUID.randomUUID();
  private final UUID bookCopyId = UUID.randomUUID();
  private final Instant now = Instant.now();

  private final SaleResponse saleResponse =
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
          now,
          now);

  // ==================== CREATE ====================

  @Test
  void create_shouldReturn201() throws Exception {
    List<CreateSaleItemRequest> saleItems = new ArrayList<>();
    saleItems.add(new CreateSaleItemRequest(bookCopyId, 3, new BigDecimal("28000")));

    CreateSaleRequest request =
        new CreateSaleRequest(
            customerId, libraryId, Instant.parse("2024-01-15T10:00:00Z"), saleItems);

    when(saleService.create(any(CreateSaleRequest.class))).thenReturn(saleResponse);

    mockMvc
        .perform(
            post("/sales")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(saleId.toString()))
        .andExpect(jsonPath("$.customerId").value(customerId.toString()))
        .andExpect(jsonPath("$.customerFirstName").value("Rakoto"))
        .andExpect(jsonPath("$.totalAmount").value(84000))
        .andExpect(jsonPath("$.saleItems").isArray());
  }

  @Test
  void create_shouldReturn400WhenInvalidRequest() throws Exception {
    CreateSaleRequest request = new CreateSaleRequest(null, null, null, null);

    mockMvc
        .perform(
            post("/sales")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_shouldReturn400WhenSaleItemsEmpty() throws Exception {
    CreateSaleRequest request =
        new CreateSaleRequest(
            customerId, libraryId, Instant.parse("2024-01-15T10:00:00Z"), new ArrayList<>());

    mockMvc
        .perform(
            post("/sales")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  // ==================== GET ALL ====================

  @Test
  void getAll_shouldReturnList() throws Exception {
    when(saleService.getAll(null, null, null, null)).thenReturn(List.of(saleResponse));

    mockMvc
        .perform(get("/sales"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].id").value(saleId.toString()))
        .andExpect(jsonPath("$[0].customerId").value(customerId.toString()));
  }

  @Test
  void getAll_FilterByCustomerId() throws Exception {
    when(saleService.getAll(customerId, null, null, null)).thenReturn(List.of(saleResponse));

    mockMvc
        .perform(get("/sales").param("customerId", customerId.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].customerId").value(customerId.toString()));
  }

  @Test
  void getAll_FilterByLibraryId() throws Exception {
    when(saleService.getAll(null, libraryId, null, null)).thenReturn(List.of(saleResponse));

    mockMvc
        .perform(get("/sales").param("libraryId", libraryId.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].libraryId").value(libraryId.toString()));
  }

  @Test
  void getAll_FilterByDateRange() throws Exception {
    Instant from = Instant.parse("2024-01-01T00:00:00Z");
    Instant to = Instant.parse("2024-01-31T23:59:59Z");

    when(saleService.getAll(null, null, from, to)).thenReturn(List.of(saleResponse));

    mockMvc
        .perform(get("/sales").param("from", from.toString()).param("to", to.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(saleId.toString()));
  }

  // ==================== GET BY ID ====================

  @Test
  void getById_shouldReturnSale() throws Exception {
    when(saleService.getById(saleId)).thenReturn(saleResponse);

    mockMvc
        .perform(get("/sales/{id}", saleId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(saleId.toString()))
        .andExpect(jsonPath("$.customerId").value(customerId.toString()))
        .andExpect(jsonPath("$.totalAmount").value(84000))
        .andExpect(jsonPath("$.libraryName").value("Central Library"));
  }

  @Test
  void getById_shouldReturn404WhenNotFound() throws Exception {
    when(saleService.getById(saleId))
        .thenThrow(new NotFoundException("Sale with id " + saleId + " not found"));

    mockMvc.perform(get("/sales/{id}", saleId)).andExpect(status().isNotFound());
  }

  // ==================== UPDATE ====================

  @Test
  void update_shouldReturnUpdatedSale() throws Exception {
    List<CreateSaleItemRequest> saleItems = new ArrayList<>();
    saleItems.add(new CreateSaleItemRequest(bookCopyId, 2, new BigDecimal("30000")));

    UpdateSaleRequest request =
        new UpdateSaleRequest(Instant.parse("2024-01-20T10:00:00Z"), saleItems);

    when(saleService.update(any(UUID.class), any(UpdateSaleRequest.class)))
        .thenReturn(saleResponse);

    mockMvc
        .perform(
            put("/sales/{id}", saleId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(saleId.toString()))
        .andExpect(jsonPath("$.customerId").value(customerId.toString()));
  }

  @Test
  void update_shouldReturn404WhenNotFound() throws Exception {
    List<CreateSaleItemRequest> saleItems = new ArrayList<>();
    saleItems.add(new CreateSaleItemRequest(bookCopyId, 2, new BigDecimal("30000")));

    UpdateSaleRequest request =
        new UpdateSaleRequest(Instant.parse("2024-01-20T10:00:00Z"), saleItems);

    when(saleService.update(any(UUID.class), any(UpdateSaleRequest.class)))
        .thenThrow(new NotFoundException("Sale with id " + saleId + " not found"));

    mockMvc
        .perform(
            put("/sales/{id}", saleId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound());
  }

  @Test
  void update_shouldReturn400WhenInvalidRequest() throws Exception {
    UpdateSaleRequest request = new UpdateSaleRequest(null, null);

    mockMvc
        .perform(
            put("/sales/{id}", saleId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  // ==================== DELETE ====================

  @Test
  void delete_shouldReturn204() throws Exception {
    doNothing().when(saleService).delete(saleId);

    mockMvc.perform(delete("/sales/{id}", saleId)).andExpect(status().isNoContent());
  }

  @Test
  void delete_shouldReturn404WhenNotFound() throws Exception {
    doThrow(new NotFoundException("Sale with id " + saleId + " not found"))
        .when(saleService)
        .delete(saleId);

    mockMvc.perform(delete("/sales/{id}", saleId)).andExpect(status().isNotFound());
  }
}
