package com.hei.school.repository;

import com.hei.school.entity.Sale;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleRepository extends JpaRepository<Sale, UUID> {

  List<Sale> findByCustomerId(UUID customerId);

  List<Sale> findByLibraryId(UUID libraryId);

  List<Sale> findBySaleDateBetween(Instant from, Instant to);
}
