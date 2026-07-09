package com.hei.school.endpoint.rest.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hei.school.dto.BookDTO;
import com.hei.school.dto.response.BookTotalStockResponse;
import com.hei.school.service.BookService;
import com.hei.school.service.StockMovementService;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(BookController.class)
class BookControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private BookService bookService;

  @MockBean private StockMovementService stockMovementService;

  private final UUID bookId = UUID.randomUUID();
  private final UUID libraryId = UUID.randomUUID();

  private final BookDTO bookDTO =
      new BookDTO(bookId, "Test Book", "9781234567890", 2024, BigDecimal.valueOf(19.99), libraryId);

  @Test
  void getTotalStock_shouldReturnStockResponse() throws Exception {
    BookTotalStockResponse stockResponse = new BookTotalStockResponse(bookId, "Test Book", 42);
    when(stockMovementService.getTotalStockByBookId(bookId)).thenReturn(stockResponse);

    mockMvc
        .perform(get("/books/{bookId}/stock", bookId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.bookId").value(bookId.toString()))
        .andExpect(jsonPath("$.totalStock").value(42));
  }

  @Test
  void getAll_shouldReturnList() throws Exception {
    when(bookService.getAll()).thenReturn(List.of(bookDTO));

    mockMvc
        .perform(get("/books"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].id").value(bookId.toString()))
        .andExpect(jsonPath("$[0].title").value("Test Book"));
  }

  @Test
  void getById_shouldReturnBook() throws Exception {
    when(bookService.getById(bookId)).thenReturn(bookDTO);

    mockMvc
        .perform(get("/books/{id}", bookId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(bookId.toString()))
        .andExpect(jsonPath("$.isbn").value("9781234567890"));
  }

  @Test
  void searchByTitle_shouldReturnList() throws Exception {
    when(bookService.searchByTitle("Test")).thenReturn(List.of(bookDTO));

    mockMvc
        .perform(get("/books/search/title").param("title", "Test"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].title").value("Test Book"));
  }

  @Test
  void searchByIsbn_shouldReturnBook() throws Exception {
    when(bookService.searchByIsbn("9781234567890")).thenReturn(bookDTO);

    mockMvc
        .perform(get("/books/search/isbn").param("isbn", "9781234567890"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.isbn").value("9781234567890"));
  }

  @Test
  void getByLibrary_shouldReturnList() throws Exception {
    when(bookService.getByLibrary(libraryId)).thenReturn(List.of(bookDTO));

    mockMvc
        .perform(get("/books/library/{libraryId}", libraryId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(bookId.toString()));
  }

  @Test
  void getByYear_shouldReturnList() throws Exception {
    when(bookService.getByYear(2024)).thenReturn(List.of(bookDTO));

    mockMvc
        .perform(get("/books/filter/year").param("year", "2024"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].title").value("Test Book"));
  }

  @Test
  void sortedByTitle_shouldReturnList() throws Exception {
    when(bookService.getAllSortedByTitle()).thenReturn(List.of(bookDTO));

    mockMvc
        .perform(get("/books/sort/title"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].title").value("Test Book"));
  }

  @Test
  void sortedByPrice_shouldReturnList() throws Exception {
    when(bookService.getAllSortedByPrice()).thenReturn(List.of(bookDTO));

    mockMvc
        .perform(get("/books/sort/price"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(bookId.toString()));
  }

  @Test
  void create_shouldReturnCreatedBook() throws Exception {
    when(bookService.create(any(BookDTO.class))).thenReturn(bookDTO);

    mockMvc
        .perform(
            post("/books")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(bookDTO)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(bookId.toString()));
  }

  @Test
  void update_shouldReturnUpdatedBook() throws Exception {
    when(bookService.update(eq(bookId), any(BookDTO.class))).thenReturn(bookDTO);

    mockMvc
        .perform(
            put("/books/{id}", bookId)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(bookDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(bookId.toString()));
  }

  @Test
  void delete_shouldReturnNoContent() throws Exception {
    doNothing().when(bookService).delete(bookId);

    mockMvc.perform(delete("/books/{id}", bookId)).andExpect(status().isNoContent());
  }
}
