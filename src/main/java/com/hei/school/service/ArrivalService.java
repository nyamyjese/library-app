package com.hei.school.service;

import com.hei.school.dto.ArrivalDTO;
import com.hei.school.entity.Arrival;
import com.hei.school.entity.BookCopy;
import com.hei.school.entity.CopyStatus;
import com.hei.school.entity.StockMovement;
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
public class ArrivalService {

  private final ArrivalRepository arrivalRepository;
  private final BookCopyRepository bookCopyRepository;
  private final StockMovementRepository stockMovementRepository;

  @Transactional
  public ArrivalDTO createArrival(ArrivalDTO dto) {
    Arrival arrival = toEntity(dto);
    Arrival saved = arrivalRepository.save(arrival);

    BookCopy copy = bookCopyRepository.findById(saved.getCopyId())
            .orElseThrow(() -> new RuntimeException("BookCopy introuvable : id=" + saved.getCopyId()));
    copy.setStatus(CopyStatus.AVAILABLE);
    bookCopyRepository.save(copy);

    StockMovement movement = StockMovement.builder()
            .copyId(saved.getCopyId())
            .quantity(saved.getQuantity())
            .movementDate(Instant.now())
            .arrivalId(saved.getId())
            .build();
    stockMovementRepository.save(movement);

    return toDTO(saved);
  }

  public ArrivalDTO getById(UUID id) {
    return toDTO(arrivalRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Arrival introuvable : id=" + id)));
  }

  public List<ArrivalDTO> getAll() {
    return arrivalRepository.findAll().stream().map(this::toDTO).toList();
  }

  public List<ArrivalDTO> getByCopyId(UUID copyId) {
    return arrivalRepository.findAllByCopyId(copyId).stream().map(this::toDTO).toList();
  }

  private Arrival toEntity(ArrivalDTO dto) {
    return Arrival.builder()
            .copyId(dto.getCopyId())
            .quantity(dto.getQuantity())
            .unitCost(dto.getUnitCost())
            .arrivalDate(dto.getArrivalDate() != null ? dto.getArrivalDate() : Instant.now())
            .build();
  }

  private ArrivalDTO toDTO(Arrival a) {
    return ArrivalDTO.builder()
            .id(a.getId())
            .copyId(a.getCopyId())
            .quantity(a.getQuantity())
            .unitCost(a.getUnitCost())
            .arrivalDate(a.getArrivalDate())
            .build();
  }
}