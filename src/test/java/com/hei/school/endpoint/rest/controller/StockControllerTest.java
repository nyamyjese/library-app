package com.hei.school.endpoint.rest.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.hei.school.dto.response.BookCopyStockResponse;
import com.hei.school.dto.response.BookStockStatusResponse;
import com.hei.school.dto.response.LowStockResponse;
import com.hei.school.service.StockService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(StockController.class)
class StockControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private StockService stockService;

  private final UUID bookId = UUID.randomUUID();
  private final BookStockStatusResponse bookStock =
      new BookStockStatusResponse(bookId, "Test Book", 5, 3, 1, 1, 0);

  @Test
  void getAllBooksStock() throws Exception {
    when(stockService.getAllBooksStock()).thenReturn(List.of(bookStock));

    mockMvc
        .perform(get("/stock/books"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(1));
  }

  @Test
  void getStockByBook() throws Exception {
    when(stockService.getStockByBook(bookId)).thenReturn(bookStock);

    mockMvc
        .perform(get("/stock/books/{bookId}", bookId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.bookId").value(bookId.toString()));
  }

  @Test
  void getStockByIsbn() throws Exception {
    var isbn = "978-1234567890";
    var response = new BookCopyStockResponse(UUID.randomUUID(), "Test Book", isbn, 2, 0, 1, 0);
    when(stockService.getStockByIsbn(isbn)).thenReturn(response);

    mockMvc
        .perform(get("/stock/editions/{isbn}", isbn))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.isbn").value(isbn));
  }

  @Test
  void getLowStock() throws Exception {
    var lowStock = new LowStockResponse(bookId, "Test Book", 2);
    when(stockService.getLowStockBooks(3)).thenReturn(List.of(lowStock));

    mockMvc
        .perform(get("/stock/low").param("threshold", "3"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(1));
  }

  @Test
  void getLowStockDefault() throws Exception {
    var lowStock = new LowStockResponse(bookId, "Test Book", 2);
    when(stockService.getLowStockBooks(3)).thenReturn(List.of(lowStock));

    mockMvc
        .perform(get("/stock/low"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(1));
  }
}
