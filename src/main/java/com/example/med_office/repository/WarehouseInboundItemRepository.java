package com.example.med_office.repository;

import com.example.med_office.dto.WarehouseInventoryAggregateRow;
import com.example.med_office.entity.WarehouseInboundItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface WarehouseInboundItemRepository extends JpaRepository<WarehouseInboundItem, String> {

    @Query("""
            select distinct i
            from WarehouseInboundItem i
            where (:keyword is null
                or lower(i.itemCode) like lower(concat('%', :keyword, '%'))
                or lower(i.itemName) like lower(concat('%', :keyword, '%')))
            order by i.itemName asc, i.itemCode asc, i.id asc
            """)
    List<WarehouseInboundItem> findOptionItems(@Param("keyword") String keyword, Pageable pageable);

    @Query("""
            select new com.example.med_office.dto.WarehouseInventoryAggregateRow(
                i.itemId,
                i.itemCode,
                i.itemName,
                inbound.warehouseId,
                inbound.warehouseName,
                i.batchNumber,
                i.expiryDate,
                i.unit,
                sum(i.quantity)
            )
            from WarehouseInboundItem i
            join i.warehouseInbound inbound
            where inbound.status = com.example.med_office.entity.WarehouseInboundStatus.COMPLETED
              and inbound.warehouseId in :warehouseIds
              and (:warehouseId is null or inbound.warehouseId = :warehouseId)
              and (:keyword is null
                or lower(i.itemCode) like lower(concat('%', :keyword, '%'))
                or lower(i.itemName) like lower(concat('%', :keyword, '%')))
            group by i.itemId, i.itemCode, i.itemName, inbound.warehouseId, inbound.warehouseName,
                     i.batchNumber, i.expiryDate, i.unit
            """)
    List<WarehouseInventoryAggregateRow> summarizeCompletedQuantities(
            @Param("warehouseIds") Collection<String> warehouseIds,
            @Param("warehouseId") String warehouseId,
            @Param("keyword") String keyword
    );
}
