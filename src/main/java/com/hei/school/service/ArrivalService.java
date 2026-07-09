package com.hei.school.service;

import com.hei.school.dto.request.CreateArrivalRequest;
import com.hei.school.dto.request.UpdateArrivalRequest;
import com.hei.school.dto.response.ArrivalResponse;
import com.hei.school.entity.Arrival;
import com.hei.school.entity.BookCopy;
import com.hei.school.entity.StockMovement;
import com.hei.school.entity.enums.MovementReason;
import com.hei.school.entity.enums.MovementType;
import com.hei.school.exception.NotFoundException;
import com.hei.school.mapper.ArrivalMapper;
import com.hei.school.repository.ArrivalRepository;
import com.hei.school.repository.BookCopyRepository;
import com.hei.school.repository.StockMovementRepository;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArrivalService {

  private final ArrivalRepository arrivalRepository;
  private final BookCopyRepository bookCopyRepository;
  private final StockMovementRepository stockMovementRepository;
  private final ArrivalMapper arrivalMapper;

  @Transactional
  public ArrivalResponse create(CreateArrivalRequest request) {
    BookCopy bookCopy =
        bookCopyRepository
            .findById(request.bookCopyId())
            .orElseThrow(
                () ->
                    new NotFoundException(
                        "BookCopy with id " + request.bookCopyId() + " not found"));
    Arrival arrival = arrivalMapper.toEntity(request, bookCopy);
    Arrival saved = arrivalRepository.save(arrival);

    StockMovement movement = new StockMovement();
    movement.setMovementType(MovementType.IN);
    movement.setReason(MovementReason.ARRIVAL);
    movement.setQuantity(saved.getQuantity());
    movement.setBookCopy(bookCopy);
    movement.setArrival(saved);
    movement.setMovementDate(Instant.now());
    stockMovementRepository.save(movement);

    return arrivalMapper.toResponse(saved);
  }

  public List<ArrivalResponse> getAll(UUID bookCopyId, Instant from, Instant to) {
    if (bookCopyId != null) {
      return arrivalRepository.findByBookCopyId(bookCopyId).stream()
          .map(arrivalMapper::toResponse)
          .toList();
    }
    if (from != null && to != null) {
      return arrivalRepository.findByArrivalDateBetween(from, to).stream()
          .map(arrivalMapper::toResponse)
          .toList();
    }
    return arrivalRepository.findAll().stream().map(arrivalMapper::toResponse).toList();
  }

  public ArrivalResponse getById(UUID id) {
    Arrival arrival =
        arrivalRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Arrival with id " + id + " not found"));
    return arrivalMapper.toResponse(arrival);
  }

  public ArrivalResponse update(UUID id, UpdateArrivalRequest request) {
    Arrival arrival =
        arrivalRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Arrival with id " + id + " not found"));
    arrival.setQuantity(request.quantity());
    arrival.setUnitPrice(request.unitPrice());
    arrival.setArrivalDate(request.arrivalDate());
    Arrival saved = arrivalRepository.save(arrival);
    return arrivalMapper.toResponse(saved);
  }

  public void delete(UUID id) {
    arrivalRepository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("Arrival with id " + id + " not found"));
    arrivalRepository.deleteById(id);
  }
}
