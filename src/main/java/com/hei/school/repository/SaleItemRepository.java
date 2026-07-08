package com.hei.school.repository;

import com.hei.school.entity.SaleItem;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleItemRepository extends JpaRepository<SaleItem, UUID> {

  List<SaleItem> findBySaleId(UUID saleId);

  void deleteBySaleId(UUID saleId);
}
