package com.hei.school.endpoint.rest.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthorController.class)
class AuthorControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private AuthorService authorService;

  private final UUID authorId = UUID.randomUUID();
  private final UUID bookId = UUID.randomUUID();
  private final AuthorResponse authorResponse = new AuthorResponse(authorId, "John", "Doe", Sexe.M);

  // ----- GET /api/authors -----
  @Test
  void getAll_shouldReturnListOfAuthors() throws Exception {
    when(authorService.getAll()).thenReturn(List.of(authorResponse));

    mockMvc
        .perform(get("/api/authors"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].id").value(authorId.toString()))
        .andExpect(jsonPath("$[0].firstName").value("John"))
        .andExpect(jsonPath("$[0].lastName").value("Doe"))
        .andExpect(jsonPath("$[0].sexe").value("M"));

    verify(authorService).getAll();
  }

  // ----- GET /api/authors/{id} -----
  @Test
  void getById_shouldReturnAuthor_whenExists() throws Exception {
    when(authorService.getById(authorId)).thenReturn(authorResponse);

    mockMvc
        .perform(get("/api/authors/{id}", authorId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(authorId.toString()))
        .andExpect(jsonPath("$.firstName").value("John"));

    verify(authorService).getById(authorId);
  }

  // ----- GET /api/authors/search?lastName=... -----
  @Test
  void searchByLastName_shouldReturnFilteredList() throws Exception {
    String lastName = "Doe";
    when(authorService.searchByLastName(lastName)).thenReturn(List.of(authorResponse));

    mockMvc
        .perform(get("/api/authors/search").param("lastName", lastName))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)));

    verify(authorService).searchByLastName(lastName);
  }

  // ----- POST /api/authors -----
  @Test
  void create_shouldReturnCreatedAuthor() throws Exception {
    AuthorCreateRequest request = new AuthorCreateRequest("John", "Doe", Sexe.M);
    when(authorService.create(any(AuthorCreateRequest.class))).thenReturn(authorResponse);

    mockMvc
        .perform(
            post("/api/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(authorId.toString()))
        .andExpect(jsonPath("$.firstName").value("John"));

    verify(authorService).create(any(AuthorCreateRequest.class));
  }

  // ----- PUT /api/authors/{id} -----
  @Test
  void update_shouldReturnUpdatedAuthor() throws Exception {
    AuthorUpdateRequest request = new AuthorUpdateRequest("Jane", "Smith");
    AuthorResponse updatedResponse = new AuthorResponse(authorId, "Jane", "Smith", Sexe.F);
    when(authorService.update(eq(authorId), any(AuthorUpdateRequest.class)))
        .thenReturn(updatedResponse);

    mockMvc
        .perform(
            put("/api/authors/{id}", authorId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.firstName").value("Jane"))
        .andExpect(jsonPath("$.lastName").value("Smith"));

    verify(authorService).update(eq(authorId), any(AuthorUpdateRequest.class));
  }

  // ----- DELETE /api/authors/{id} -----
  @Test
  void delete_shouldReturnNoContent() throws Exception {
    doNothing().when(authorService).delete(authorId);

    mockMvc.perform(delete("/api/authors/{id}", authorId)).andExpect(status().isNoContent());

    verify(authorService).delete(authorId);
  }

  // ----- POST /api/authors/{authorId}/books/{bookId} -----
  @Test
  void addAuthorToBook_shouldReturnNoContent() throws Exception {
    doNothing().when(authorService).addAuthorToBook(bookId, authorId);

    mockMvc
        .perform(post("/api/authors/{authorId}/books/{bookId}", authorId, bookId))
        .andExpect(status().isNoContent());

    verify(authorService).addAuthorToBook(bookId, authorId);
  }

  // ----- DELETE /api/authors/{authorId}/books/{bookId} -----
  @Test
  void removeAuthorFromBook_shouldReturnNoContent() throws Exception {
    doNothing().when(authorService).removeAuthorFromBook(bookId, authorId);

    mockMvc
        .perform(delete("/api/authors/{authorId}/books/{bookId}", authorId, bookId))
        .andExpect(status().isNoContent());

    verify(authorService).removeAuthorFromBook(bookId, authorId);
  }

  // ----- GET /api/authors/{authorId}/books -----
  @Test
  void getBooksByAuthor_shouldReturnListOfBookIds() throws Exception {
    List<UUID> bookIds = List.of(bookId, UUID.randomUUID());
    when(authorService.getBooksByAuthor(authorId)).thenReturn(bookIds);

    mockMvc
        .perform(get("/api/authors/{authorId}/books", authorId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0]").value(bookIds.get(0).toString()));

    verify(authorService).getBooksByAuthor(authorId);
  }

  // ----- GET /api/authors/books/{bookId}/authors -----
  @Test
  void getAuthorsByBook_shouldReturnListOfAuthors() throws Exception {
    when(authorService.getAuthorsByBook(bookId)).thenReturn(List.of(authorResponse));

    mockMvc
        .perform(get("/api/authors/books/{bookId}/authors", bookId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)));

    verify(authorService).getAuthorsByBook(bookId);
  }
}
