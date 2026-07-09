package com.hei.school.endpoint.rest.controller;

import com.hei.school.dto.request.CreateArrivalRequest;
import com.hei.school.dto.request.UpdateArrivalRequest;
import com.hei.school.dto.response.ArrivalResponse;
import com.hei.school.service.ArrivalService;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/arrivals")
@RequiredArgsConstructor
public class ArrivalController {

  private final ArrivalService arrivalService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ArrivalResponse create(@RequestBody @Valid CreateArrivalRequest request) {
    return arrivalService.create(request);
  }

  @GetMapping
  public List<ArrivalResponse> getAll(
      @RequestParam(required = false) UUID bookCopyId,
      @RequestParam(required = false) Instant from,
      @RequestParam(required = false) Instant to) {
    return arrivalService.getAll(bookCopyId, from, to);
  }

  @GetMapping("/{id}")
  public ArrivalResponse getById(@PathVariable UUID id) {
    return arrivalService.getById(id);
  }

  @PutMapping("/{id}")
  public ArrivalResponse update(
      @PathVariable UUID id, @RequestBody @Valid UpdateArrivalRequest request) {
    return arrivalService.update(id, request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable UUID id) {
    arrivalService.delete(id);
  }
}
