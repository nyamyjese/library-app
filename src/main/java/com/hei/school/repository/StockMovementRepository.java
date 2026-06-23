package com.hei.school.repository;

import com.hei.school.entity.StockMovement;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, UUID> {

    List<StockMovement> findAllByCopyId(UUID copyId);

    List<StockMovement> findAllByArrivalId(UUID arrivalId);
}