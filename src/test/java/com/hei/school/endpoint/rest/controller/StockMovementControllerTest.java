package com.hei.school.endpoint.rest.controller;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hei.school.dto.response.StockMovementResponse;
import com.hei.school.entity.enums.MovementReason;
import com.hei.school.entity.enums.MovementType;
import com.hei.school.service.StockMovementService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(StockMovementController.class)
class StockMovementControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private StockMovementService stockMovementService;

  private final UUID stockMovementId = UUID.randomUUID();
  private final UUID bookCopyId = UUID.randomUUID();
  private final UUID arrivalId = UUID.randomUUID();

  private final StockMovementResponse stockMovementResponse =
      new StockMovementResponse(
          stockMovementId,
          10,
          MovementType.IN,
          MovementReason.ARRIVAL,
          bookCopyId,
          "Test Book",
          "9781234567890",
          arrivalId,
          null,
          Instant.parse("2024-01-15T10:00:00Z"));

  @Test
  void getAll_shouldReturnList() throws Exception {
    when(stockMovementService.getAll(null, null, null, null, null))
        .thenReturn(List.of(stockMovementResponse));

    mockMvc
        .perform(get("/stock-movements"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].id").value(stockMovementId.toString()))
        .andExpect(jsonPath("$[0].movementType").value("IN"));
  }

  @Test
  void getAll_withBookCopyIdFilter() throws Exception {
    when(stockMovementService.getAll(bookCopyId, null, null, null, null))
        .thenReturn(List.of(stockMovementResponse));

    mockMvc
        .perform(get("/stock-movements").param("bookCopyId", bookCopyId.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].bookCopyId").value(bookCopyId.toString()));
  }

  @Test
  void getAll_withMovementTypeFilter() throws Exception {
    when(stockMovementService.getAll(null, MovementType.IN, null, null, null))
        .thenReturn(List.of(stockMovementResponse));

    mockMvc
        .perform(get("/stock-movements").param("movementType", "IN"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].movementType").value("IN"));
  }

  @Test
  void getAll_withReasonFilter() throws Exception {
    when(stockMovementService.getAll(null, null, MovementReason.ARRIVAL, null, null))
        .thenReturn(List.of(stockMovementResponse));

    mockMvc
        .perform(get("/stock-movements").param("reason", "ARRIVAL"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].reason").value("ARRIVAL"));
  }

  @Test
  void getAll_withDateRangeFilter() throws Exception {
    Instant from = Instant.parse("2024-01-01T00:00:00Z");
    Instant to = Instant.parse("2024-01-31T23:59:59Z");
    when(stockMovementService.getAll(null, null, null, from, to))
        .thenReturn(List.of(stockMovementResponse));

    mockMvc
        .perform(get("/stock-movements").param("from", from.toString()).param("to", to.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(stockMovementId.toString()));
  }

  @Test
  void getById_shouldReturnStockMovement() throws Exception {
    when(stockMovementService.getById(stockMovementId)).thenReturn(stockMovementResponse);

    mockMvc
        .perform(get("/stock-movements/{id}", stockMovementId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(stockMovementId.toString()))
        .andExpect(jsonPath("$.quantity").value(10))
        .andExpect(jsonPath("$.bookTitle").value("Test Book"));
  }

  @Test
  void update_shouldThrowForbiddenException() throws Exception {
    when(stockMovementService.update(stockMovementId))
        .thenThrow(
            new com.hei.school.exception.ForbiddenException(
                "StockMovement with id "
                    + stockMovementId
                    + " cannot be updated — stock movements are immutable"));

    mockMvc
        .perform(put("/stock-movements/{id}", stockMovementId))
        .andExpect(status().isForbidden());
  }

  @Test
  void delete_shouldThrowForbiddenException() throws Exception {
    doThrow(
            new com.hei.school.exception.ForbiddenException(
                "StockMovement with id "
                    + stockMovementId
                    + " cannot be deleted — stock movements are immutable"))
        .when(stockMovementService)
        .delete(stockMovementId);

    mockMvc
        .perform(delete("/stock-movements/{id}", stockMovementId))
        .andExpect(status().isForbidden());
  }
}
