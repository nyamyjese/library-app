package com.hei.school.service;

import com.hei.school.dto.ArrivalDTO;
import com.hei.school.entity.Arrival;
import com.hei.school.entity.Book;
import com.hei.school.entity.BookCopy;
import com.hei.school.entity.CopyStatus;
import com.hei.school.entity.Library;
import com.hei.school.repository.ArrivalRepository;
import com.hei.school.repository.BookCopyRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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
          .book(arrival.getBook())
          .library(arrival.getLibrary())
          .format(arrival.getFormat())
          .copyPrice(arrival.getUnitCost())
          .status(CopyStatus.AVAILABLE)
          .acquisitionDate(arrival.getArrivalDate() != null
              ? arrival.getArrivalDate()
              : LocalDate.now())
          .build();
      copies.add(copy);
    }
    bookCopyRepository.saveAll(copies);
  }

  public ArrivalDTO updateArrival(Integer id, ArrivalDTO dto) {
    Arrival existing = arrivalRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Arrival introuvable : id=" + id));
    existing.setBook(Book.builder().bookId(dto.getBookId()).build());
    existing.setLibrary(Library.builder().libraryId(dto.getLibraryId()).build());
    existing.setFormat(dto.getFormat());
    existing.setQuantity(dto.getQuantity());
    existing.setArrivalDate(dto.getArrivalDate());
    existing.setUnitCost(dto.getUnitCost());
    return toDTO(arrivalRepository.save(existing));
  }

  public ArrivalDTO getById(Integer id) {
    return toDTO(arrivalRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Arrival introuvable : id=" + id)));
  }

  public List<ArrivalDTO> getAll() {
    return arrivalRepository.findAll().stream().map(this::toDTO).toList();
  }

  public List<ArrivalDTO> getByBook(Integer bookId) {
    return arrivalRepository.findAllByBook_BookId(bookId).stream().map(this::toDTO).toList();
  }

  public List<ArrivalDTO> getByLibrary(Integer libraryId) {
    return arrivalRepository.findAllByLibrary_LibraryId(libraryId).stream().map(this::toDTO).toList();
  }

  public List<ArrivalDTO> getByDate(LocalDate date) {
    return arrivalRepository.findAllByArrivalDate(date).stream().map(this::toDTO).toList();
  }

  public List<ArrivalDTO> getByDateRange(LocalDate from, LocalDate to) {
    return arrivalRepository.findAllByArrivalDateBetween(from, to).stream().map(this::toDTO).toList();
  }

  // ---- Mappers ----

  private Arrival toEntity(ArrivalDTO dto) {
    return Arrival.builder()
        .book(Book.builder().bookId(dto.getBookId()).build())
        .library(Library.builder().libraryId(dto.getLibraryId()).build())
        .format(dto.getFormat())
        .quantity(dto.getQuantity())
        .arrivalDate(dto.getArrivalDate() != null ? dto.getArrivalDate() : LocalDate.now())
        .unitCost(dto.getUnitCost())
        .build();
  }

  private ArrivalDTO toDTO(Arrival a) {
    return ArrivalDTO.builder()
        .arrivalId(a.getArrivalId())
        .bookId(a.getBook() != null ? a.getBook().getBookId() : null)
        .libraryId(a.getLibrary() != null ? a.getLibrary().getLibraryId() : null)
        .format(a.getFormat())
        .quantity(a.getQuantity())
        .arrivalDate(a.getArrivalDate())
        .unitCost(a.getUnitCost())
        .build();
  }
}
