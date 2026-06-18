package com.example.med_office.repository;

import com.example.med_office.dto.WarehouseInventoryAggregateRow;
import com.example.med_office.entity.WarehouseOutboundItem;
import com.example.med_office.entity.WarehouseOutboundStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface WarehouseOutboundItemRepository extends JpaRepository<WarehouseOutboundItem, String> {

    @Query("""
            select new com.example.med_office.dto.WarehouseInventoryAggregateRow(
                i.itemId,
                i.itemCode,
                i.itemName,
                outbound.warehouseId,
                outbound.warehouseName,
                i.batchNumber,
                i.expiryDate,
                i.unit,
                sum(i.quantity),
                max(i.unitPrice)
            )
            from WarehouseOutboundItem i
            join i.warehouseOutbound outbound
            where outbound.status in :statuses
              and outbound.warehouseId in :warehouseIds
              and (:warehouseId is null or outbound.warehouseId = :warehouseId)
              and (:keyword is null
                or lower(i.itemCode) like lower(concat('%', :keyword, '%'))
                or lower(i.itemName) like lower(concat('%', :keyword, '%'))
                or lower(i.batchNumber) like lower(concat('%', :keyword, '%')))
            group by i.itemId, i.itemCode, i.itemName, outbound.warehouseId, outbound.warehouseName,
                     i.batchNumber, i.expiryDate, i.unit
            """)
    List<WarehouseInventoryAggregateRow> summarizeQuantities(
            @Param("statuses") Collection<WarehouseOutboundStatus> statuses,
            @Param("warehouseIds") Collection<String> warehouseIds,
            @Param("warehouseId") String warehouseId,
            @Param("keyword") String keyword
    );

    @Query("""
            select new com.example.med_office.dto.WarehouseInventoryAggregateRow(
                i.itemId,
                i.itemCode,
                i.itemName,
                outbound.destinationWarehouseId,
                outbound.destinationName,
                i.batchNumber,
                i.expiryDate,
                i.unit,
                sum(i.quantity),
                max(i.unitPrice)
            )
            from WarehouseOutboundItem i
            join i.warehouseOutbound outbound
            where outbound.status in :statuses
              and outbound.destinationWarehouseId in :warehouseIds
              and (:warehouseId is null or outbound.destinationWarehouseId = :warehouseId)
              and (:keyword is null
                or lower(i.itemCode) like lower(concat('%', :keyword, '%'))
                or lower(i.itemName) like lower(concat('%', :keyword, '%'))
                or lower(i.batchNumber) like lower(concat('%', :keyword, '%')))
            group by i.itemId, i.itemCode, i.itemName, outbound.destinationWarehouseId, outbound.destinationName,
                     i.batchNumber, i.expiryDate, i.unit
            """)
    List<WarehouseInventoryAggregateRow> summarizeTransferredInQuantities(
            @Param("statuses") Collection<WarehouseOutboundStatus> statuses,
            @Param("warehouseIds") Collection<String> warehouseIds,
            @Param("warehouseId") String warehouseId,
            @Param("keyword") String keyword
    );
}
