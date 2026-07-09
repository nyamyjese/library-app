package com.hei.school.service;

import com.hei.school.dto.response.BookStockResponse;
import com.hei.school.dto.response.BookTotalStockResponse;
import com.hei.school.dto.response.StockMovementResponse;
import com.hei.school.entity.*;
import com.hei.school.entity.enums.MovementReason;
import com.hei.school.entity.enums.MovementType;
import com.hei.school.exception.BadRequestException;
import com.hei.school.exception.ForbiddenException;
import com.hei.school.exception.NotFoundException;
import com.hei.school.mapper.StockMovementMapper;
import com.hei.school.repository.*;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StockMovementService {

  private final StockMovementRepository stockMovementRepository;
  private final BookCopyRepository bookCopyRepository;
  private final ArrivalRepository arrivalRepository;
  private final SaleItemRepository saleItemRepository;
  private final BookRepository bookRepository;
  private final StockMovementMapper stockMovementMapper;

  @Transactional
  public StockMovementResponse recordArrivalMovement(UUID bookCopyId, UUID arrivalId) {
    BookCopy bookCopy =
        bookCopyRepository
            .findById(bookCopyId)
            .orElseThrow(() -> new NotFoundException("BookCopy not found: id=" + bookCopyId));
    Arrival arrival =
        arrivalRepository
            .findById(arrivalId)
            .orElseThrow(() -> new NotFoundException("Arrival not found: id=" + arrivalId));

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

  public List<StockMovementResponse> getAll(
      UUID bookCopyId, MovementType movementType, MovementReason reason, Instant from, Instant to) {
    if (bookCopyId != null) {
      return stockMovementRepository.findByBookCopyId(bookCopyId).stream()
          .map(stockMovementMapper::toResponse)
          .toList();
    }
    if (movementType != null) {
      return stockMovementRepository.findByMovementType(movementType).stream()
          .map(stockMovementMapper::toResponse)
          .toList();
    }
    if (reason != null) {
      return stockMovementRepository.findByReason(reason).stream()
          .map(stockMovementMapper::toResponse)
          .toList();
    }
    if (from != null && to != null) {
      return stockMovementRepository.findByMovementDateBetween(from, to).stream()
          .map(stockMovementMapper::toResponse)
          .toList();
    }
    return stockMovementRepository.findAll().stream().map(stockMovementMapper::toResponse).toList();
  }

  public StockMovementResponse getById(UUID id) {
    StockMovement stockMovement =
        stockMovementRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("StockMovement with id " + id + " not found"));
    return stockMovementMapper.toResponse(stockMovement);
  }

  // Update forbidden — a stock movement is immutable
  public StockMovementResponse update(UUID id) {
    throw new ForbiddenException(
        "StockMovement with id " + id + " cannot be updated — stock movements are immutable");
  }

  // Delete forbidden — a stock movement is immutable
  public void delete(UUID id) {
    throw new ForbiddenException(
        "StockMovement with id " + id + " cannot be deleted — stock movements are immutable");
  }

  // Validate movementType / reason consistency
  private void validateMovementTypeAndReason(MovementType type, MovementReason reason) {
    boolean valid =
        switch (reason) {
          case ARRIVAL, RETURN -> type == MovementType.IN;
          case SALE, DAMAGED, LOST -> type == MovementType.OUT;
        };
    if (!valid) {
      throw new BadRequestException(
          "MovementType " + type + " is not compatible with reason " + reason);
    }
  }

  public BookStockResponse getStockByBookCopyId(UUID bookCopyId) {
    BookCopy bookCopy =
        bookCopyRepository
            .findById(bookCopyId)
            .orElseThrow(
                () -> new NotFoundException("BookCopy with id " + bookCopyId + " not found"));

    Integer totalIn = stockMovementRepository.sumInQuantityByBookCopyId(bookCopyId);
    Integer totalOut = stockMovementRepository.sumOutQuantityByBookCopyId(bookCopyId);
    Integer currentStock = totalIn - totalOut;

    return new BookStockResponse(
        bookCopy.getId(),
        bookCopy.getBook().getTitle(),
        bookCopy.getIsbn(),
        totalIn,
        totalOut,
        currentStock);
  }

  public BookTotalStockResponse getTotalStockByBookId(UUID bookId) {
    Book book =
        bookRepository
            .findById(bookId)
            .orElseThrow(() -> new NotFoundException("Book with id " + bookId + " not found"));

    Integer totalStock = stockMovementRepository.getTotalStockByBookId(bookId);

    return new BookTotalStockResponse(bookId, book.getTitle(), totalStock);
  }
}
