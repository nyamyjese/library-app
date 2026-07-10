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
import com.hei.school.dto.request.GenreCreateRequest;
import com.hei.school.dto.request.GenreUpdateRequest;
import com.hei.school.dto.response.GenreResponse;
import com.hei.school.dto.response.GenreRevenueResponse;
import com.hei.school.service.GenreService;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(GenreController.class)
class GenreControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private GenreService genreService;

  private final UUID genreId = UUID.randomUUID();
  private final UUID bookId = UUID.randomUUID();
  private final GenreResponse genreResponse = new GenreResponse(genreId, "Fiction");

  // ----- POST /genres -----
  @Test
  void createGenre_shouldReturnCreated() throws Exception {
    GenreCreateRequest request = new GenreCreateRequest("Fiction");
    when(genreService.createGenre(any(GenreCreateRequest.class))).thenReturn(genreResponse);

    mockMvc
        .perform(
            post("/genres")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(genreId.toString()))
        .andExpect(jsonPath("$.name").value("Fiction"));

    verify(genreService).createGenre(any(GenreCreateRequest.class));
  }

  // ----- GET /genres/{genreId}/revenue -----
  @Test
  void getRevenue_shouldReturnRevenue() throws Exception {
    BigDecimal revenue = new BigDecimal("150.00");
    GenreRevenueResponse revenueResponse = new GenreRevenueResponse(genreId, "Fiction", revenue);
    when(genreService.getRevenue(genreId)).thenReturn(revenueResponse);

    mockMvc
        .perform(get("/genres/{genreId}/revenue", genreId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.genreId").value(genreId.toString()))
        .andExpect(jsonPath("$.name").value("Fiction"))
        .andExpect(jsonPath("$.revenue").value(150.00));

    verify(genreService).getRevenue(genreId);
  }

  // ----- PUT /genres/{genreId} -----
  @Test
  void updateGenre_shouldReturnUpdated() throws Exception {
    GenreUpdateRequest request = new GenreUpdateRequest("Mystery");
    GenreResponse updated = new GenreResponse(genreId, "Mystery");
    when(genreService.updateGenre(eq(genreId), any(GenreUpdateRequest.class))).thenReturn(updated);

    mockMvc
        .perform(
            put("/genres/{genreId}", genreId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Mystery"));

    verify(genreService).updateGenre(eq(genreId), any(GenreUpdateRequest.class));
  }

  // ----- DELETE /genres/{genreId} -----
  @Test
  void deleteGenre_shouldReturnNoContent() throws Exception {
    doNothing().when(genreService).deleteGenre(genreId);

    mockMvc.perform(delete("/genres/{genreId}", genreId)).andExpect(status().isNoContent());

    verify(genreService).deleteGenre(genreId);
  }

  // ----- GET /genres/{genreId} -----
  @Test
  void getGenreById_shouldReturnGenre() throws Exception {
    when(genreService.getGenreById(genreId)).thenReturn(genreResponse);

    mockMvc
        .perform(get("/genres/{genreId}", genreId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(genreId.toString()))
        .andExpect(jsonPath("$.name").value("Fiction"));

    verify(genreService).getGenreById(genreId);
  }

  // ----- GET /genres?name=... -----
  @Test
  void getAllGenres_withNameParam_shouldSearch() throws Exception {
    String name = "Fic";
    when(genreService.searchGenresByName(name)).thenReturn(List.of(genreResponse));

    mockMvc
        .perform(get("/genres").param("name", name))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)));

    verify(genreService).searchGenresByName(name);
  }

  @Test
  void getAllGenres_withoutNameParam_shouldReturnAll() throws Exception {
    when(genreService.getAllGenres()).thenReturn(List.of(genreResponse));

    mockMvc.perform(get("/genres")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)));

    verify(genreService).getAllGenres();
  }

  // ----- POST /genres/{genreId}/books/{bookId} -----
  @Test
  void addGenreToBook_shouldReturnCreated() throws Exception {
    doNothing().when(genreService).addGenreToBook(bookId, genreId);

    mockMvc
        .perform(post("/genres/{genreId}/books/{bookId}", genreId, bookId))
        .andExpect(status().isCreated());

    verify(genreService).addGenreToBook(bookId, genreId);
  }

  // ----- DELETE /genres/{genreId}/books/{bookId} -----
  @Test
  void removeGenreFromBook_shouldReturnNoContent() throws Exception {
    doNothing().when(genreService).removeGenreFromBook(bookId, genreId);

    mockMvc
        .perform(delete("/genres/{genreId}/books/{bookId}", genreId, bookId))
        .andExpect(status().isNoContent());

    verify(genreService).removeGenreFromBook(bookId, genreId);
  }

  // ----- GET /genres/books/{bookId}/genres -----
  @Test
  void getGenresByBook_shouldReturnList() throws Exception {
    when(genreService.getGenresByBook(bookId)).thenReturn(List.of(genreResponse));

    mockMvc
        .perform(get("/genres/books/{bookId}/genres", bookId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)));

    verify(genreService).getGenresByBook(bookId);
  }

  // ----- GET /genres/{genreId}/books -----
  @Test
  void getBooksByGenre_shouldReturnListOfBookIds() throws Exception {
    List<UUID> bookIds = List.of(bookId, UUID.randomUUID());
    when(genreService.getBooksByGenre(genreId)).thenReturn(bookIds);

    mockMvc
        .perform(get("/genres/{genreId}/books", genreId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)));

    verify(genreService).getBooksByGenre(genreId);
  }
}
