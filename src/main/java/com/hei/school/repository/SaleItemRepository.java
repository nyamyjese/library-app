package com.hei.school.repository;

import com.hei.school.entity.SaleItem;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleItemRepository extends JpaRepository<SaleItem, UUID> {

  List<SaleItem> findBySaleId(UUID saleId);

  void deleteBySaleId(UUID saleId);

  @Query(
      """
      SELECT SUM(si.unitPrice * si.quantity)
      FROM SaleItem si
      JOIN si.bookCopy bookCopy
      JOIN bookCopy.book book
      JOIN book.genres genre
      WHERE genre.id = :genreId
      """)
  BigDecimal sumRevenueByGenreId(@Param("genreId") UUID genreId);
}
