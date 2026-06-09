package com.hei.school.endpoint.rest.controller;

import com.hei.school.dto.ArrivalDTO;
import com.hei.school.service.ArrivalService;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/arrivals")
public class ArrivalController {

  private final ArrivalService arrivalService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ArrivalDTO createArrival(@RequestBody ArrivalDTO dto) {
    return arrivalService.createArrival(dto);
  }

  @PutMapping("/{id}")
  public ArrivalDTO updateArrival(@PathVariable Integer id, @RequestBody ArrivalDTO dto) {
    return arrivalService.updateArrival(id, dto);
  }

  @GetMapping
  public List<ArrivalDTO> getAll() {
    return arrivalService.getAll();
  }

  @GetMapping("/{id}")
  public ArrivalDTO getById(@PathVariable Integer id) {
    return arrivalService.getById(id);
  }

  @GetMapping(params = "bookId")
  public List<ArrivalDTO> getByBook(@RequestParam Integer bookId) {
    return arrivalService.getByBook(bookId);
  }

  @GetMapping(params = "libraryId")
  public List<ArrivalDTO> getByLibrary(@RequestParam Integer libraryId) {
    return arrivalService.getByLibrary(libraryId);
  }

  @GetMapping(params = "date")
  public List<ArrivalDTO> getByDate(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
    return arrivalService.getByDate(date);
  }

  @GetMapping(params = {"from", "to"})
  public List<ArrivalDTO> getByDateRange(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
    return arrivalService.getByDateRange(from, to);
  }
}
