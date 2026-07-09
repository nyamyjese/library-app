package com.hei.school.endpoint.rest.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hei.school.dto.BookCopyDTO;
import com.hei.school.entity.enums.BookCopyFormat;
import com.hei.school.entity.enums.BookCopyStatus;
import com.hei.school.service.BookCopyService;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(BookCopyController.class)
class BookCopyControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @MockBean private BookCopyService bookCopyService;

  private final UUID copyId = UUID.randomUUID();
  private final BookCopyDTO dto = BookCopyDTO.builder()
      .id(copyId)
      .bookId(UUID.randomUUID())
      .libraryId(UUID.randomUUID())
      .format(BookCopyFormat.PHYSICAL)
      .isbn("978-1234567890")
      .sellingPrice(BigDecimal.valueOf(20.00))
      .status(BookCopyStatus.AVAILABLE)
      .build();

  @Test
  void getAll() throws Exception {
    when(bookCopyService.getAll()).thenReturn(List.of(dto));
    mockMvc.perform(get("/book-copies"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(1));
  }

  @Test
  void getById() throws Exception {
    when(bookCopyService.getById(copyId)).thenReturn(dto);
    mockMvc.perform(get("/book-copies/{id}", copyId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(copyId.toString()));
  }

  @Test
  void getAvailable() throws Exception {
    when(bookCopyService.getAvailable()).thenReturn(List.of(dto));
    mockMvc.perform(get("/book-copies/available"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(1));
  }

  @Test
  void getByBook() throws Exception {
    when(bookCopyService.getByBook(any())).thenReturn(List.of(dto));
    mockMvc.perform(get("/book-copies").param("bookId", UUID.randomUUID().toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(1));
  }

  @Test
  void getAvailableByBook() throws Exception {
    when(bookCopyService.getAvailableByBook(any())).thenReturn(List.of(dto));
    mockMvc.perform(get("/book-copies/available").param("bookId", UUID.randomUUID().toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(1));
  }

  @Test
  void getByLibrary() throws Exception {
    when(bookCopyService.getByLibrary(any())).thenReturn(List.of(dto));
    mockMvc.perform(get("/book-copies").param("libraryId", UUID.randomUUID().toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(1));
  }

  @Test
  void countAvailableByBook() throws Exception {
    when(bookCopyService.countAvailableByBook(any())).thenReturn(5L);
    mockMvc.perform(get("/book-copies/count/available").param("bookId", UUID.randomUUID().toString()))
        .andExpect(status().isOk())
        .andExpect(content().string("5"));
  }

  @Test
  void updateStatus() throws Exception {
    when(bookCopyService.updateStatus(eq(copyId), eq(BookCopyStatus.DAMAGED))).thenReturn(dto);
    mockMvc.perform(patch("/book-copies/{id}/status", copyId)
            .param("status", "DAMAGED"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(copyId.toString()));
  }
}
