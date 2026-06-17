package com.hei.school.endpoint.rest.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hei.school.dto.request.AuthorCreateRequest;
import com.hei.school.dto.request.AuthorUpdateRequest;
import com.hei.school.dto.response.AuthorResponse;
import com.hei.school.entity.enums.Sexe;
import com.hei.school.service.AuthorService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

@WebMvcTest(AuthorController.class)
class AuthorControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private AuthorService authorService;

  private final UUID authorId = UUID.randomUUID();
  private final UUID bookId = UUID.randomUUID();

  private final AuthorResponse authorResponse = new AuthorResponse(authorId, "John", "Doe");
  private final AuthorCreateRequest createRequest =
      new AuthorCreateRequest("Jane", "Smith", Sexe.F, null);
  private final AuthorUpdateRequest updateRequest =
      new AuthorUpdateRequest(authorId, "John", "DoeUpdated");

  @Test
  void testCreateAuthor() throws Exception {
    when(authorService.createAuthor(any(AuthorCreateRequest.class)))
        .thenReturn(new AuthorResponse(UUID.randomUUID(), "Jane", "Smith"));

    mockMvc
        .perform(
            post("/api/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.firstName").value("Jane"))
        .andExpect(jsonPath("$.lastName").value("Smith"));
  }

  @Test
  void testUpdateAuthor() throws Exception {
    when(authorService.updateAuthor(any(AuthorUpdateRequest.class))).thenReturn(authorResponse);

    mockMvc
        .perform(
            put("/api/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.firstName").value("John"))
        .andExpect(jsonPath("$.lastName").value("Doe"));
  }

  @Test
  void testUpdateAuthor_NotFound() throws Exception {
    when(authorService.updateAuthor(any(AuthorUpdateRequest.class)))
        .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Author not found"));

    mockMvc
        .perform(
            put("/api/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isNotFound());
  }

  @Test
  void testDeleteAuthor() throws Exception {
    doNothing().when(authorService).deleteAuthor(authorId);

    mockMvc.perform(delete("/api/authors/{authorId}", authorId)).andExpect(status().isNoContent());
  }

  @Test
  void testDeleteAuthor_NotFound() throws Exception {
    doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Author not found"))
        .when(authorService)
        .deleteAuthor(authorId);

    mockMvc.perform(delete("/api/authors/{authorId}", authorId)).andExpect(status().isNotFound());
  }

  @Test
  void testGetAuthorById() throws Exception {
    when(authorService.getAuthorById(authorId)).thenReturn(authorResponse);

    mockMvc
        .perform(get("/api/authors/{authorId}", authorId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.firstName").value("John"));
  }

  @Test
  void testGetAuthorById_NotFound() throws Exception {
    when(authorService.getAuthorById(authorId))
        .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Author not found"));

    mockMvc.perform(get("/api/authors/{authorId}", authorId)).andExpect(status().isNotFound());
  }

  @Test
  void testGetAllAuthors() throws Exception {
    when(authorService.getAllAuthors()).thenReturn(List.of(authorResponse));

    mockMvc
        .perform(get("/api/authors"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].firstName").value("John"));
  }

  @Test
  void testSearchAuthorsByLastName() throws Exception {
    when(authorService.searchAuthorsByLastName("Doe")).thenReturn(List.of(authorResponse));

    mockMvc
        .perform(get("/api/authors?lastName=Doe"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)));
  }

  @Test
  void testAddAuthorToBook() throws Exception {
    doNothing().when(authorService).addAuthorToBook(bookId, authorId);

    mockMvc
        .perform(post("/api/authors/{authorId}/books/{bookId}", authorId, bookId))
        .andExpect(status().isCreated());
  }

  @Test
  void testAddAuthorToBook_BookNotFound() throws Exception {
    doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"))
        .when(authorService)
        .addAuthorToBook(bookId, authorId);

    mockMvc
        .perform(post("/api/authors/{authorId}/books/{bookId}", authorId, bookId))
        .andExpect(status().isNotFound());
  }

  @Test
  void testRemoveAuthorFromBook() throws Exception {
    doNothing().when(authorService).removeAuthorFromBook(bookId, authorId);

    mockMvc
        .perform(delete("/api/authors/{authorId}/books/{bookId}", authorId, bookId))
        .andExpect(status().isNoContent());
  }

  @Test
  void testGetAuthorsByBook() throws Exception {
    when(authorService.getAuthorsByBook(bookId)).thenReturn(List.of(authorResponse));

    mockMvc
        .perform(get("/api/authors/books/{bookId}/authors", bookId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)));
  }

  @Test
  void testGetBooksByAuthor() throws Exception {
    when(authorService.getBooksByAuthor(authorId)).thenReturn(List.of(bookId));

    mockMvc
        .perform(get("/api/authors/{authorId}/books", authorId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0]").value(bookId.toString()));
  }
}
