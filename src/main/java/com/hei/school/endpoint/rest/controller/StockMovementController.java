package com.hei.school.endpoint.rest.controller;

import com.hei.school.dto.response.StockMovementResponse;
import com.hei.school.entity.enums.MovementReason;
import com.hei.school.entity.enums.MovementType;
import com.hei.school.service.StockMovementService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stock-movements")
@RequiredArgsConstructor
public class StockMovementController {

  private final StockMovementService stockMovementService;

  @GetMapping
  public List<StockMovementResponse> getAll(
      @RequestParam(required = false) UUID bookCopyId,
      @RequestParam(required = false) MovementType movementType,
      @RequestParam(required = false) MovementReason reason,
      @RequestParam(required = false) Instant from,
      @RequestParam(required = false) Instant to) {
    return stockMovementService.getAll(bookCopyId, movementType, reason, from, to);
  }

  @GetMapping("/{id}")
  public StockMovementResponse getById(@PathVariable UUID id) {
    return stockMovementService.getById(id);
  }

  @PutMapping("/{id}")
  public StockMovementResponse update(@PathVariable UUID id) {
    return stockMovementService.update(id);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable UUID id) {
    stockMovementService.delete(id);
  }
}
