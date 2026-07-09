package com.hei.school.endpoint.rest.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hei.school.dto.request.CreateBookCopyRequest;
import com.hei.school.dto.request.UpdateBookCopyRequest;
import com.hei.school.dto.response.BookCopyResponse;
import com.hei.school.entity.enums.BookCopyFormat;
import com.hei.school.entity.enums.BookCopyStatus;
import com.hei.school.service.BookCopyService;
import com.hei.school.service.StockMovementService;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(BookCopyController.class)
class BookCopyControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @MockBean private BookCopyService bookCopyService;
  @MockBean private StockMovementService stockMovementService;

  private final UUID copyId = UUID.randomUUID();
  private final BookCopyResponse response =
      new BookCopyResponse(copyId, UUID.randomUUID(), "Test Book", UUID.randomUUID(),
          "Main Library", BookCopyFormat.PHYSICAL, "978-1234567890",
          BigDecimal.valueOf(20.00), BookCopyStatus.AVAILABLE);

  @Test
  void getAll() throws Exception {
    when(bookCopyService.getAll(null, null, null)).thenReturn(List.of(response));
    mockMvc.perform(get("/book-copies"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(1));
  }

  @Test
  void getById() throws Exception {
    when(bookCopyService.getById(copyId)).thenReturn(response);
    mockMvc.perform(get("/book-copies/{id}", copyId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(copyId.toString()));
  }

  @Test
  void create() throws Exception {
    var request = new CreateBookCopyRequest(UUID.randomUUID(), UUID.randomUUID(),
        BookCopyFormat.PHYSICAL, "9781234567890", BigDecimal.valueOf(20.00),
        BookCopyStatus.AVAILABLE);
    when(bookCopyService.create(any())).thenReturn(response);

    mockMvc.perform(post("/book-copies")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(copyId.toString()));
  }

  @Test
  void update() throws Exception {
    var request = new UpdateBookCopyRequest(BookCopyFormat.DIGITAL,
        BigDecimal.valueOf(25.00), BookCopyStatus.DAMAGED);
    when(bookCopyService.update(eq(copyId), any())).thenReturn(response);

    mockMvc.perform(put("/book-copies/{id}", copyId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(copyId.toString()));
  }

  @Test
  void deleteBookCopy() throws Exception {
    doNothing().when(bookCopyService).delete(copyId);
    mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete("/book-copies/{id}", copyId))
        .andExpect(status().isNoContent());
  }
}
