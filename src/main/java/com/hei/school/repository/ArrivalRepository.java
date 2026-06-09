package com.hei.school.repository;

import com.hei.school.entity.Arrival;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArrivalRepository extends JpaRepository<Arrival, Integer> {

  List<Arrival> findAllByBook_BookId(Integer bookId);

  List<Arrival> findAllByLibrary_LibraryId(Integer libraryId);

  List<Arrival> findAllByArrivalDate(LocalDate arrivalDate);

  List<Arrival> findAllByArrivalDateBetween(LocalDate from, LocalDate to);
}
