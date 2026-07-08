package com.hei.school.repository;

import com.hei.school.entity.StockMovement;
import com.hei.school.entity.enums.MovementReason;
import com.hei.school.entity.enums.MovementType;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, UUID> {

  List<StockMovement> findAllByBookCopy_Id(UUID bookCopyId);

  List<StockMovement> findAllByMovementType(MovementType movementType);

  List<StockMovement> findAllByReason(MovementReason reason);

  List<StockMovement> findAllByArrival_Id(UUID arrivalId);
}
