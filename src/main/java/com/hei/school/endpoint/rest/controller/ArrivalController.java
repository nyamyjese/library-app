package com.hei.school.endpoint.rest.controller;

import com.hei.school.dto.request.CreateArrivalRequest;
import com.hei.school.dto.request.UpdateArrivalRequest;
import com.hei.school.dto.response.ArrivalResponse;
import com.hei.school.service.ArrivalService;
import jakarta.validation.Valid;
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
  public ArrivalResponse createArrival(@Valid @RequestBody CreateArrivalRequest request) {
    return arrivalService.createArrival(request);
  }

  @PutMapping("/{id}")
  public ArrivalResponse updateArrival(
      @PathVariable UUID id, @Valid @RequestBody UpdateArrivalRequest request) {
    return arrivalService.updateArrival(id, request);
  }

  @GetMapping
  public List<ArrivalResponse> getAll() {
    return arrivalService.getAll();
  }

  @GetMapping("/{id}")
  public ArrivalResponse getById(@PathVariable UUID id) {
    return arrivalService.getById(id);
  }

  @GetMapping(params = "bookCopyId")
  public List<ArrivalResponse> getByBookCopyId(@RequestParam UUID bookCopyId) {
    return arrivalService.getByBookCopyId(bookCopyId);
  }

  @GetMapping(params = "bookId")
  public List<ArrivalResponse> getByBookId(@RequestParam UUID bookId) {
    return arrivalService.getByBookId(bookId);
  }
}
