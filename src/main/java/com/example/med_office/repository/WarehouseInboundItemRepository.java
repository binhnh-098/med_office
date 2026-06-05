package com.example.med_office.repository;

import com.example.med_office.entity.WarehouseInboundItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
}
