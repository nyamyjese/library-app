package com.hei.school.repository;

import com.hei.school.entity.Arrival;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArrivalRepository extends JpaRepository<Arrival, UUID> {

  List<Arrival> findAllByBookId(UUID bookId);

  List<Arrival> findAllByLibraryId(UUID libraryId);

  List<Arrival> findAllByArrivalDate(Instant arrivalDate);

  List<Arrival> findAllByArrivalDateBetween(Instant from, Instant to);
}