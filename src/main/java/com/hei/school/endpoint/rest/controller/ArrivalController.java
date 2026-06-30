package com.hei.school.endpoint.rest.controller;

import com.hei.school.dto.ArrivalDTO;
import com.hei.school.service.ArrivalService;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
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

  @GetMapping
  public List<ArrivalDTO> getAll() {
    return arrivalService.getAll();
  }

  @GetMapping("/{id}")
  public ArrivalDTO getById(@PathVariable UUID id) {
    return arrivalService.getById(id);
  }

  @GetMapping(params = "bookCopyId")
  public List<ArrivalDTO> getByBookCopyId(@RequestParam UUID bookCopyId) {
    return arrivalService.getByBookCopyId(bookCopyId);
  }
}