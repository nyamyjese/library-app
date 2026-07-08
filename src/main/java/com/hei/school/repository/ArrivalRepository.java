package com.hei.school.repository;

import com.hei.school.entity.Arrival;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArrivalRepository extends JpaRepository<Arrival, UUID> {

  List<Arrival> findByBookCopyId(UUID bookCopyId);

  List<Arrival> findByArrivalDateBetween(Instant from, Instant to);
}
