package com.hei.school.endpoint.rest.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

  @MockBean private StockMovementService stockMovementService;

  private final UUID movementId = UUID.randomUUID();
  private final UUID bookCopyId = UUID.randomUUID();
  private final UUID arrivalId = UUID.randomUUID();
  private final StockMovementResponse response =
      new StockMovementResponse(
          movementId,
          10,
          MovementType.IN,
          MovementReason.ARRIVAL,
          bookCopyId,
          "Test Book",
          "978-1234567890",
          arrivalId,
          null,
          Instant.now());

  @Test
  void recordArrivalMovement() throws Exception {
    when(stockMovementService.recordArrivalMovement(bookCopyId, arrivalId)).thenReturn(response);

    mockMvc
        .perform(
            post("/stock-movements/arrival")
                .param("bookCopyId", bookCopyId.toString())
                .param("arrivalId", arrivalId.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(movementId.toString()));
  }

  @Test
  void getAll() throws Exception {
    when(stockMovementService.getAll()).thenReturn(List.of(response));

    mockMvc
        .perform(get("/stock-movements"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(1));
  }

  @Test
  void getById() throws Exception {
    when(stockMovementService.getById(movementId)).thenReturn(response);

    mockMvc
        .perform(get("/stock-movements/{id}", movementId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(movementId.toString()));
  }

  @Test
  void getByBookCopyId() throws Exception {
    when(stockMovementService.getByBookCopyId(any())).thenReturn(List.of(response));

    mockMvc
        .perform(get("/stock-movements").param("bookCopyId", bookCopyId.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(1));
  }

  @Test
  void getByArrivalId() throws Exception {
    when(stockMovementService.getByArrivalId(any())).thenReturn(List.of(response));

    mockMvc
        .perform(get("/stock-movements").param("arrivalId", arrivalId.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(1));
  }

  @Test
  void getByMovementType() throws Exception {
    when(stockMovementService.getByMovementType(MovementType.IN)).thenReturn(List.of(response));

    mockMvc
        .perform(get("/stock-movements").param("movementType", MovementType.IN.name()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(1));
  }

  @Test
  void getByReason() throws Exception {
    when(stockMovementService.getByReason(MovementReason.ARRIVAL)).thenReturn(List.of(response));

    mockMvc
        .perform(get("/stock-movements").param("reason", MovementReason.ARRIVAL.name()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(1));
  }
}
