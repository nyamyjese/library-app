package com.hei.school.endpoint.rest.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hei.school.dto.request.CreateBookCopyRequest;
import com.hei.school.dto.request.LowStockResponse;
import com.hei.school.dto.request.UpdateBookCopyRequest;
import com.hei.school.dto.response.BookCopyResponse;
import com.hei.school.dto.response.BookStockResponse;
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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(BookCopyController.class)
class BookCopyControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private BookCopyService bookCopyService;

  @MockBean private StockMovementService stockMovementService;

  private final UUID bookCopyId = UUID.randomUUID();
  private final UUID bookId = UUID.randomUUID();
  private final UUID libraryId = UUID.randomUUID();

  private final BookCopyResponse bookCopyResponse =
      new BookCopyResponse(
          bookCopyId,
          bookId,
          "Test Book",
          libraryId,
          "Test Library",
          BookCopyFormat.PHYSICAL,
          "9781234567890",
          new BigDecimal("29.99"),
          BookCopyStatus.AVAILABLE);

  @Test
  void create_shouldReturn201() throws Exception {
    CreateBookCopyRequest request =
        new CreateBookCopyRequest(
            bookId,
            libraryId,
            BookCopyFormat.PHYSICAL,
            "9781234567890",
            new BigDecimal("29.99"),
            BookCopyStatus.AVAILABLE);

    when(bookCopyService.create(any(CreateBookCopyRequest.class))).thenReturn(bookCopyResponse);

    mockMvc
        .perform(
            post("/book-copies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(bookCopyId.toString()))
        .andExpect(jsonPath("$.bookId").value(bookId.toString()))
        .andExpect(jsonPath("$.format").value("PHYSICAL"))
        .andExpect(jsonPath("$.status").value("AVAILABLE"));
  }

  @Test
  void getAll_shouldReturnList() throws Exception {
    when(bookCopyService.getAll(null, null, null)).thenReturn(List.of(bookCopyResponse));

    mockMvc
        .perform(get("/book-copies"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].id").value(bookCopyId.toString()));
  }

  @Test
  void getAll_withFilters() throws Exception {
    UUID filterBookId = UUID.randomUUID();
    when(bookCopyService.getAll(filterBookId, null, null)).thenReturn(List.of(bookCopyResponse));

    mockMvc
        .perform(get("/book-copies").param("bookId", filterBookId.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(bookCopyId.toString()));
  }

  @Test
  void getById_shouldReturnBookCopy() throws Exception {
    when(bookCopyService.getById(bookCopyId)).thenReturn(bookCopyResponse);

    mockMvc
        .perform(get("/book-copies/{id}", bookCopyId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(bookCopyId.toString()))
        .andExpect(jsonPath("$.bookTitle").value("Test Book"));
  }

  @Test
  void update_shouldReturnUpdatedBookCopy() throws Exception {
    UpdateBookCopyRequest request =
        new UpdateBookCopyRequest(
            BookCopyFormat.DIGITAL, new BigDecimal("19.99"), BookCopyStatus.DAMAGED);

    BookCopyResponse updatedResponse =
        new BookCopyResponse(
            bookCopyId,
            bookId,
            "Test Book",
            libraryId,
            "Test Library",
            BookCopyFormat.DIGITAL,
            "9781234567890",
            new BigDecimal("19.99"),
            BookCopyStatus.DAMAGED);

    when(bookCopyService.update(any(UUID.class), any(UpdateBookCopyRequest.class)))
        .thenReturn(updatedResponse);

    mockMvc
        .perform(
            put("/book-copies/{id}", bookCopyId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.format").value("DIGITAL"))
        .andExpect(jsonPath("$.status").value("DAMAGED"));
  }

  @Test
  void delete_shouldReturn204() throws Exception {
    mockMvc.perform(delete("/book-copies/{id}", bookCopyId)).andExpect(status().isNoContent());
  }

  @Test
  void getStock_shouldReturnStock() throws Exception {
    BookStockResponse stockResponse =
        new BookStockResponse(bookCopyId, "Test Book", "9781234567890", 10, 3, 7);

    when(stockMovementService.getStockByBookCopyId(bookCopyId)).thenReturn(stockResponse);

    mockMvc
        .perform(get("/book-copies/{bookCopyId}/stock", bookCopyId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.bookCopyId").value(bookCopyId.toString()))
        .andExpect(jsonPath("$.currentStock").value(7));
  }

  @Test
  void getLowStock_shouldReturnList() throws Exception {
    LowStockResponse lowStock =
        new LowStockResponse(
            bookCopyId, bookId, "Test Book", BookCopyFormat.PHYSICAL, "9781234567890", 2);

    when(bookCopyService.getLowStock(5)).thenReturn(List.of(lowStock));

    mockMvc
        .perform(get("/book-copies/low-stock").param("threshold", "5"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].bookCopyId").value(bookCopyId.toString()))
        .andExpect(jsonPath("$[0].currentStock").value(2));
  }

  @Test
  void getLowStock_withoutThreshold() throws Exception {
    LowStockResponse lowStock =
        new LowStockResponse(
            bookCopyId, bookId, "Test Book", BookCopyFormat.PHYSICAL, "9781234567890", 1);

    when(bookCopyService.getLowStock(null)).thenReturn(List.of(lowStock));

    mockMvc
        .perform(get("/book-copies/low-stock"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].bookCopyId").value(bookCopyId.toString()));
  }
}
