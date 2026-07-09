package com.hei.school.endpoint.rest.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hei.school.dto.LibraryDTO;
import com.hei.school.entity.Library;
import com.hei.school.service.LibraryService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(LibraryController.class)
class LibraryControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private LibraryService libraryService;

  private final UUID libraryId = UUID.randomUUID();

  private final LibraryDTO libraryDTO =
      new LibraryDTO(libraryId, "Test Library", "123 Main St", "0123456789");

  private Library buildLibrary() {
    Library library = new Library();
    library.setId(libraryId);
    library.setName("Test Library");
    library.setAddress("123 Main St");
    library.setPhone("0123456789");
    return library;
  }

  @Test
  void getAllLibraries_shouldReturnList() throws Exception {
    when(libraryService.getAll()).thenReturn(List.of(buildLibrary()));

    mockMvc
        .perform(get("/libraries"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].id").value(libraryId.toString()))
        .andExpect(jsonPath("$[0].name").value("Test Library"));
  }

  @Test
  void getLibraryById_shouldReturnLibrary() throws Exception {
    when(libraryService.getById(libraryId)).thenReturn(buildLibrary());

    mockMvc
        .perform(get("/libraries/{id}", libraryId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(libraryId.toString()))
        .andExpect(jsonPath("$.address").value("123 Main St"));
  }

  @Test
  void searchLibraries_shouldReturnList() throws Exception {
    when(libraryService.searchByName("Test")).thenReturn(List.of(buildLibrary()));

    mockMvc
        .perform(get("/libraries/search").param("name", "Test"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].name").value("Test Library"));
  }

  @Test
  void createLibrary_shouldReturnCreatedLibrary() throws Exception {
    when(libraryService.create(any(LibraryDTO.class))).thenReturn(buildLibrary());

    mockMvc
        .perform(
            post("/libraries")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(libraryDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(libraryId.toString()))
        .andExpect(jsonPath("$.name").value("Test Library"));
  }

  @Test
  void updateLibrary_shouldReturnUpdatedLibrary() throws Exception {
    when(libraryService.update(eq(libraryId), any(LibraryDTO.class))).thenReturn(buildLibrary());

    mockMvc
        .perform(
            put("/libraries/{id}", libraryId)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(libraryDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(libraryId.toString()))
        .andExpect(jsonPath("$.phone").value("0123456789"));
  }

  @Test
  void deleteLibrary_shouldReturnOk() throws Exception {
    doNothing().when(libraryService).delete(libraryId);

    mockMvc.perform(delete("/libraries/{id}", libraryId)).andExpect(status().isOk());
  }
}
