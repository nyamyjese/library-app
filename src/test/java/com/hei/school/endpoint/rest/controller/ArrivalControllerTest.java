package com.hei.school.endpoint.rest.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hei.school.dto.request.CreateArrivalRequest;
import com.hei.school.dto.request.UpdateArrivalRequest;
import com.hei.school.dto.response.ArrivalResponse;
import com.hei.school.service.ArrivalService;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ArrivalController.class)
class ArrivalControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private ArrivalService arrivalService;

  private final UUID arrivalId = UUID.randomUUID();
  private final UUID bookCopyId = UUID.randomUUID();

  private final ArrivalResponse arrivalResponse =
      new ArrivalResponse(
          arrivalId,
          bookCopyId,
          "Test Book",
          "9781234567890",
          10,
          new BigDecimal("15.00"),
          new BigDecimal("150.00"),
          Instant.parse("2024-01-15T10:00:00Z"));

  @Test
  void create_shouldReturn201() throws Exception {
    CreateArrivalRequest request =
        new CreateArrivalRequest(
            bookCopyId, 10, new BigDecimal("15.00"), Instant.parse("2024-01-15T10:00:00Z"));

    when(arrivalService.create(any(CreateArrivalRequest.class))).thenReturn(arrivalResponse);

    mockMvc
        .perform(
            post("/arrivals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(arrivalId.toString()))
        .andExpect(jsonPath("$.bookCopyId").value(bookCopyId.toString()))
        .andExpect(jsonPath("$.quantity").value(10))
        .andExpect(jsonPath("$.unitPrice").value(15.00));
  }

  @Test
  void getAll_shouldReturnList() throws Exception {
    when(arrivalService.getAll(null, null, null)).thenReturn(List.of(arrivalResponse));

    mockMvc
        .perform(get("/arrivals"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].id").value(arrivalId.toString()));
  }

  @Test
  void getAll_withBookCopyIdFilter() throws Exception {
    when(arrivalService.getAll(bookCopyId, null, null)).thenReturn(List.of(arrivalResponse));

    mockMvc
        .perform(get("/arrivals").param("bookCopyId", bookCopyId.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].bookCopyId").value(bookCopyId.toString()));
  }

  @Test
  void getAll_withDateRangeFilter() throws Exception {
    Instant from = Instant.parse("2024-01-01T00:00:00Z");
    Instant to = Instant.parse("2024-01-31T23:59:59Z");
    when(arrivalService.getAll(null, from, to)).thenReturn(List.of(arrivalResponse));

    mockMvc
        .perform(get("/arrivals").param("from", from.toString()).param("to", to.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(arrivalId.toString()));
  }

  @Test
  void getById_shouldReturnArrival() throws Exception {
    when(arrivalService.getById(arrivalId)).thenReturn(arrivalResponse);

    mockMvc
        .perform(get("/arrivals/{id}", arrivalId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(arrivalId.toString()))
        .andExpect(jsonPath("$.bookTitle").value("Test Book"));
  }

  @Test
  void update_shouldReturnUpdated() throws Exception {
    UpdateArrivalRequest updateRequest =
        new UpdateArrivalRequest(
            20, new BigDecimal("16.00"), Instant.parse("2024-01-15T10:00:00Z"));

    ArrivalResponse updatedResponse =
        new ArrivalResponse(
            arrivalId,
            bookCopyId,
            "Test Book",
            "9781234567890",
            20,
            new BigDecimal("16.00"),
            new BigDecimal("320.00"),
            Instant.parse("2024-01-15T10:00:00Z"));

    when(arrivalService.update(arrivalId, updateRequest)).thenReturn(updatedResponse);

    mockMvc
        .perform(
            put("/arrivals/{id}", arrivalId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.quantity").value(20))
        .andExpect(jsonPath("$.unitPrice").value(16.00));
  }

  @Test
  void delete_shouldReturn204() throws Exception {
    mockMvc.perform(delete("/arrivals/{id}", arrivalId)).andExpect(status().isNoContent());
  }
}
