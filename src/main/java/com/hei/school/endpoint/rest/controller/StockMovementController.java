package com.hei.school.endpoint.rest.controller;

import com.hei.school.dto.response.StockMovementResponse;
import com.hei.school.entity.enums.MovementReason;
import com.hei.school.entity.enums.MovementType;
import com.hei.school.service.StockMovementService;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/stock-movements")
public class StockMovementController {

    private final StockMovementService stockMovementService;

    @PostMapping("/arrival")
    public StockMovementResponse recordArrivalMovement(
            @RequestParam UUID bookCopyId,
            @RequestParam UUID arrivalId) {
        return stockMovementService.recordArrivalMovement(bookCopyId, arrivalId);
    }

    @GetMapping
    public List<StockMovementResponse> getAll() {
        return stockMovementService.getAll();
    }

    @GetMapping("/{id}")
    public StockMovementResponse getById(@PathVariable UUID id) {
        return stockMovementService.getById(id);
    }

    @GetMapping(params = "bookCopyId")
    public List<StockMovementResponse> getByBookCopyId(@RequestParam UUID bookCopyId) {
        return stockMovementService.getByBookCopyId(bookCopyId);
    }

    @GetMapping(params = "arrivalId")
    public List<StockMovementResponse> getByArrivalId(@RequestParam UUID arrivalId) {
        return stockMovementService.getByArrivalId(arrivalId);
    }

    @GetMapping(params = "movementType")
    public List<StockMovementResponse> getByMovementType(@RequestParam MovementType movementType) {
        return stockMovementService.getByMovementType(movementType);
    }

    @GetMapping(params = "reason")
    public List<StockMovementResponse> getByReason(@RequestParam MovementReason reason) {
        return stockMovementService.getByReason(reason);
    }
}