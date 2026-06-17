package com.hei.school.endpoint.rest.controller;

import com.hei.school.dto.ArrivalDTO;
import com.hei.school.service.ArrivalService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/arrivals")
public class ArrivalController {

  private final ArrivalService arrivalService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ArrivalDTO createArrival(@RequestBody ArrivalDTO dto) {
    return arrivalService.createArrival(dto);
  }

  @PutMapping("/{id}")
  public ArrivalDTO updateArrival(@PathVariable UUID id, @RequestBody ArrivalDTO dto) {
    return arrivalService.updateArrival(id, dto);
  }

  @GetMapping
  public List<ArrivalDTO> getAll() {
    return arrivalService.getAll();
  }

  @GetMapping("/{id}")
  public ArrivalDTO getById(@PathVariable UUID id) {
    return arrivalService.getById(id);
  }

  @GetMapping(params = "bookId")
  public List<ArrivalDTO> getByBook(@RequestParam UUID bookId) {
    return arrivalService.getByBook(bookId);
  }

  @GetMapping(params = "libraryId")
  public List<ArrivalDTO> getByLibrary(@RequestParam UUID libraryId) {
    return arrivalService.getByLibrary(libraryId);
  }

  @GetMapping(params = "date")
  public List<ArrivalDTO> getByDate(@RequestParam Instant date) {
    return arrivalService.getByDate(date);
  }

  @GetMapping(params = {"from", "to"})
  public List<ArrivalDTO> getByDateRange(
          @RequestParam Instant from,
          @RequestParam Instant to) {
    return arrivalService.getByDateRange(from, to);
  }
}