package com.hei.school.service;

import com.hei.school.dto.response.StockMovementResponse;
import com.hei.school.entity.Arrival;
import com.hei.school.entity.BookCopy;
import com.hei.school.entity.enums.MovementReason;
import com.hei.school.entity.enums.MovementType;
import com.hei.school.mapper.StockMovementMapper;
import com.hei.school.repository.ArrivalRepository;
import com.hei.school.repository.BookCopyRepository;
import com.hei.school.repository.StockMovementRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class StockMovementService {

  private final StockMovementRepository stockMovementRepository;
  private final BookCopyRepository bookCopyRepository;
  private final ArrivalRepository arrivalRepository;
  private final StockMovementMapper stockMovementMapper;

  @Transactional
  public StockMovementResponse recordArrivalMovement(UUID bookCopyId, UUID arrivalId) {
    BookCopy bookCopy =
        bookCopyRepository
            .findById(bookCopyId)
            .orElseThrow(() -> new RuntimeException("BookCopy not found: id=" + bookCopyId));
    Arrival arrival =
        arrivalRepository
            .findById(arrivalId)
            .orElseThrow(() -> new RuntimeException("Arrival not found: id=" + arrivalId));

    return stockMovementMapper.toResponse(
        stockMovementRepository.save(
            stockMovementMapper.toEntity(
                arrival.getQuantity(),
                MovementType.IN,
                MovementReason.ARRIVAL,
                bookCopy,
                arrival,
                null,
                Instant.now())));
  }

  public StockMovementResponse getById(UUID id) {
    return stockMovementMapper.toResponse(
        stockMovementRepository
            .findById(id)
            .orElseThrow(() -> new RuntimeException("StockMovement not found: id=" + id)));
  }

  public List<StockMovementResponse> getAll() {
    return stockMovementRepository.findAll().stream().map(stockMovementMapper::toResponse).toList();
  }

  public List<StockMovementResponse> getByBookCopyId(UUID bookCopyId) {
    return stockMovementRepository.findAllByBookCopy_Id(bookCopyId).stream()
        .map(stockMovementMapper::toResponse)
        .toList();
  }

  public List<StockMovementResponse> getByArrivalId(UUID arrivalId) {
    return stockMovementRepository.findAllByArrival_Id(arrivalId).stream()
        .map(stockMovementMapper::toResponse)
        .toList();
  }

  public List<StockMovementResponse> getByMovementType(MovementType movementType) {
    return stockMovementRepository.findAllByMovementType(movementType).stream()
        .map(stockMovementMapper::toResponse)
        .toList();
  }

  public List<StockMovementResponse> getByReason(MovementReason reason) {
    return stockMovementRepository.findAllByReason(reason).stream()
        .map(stockMovementMapper::toResponse)
        .toList();
  }
}
