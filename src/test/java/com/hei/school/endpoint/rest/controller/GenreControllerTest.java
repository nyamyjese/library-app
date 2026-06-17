package com.hei.school.endpoint.rest.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hei.school.dto.request.GenreCreateRequest;
import com.hei.school.dto.request.GenreUpdateRequest;
import com.hei.school.dto.response.GenreResponse;
import com.hei.school.service.GenreService;
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

@WebMvcTest(GenreController.class)
class GenreControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private GenreService genreService;

  private final UUID genreId = UUID.randomUUID();
  private final UUID bookId = UUID.randomUUID();

  private final GenreResponse genreResponse = new GenreResponse(genreId, "Fantasy");
  private final GenreCreateRequest createRequest = new GenreCreateRequest("Science Fiction");
  private final GenreUpdateRequest updateRequest =
      new GenreUpdateRequest(genreId, "Fantasy Updated");

  @Test
  void testCreateGenre() throws Exception {
    when(genreService.createGenre(any(GenreCreateRequest.class)))
        .thenReturn(new GenreResponse(UUID.randomUUID(), "Science Fiction"));

    mockMvc
        .perform(
            post("/api/v1/genres")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("Science Fiction"));
  }

  @Test
  void testUpdateGenre() throws Exception {
    when(genreService.updateGenre(any(GenreUpdateRequest.class))).thenReturn(genreResponse);

    mockMvc
        .perform(
            put("/api/v1/genres")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Fantasy"));
  }

  @Test
  void testUpdateGenre_NotFound() throws Exception {
    when(genreService.updateGenre(any(GenreUpdateRequest.class)))
        .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Genre not found"));

    mockMvc
        .perform(
            put("/api/v1/genres")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isNotFound());
  }

  @Test
  void testDeleteGenre() throws Exception {
    doNothing().when(genreService).deleteGenre(genreId);

    mockMvc.perform(delete("/api/v1/genres/{genreId}", genreId)).andExpect(status().isNoContent());
  }

  @Test
  void testDeleteGenre_NotFound() throws Exception {
    doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Genre not found"))
        .when(genreService)
        .deleteGenre(genreId);

    mockMvc.perform(delete("/api/v1/genres/{genreId}", genreId)).andExpect(status().isNotFound());
  }

  @Test
  void testGetGenreById() throws Exception {
    when(genreService.getGenreById(genreId)).thenReturn(genreResponse);

    mockMvc
        .perform(get("/api/v1/genres/{genreId}", genreId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Fantasy"));
  }

  @Test
  void testGetGenreById_NotFound() throws Exception {
    when(genreService.getGenreById(genreId))
        .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Genre not found"));

    mockMvc.perform(get("/api/v1/genres/{genreId}", genreId)).andExpect(status().isNotFound());
  }

  @Test
  void testGetAllGenres() throws Exception {
    when(genreService.getAllGenres()).thenReturn(List.of(genreResponse));

    mockMvc
        .perform(get("/api/genres"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].name").value("Fantasy"));
  }

  @Test
  void testSearchGenresByName() throws Exception {
    when(genreService.searchGenresByName("Fantasy")).thenReturn(List.of(genreResponse));

    mockMvc
        .perform(get("/api/genres?name=Fantasy"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)));
  }

  @Test
  void testAddGenreToBook() throws Exception {
    doNothing().when(genreService).addGenreToBook(bookId, genreId);

    mockMvc
        .perform(post("/api/genres/{genreId}/books/{bookId}", genreId, bookId))
        .andExpect(status().isCreated());
  }

  @Test
  void testAddGenreToBook_BookNotFound() throws Exception {
    doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"))
        .when(genreService)
        .addGenreToBook(bookId, genreId);

    mockMvc
        .perform(post("/api/genres/{genreId}/books/{bookId}", genreId, bookId))
        .andExpect(status().isNotFound());
  }

  @Test
  void testRemoveGenreFromBook() throws Exception {
    doNothing().when(genreService).removeGenreFromBook(bookId, genreId);

    mockMvc
        .perform(delete("/api/genres/{genreId}/books/{bookId}", genreId, bookId))
        .andExpect(status().isNoContent());
  }

  @Test
  void testGetGenresByBook() throws Exception {
    when(genreService.getGenresByBook(bookId)).thenReturn(List.of(genreResponse));

    mockMvc
        .perform(get("/api/genres/books/{bookId}/genres", bookId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)));
  }

  @Test
  void testGetBooksByGenre() throws Exception {
    when(genreService.getBooksByGenre(genreId)).thenReturn(List.of(bookId));

    mockMvc
        .perform(get("/api/genres/{genreId}/books", genreId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0]").value(bookId.toString()));
  }
}
