package com.hei.school.repository;

import com.hei.school.entity.StockMovement;
import com.hei.school.entity.enums.MovementReason;
import com.hei.school.entity.enums.MovementType;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, UUID> {

  List<StockMovement> findByBookCopyId(UUID bookCopyId);

  List<StockMovement> findByMovementType(MovementType movementType);

  List<StockMovement> findByReason(MovementReason reason);

  List<StockMovement> findByMovementDateBetween(Instant from, Instant to);

  @Query(
      "SELECT COALESCE(SUM(s.quantity), 0) FROM StockMovement s WHERE s.bookCopy.id = :bookCopyId"
          + " AND s.movementType = 'IN'")
  Integer sumInQuantityByBookCopyId(@Param("bookCopyId") UUID bookCopyId);

  @Query(
      "SELECT COALESCE(SUM(s.quantity), 0) FROM StockMovement s WHERE s.bookCopy.id = :bookCopyId"
          + " AND s.movementType = 'OUT'")
  Integer sumOutQuantityByBookCopyId(@Param("bookCopyId") UUID bookCopyId);

  @Query(
      """
  SELECT COALESCE(SUM(CASE WHEN sm.movementType = 'IN' THEN sm.quantity ELSE -sm.quantity END), 0)
  FROM StockMovement sm
  WHERE sm.bookCopy.book.id = :bookId
""")
  Integer getTotalStockByBookId(@Param("bookId") UUID bookId);

  @Query(
      """
SELECT sm.bookCopy.id
FROM StockMovement sm
GROUP BY sm.bookCopy.id
HAVING COALESCE(SUM(CASE WHEN sm.movementType = 'IN' THEN sm.quantity ELSE -sm.quantity END), 0) <= :threshold
""")
  List<UUID> findBookCopyIdsWithLowStock(@Param("threshold") Integer threshold);

  @Query(
      """
SELECT COALESCE(SUM(CASE WHEN sm.movementType = 'IN' THEN sm.quantity ELSE -sm.quantity END), 0)
FROM StockMovement sm
WHERE sm.bookCopy.id = :bookCopyId
""")
  Integer getTotalStockByBookCopyId(@Param("bookCopyId") UUID bookCopyId);
}
