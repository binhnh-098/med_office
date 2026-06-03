package com.example.med_office.service;

import com.example.med_office.dto.WarehouseHierarchyItem;
import com.example.med_office.dto.WarehouseHierarchyUpdateRequest;
import com.example.med_office.dto.WarehousePageResponse;
import com.example.med_office.dto.WarehouseRequest;
import com.example.med_office.dto.WarehouseResponse;
import com.example.med_office.dto.WarehouseStatusUpdateRequest;
import com.example.med_office.dto.WarehouseSummaryResponse;

import java.util.List;

public interface WarehouseService {

    WarehousePageResponse findAll(String keyword, String status, int page, int size, String sort);

    WarehouseResponse findById(String id);

    WarehouseResponse create(WarehouseRequest request);

    WarehouseResponse update(String id, WarehouseRequest request);

    WarehouseResponse updateStatus(String id, WarehouseStatusUpdateRequest request);

    List<WarehouseHierarchyItem> getHierarchy();

    List<WarehouseHierarchyItem> updateHierarchy(WarehouseHierarchyUpdateRequest request);

    WarehouseSummaryResponse getSummary();
}
