package com.hei.school.endpoint.rest.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hei.school.dto.response.AuthorResponse;
import com.hei.school.entity.enums.Sexe;
import com.hei.school.service.AuthorService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

@WebMvcTest(controllers = AuthorController.class, useDefaultFilters = false)
class AuthorControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private AuthorService authorService;

  private final UUID authorId = UUID.randomUUID();
  private final UUID bookId = UUID.randomUUID();
  private AuthorResponse authorResponse;

  @BeforeEach
  void setUp() {
    authorResponse = new AuthorResponse(authorId, "John", "Doe", Sexe.M);
  }

  @Test
  void testAddAuthorToBook_Success() throws Exception {
    doNothing().when(authorService).addAuthorToBook(bookId, authorId);

    mockMvc
        .perform(post("/authors/{authorId}/books/{bookId}", authorId, bookId))
        .andExpect(status().isNoContent());
  }

  @Test
  void testAddAuthorToBook_BookNotFound() throws Exception {
    doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"))
        .when(authorService)
        .addAuthorToBook(bookId, authorId);

    mockMvc
        .perform(post("/authors/{authorId}/books/{bookId}", authorId, bookId))
        .andExpect(status().isNotFound());
  }

  @Test
  void testRemoveAuthorFromBook() throws Exception {
    doNothing().when(authorService).removeAuthorFromBook(bookId, authorId);

    mockMvc
        .perform(delete("/authors/{authorId}/books/{bookId}", authorId, bookId))
        .andExpect(status().isNoContent());
  }

  @Test
  void testGetAuthorsByBook() throws Exception {
    when(authorService.getAuthorsByBook(bookId)).thenReturn(List.of(authorResponse));

    mockMvc
        .perform(get("/authors/books/{bookId}/authors", bookId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].firstName").value("John"));
  }

  @Test
  void testGetBooksByAuthor() throws Exception {
    when(authorService.getBooksByAuthor(authorId)).thenReturn(List.of(bookId));

    mockMvc
        .perform(get("/authors/{authorId}/books", authorId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0]").value(bookId.toString()));
  }
}
