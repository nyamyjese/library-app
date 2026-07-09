package com.hei.school.endpoint.rest.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
import org.springframework.http.MediaType;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ArrivalController.class)
class ArrivalControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @MockBean private ArrivalService arrivalService;

  private final UUID arrivalId = UUID.randomUUID();
  private final ArrivalResponse response =
      new ArrivalResponse(arrivalId, UUID.randomUUID(), "Test Book",
          "978-1234567890", 10, BigDecimal.valueOf(15.00),
          BigDecimal.valueOf(150.00), Instant.now());

  @Test
  void create() throws Exception {
    var request = new CreateArrivalRequest(UUID.randomUUID(), 10,
        BigDecimal.valueOf(15.00), Instant.now());
    when(arrivalService.create(any())).thenReturn(response);

    mockMvc.perform(post("/arrivals")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(arrivalId.toString()));
  }

  @Test
  void update() throws Exception {
    var request = new UpdateArrivalRequest(20, BigDecimal.valueOf(12.00), Instant.now());
    when(arrivalService.update(eq(arrivalId), any())).thenReturn(response);

    mockMvc.perform(put("/arrivals/{id}", arrivalId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(arrivalId.toString()));
  }

  @Test
  void getAll() throws Exception {
    when(arrivalService.getAll(null, null, null)).thenReturn(List.of(response));

    mockMvc.perform(get("/arrivals"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(1));
  }

  @Test
  void getById() throws Exception {
    when(arrivalService.getById(arrivalId)).thenReturn(response);

    mockMvc.perform(get("/arrivals/{id}", arrivalId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(arrivalId.toString()));
  }

  @Test
  void deleteArrival() throws Exception {
    doNothing().when(arrivalService).delete(arrivalId);
    mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete("/arrivals/{id}", arrivalId))
        .andExpect(status().isNoContent());
  }
}
