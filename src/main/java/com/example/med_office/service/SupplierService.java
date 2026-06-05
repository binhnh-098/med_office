package com.example.med_office.service;

import com.example.med_office.dto.PagedResponse;
import com.example.med_office.dto.SupplierResponse;

public interface SupplierService {

    PagedResponse<SupplierResponse> findAll(int page, int size, String keyword, String status);
}
