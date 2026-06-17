package com.hei.school.service;

import com.hei.school.dto.ArrivalDTO;
import com.hei.school.entity.Arrival;
import com.hei.school.entity.BookCopy;
import com.hei.school.entity.CopyStatus;
import com.hei.school.repository.ArrivalRepository;
import com.hei.school.repository.BookCopyRepository;
import java.time.Instant;
import java.util.ArrayList;
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

  @Transactional
  public ArrivalDTO createArrival(ArrivalDTO dto) {
    Arrival arrival = toEntity(dto);
    Arrival saved = arrivalRepository.save(arrival);
    generateBookCopies(saved);
    return toDTO(saved);
  }

  private void generateBookCopies(Arrival arrival) {
    List<BookCopy> copies = new ArrayList<>();
    for (int i = 0; i < arrival.getQuantity(); i++) {
      BookCopy copy = BookCopy.builder()
              .bookId(arrival.getBookId())
              .libraryId(arrival.getLibraryId())
              .format(arrival.getFormat())
              .copyPrice(arrival.getUnitCost())
              .status(CopyStatus.AVAILABLE)
              .acquisitionDate(arrival.getArrivalDate() != null ? arrival.getArrivalDate() : Instant.now())
              .build();
      copies.add(copy);
    }
    bookCopyRepository.saveAll(copies);
  }

  public ArrivalDTO updateArrival(UUID id, ArrivalDTO dto) {
    Arrival existing = arrivalRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Arrival introuvable : id=" + id));
    existing.setBookId(dto.getBookId());
    existing.setLibraryId(dto.getLibraryId());
    existing.setFormat(dto.getFormat());
    existing.setQuantity(dto.getQuantity());
    existing.setArrivalDate(dto.getArrivalDate());
    existing.setUnitCost(dto.getUnitCost());
    return toDTO(arrivalRepository.save(existing));
  }

  public ArrivalDTO getById(UUID id) {
    return toDTO(arrivalRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Arrival introuvable : id=" + id)));
  }

  public List<ArrivalDTO> getAll() {
    return arrivalRepository.findAll().stream().map(this::toDTO).toList();
  }

  public List<ArrivalDTO> getByBook(UUID bookId) {
    return arrivalRepository.findAllByBookId(bookId).stream().map(this::toDTO).toList();
  }

  public List<ArrivalDTO> getByLibrary(UUID libraryId) {
    return arrivalRepository.findAllByLibraryId(libraryId).stream().map(this::toDTO).toList();
  }

  public List<ArrivalDTO> getByDate(Instant date) {
    return arrivalRepository.findAllByArrivalDate(date).stream().map(this::toDTO).toList();
  }

  public List<ArrivalDTO> getByDateRange(Instant from, Instant to) {
    return arrivalRepository.findAllByArrivalDateBetween(from, to).stream().map(this::toDTO).toList();
  }

  private Arrival toEntity(ArrivalDTO dto) {
    return Arrival.builder()
            .bookId(dto.getBookId())
            .libraryId(dto.getLibraryId())
            .format(dto.getFormat())
            .quantity(dto.getQuantity())
            .arrivalDate(dto.getArrivalDate() != null ? dto.getArrivalDate() : Instant.now())
            .unitCost(dto.getUnitCost())
            .build();
  }

  private ArrivalDTO toDTO(Arrival a) {
    return ArrivalDTO.builder()
            .arrivalId(a.getArrivalId())
            .bookId(a.getBookId())
            .libraryId(a.getLibraryId())
            .format(a.getFormat())
            .quantity(a.getQuantity())
            .arrivalDate(a.getArrivalDate())
            .unitCost(a.getUnitCost())
            .build();
  }
}