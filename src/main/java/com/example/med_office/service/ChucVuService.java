package com.example.med_office.service;

import com.example.med_office.dto.ChucVuRequest;
import com.example.med_office.dto.ChucVuResponse;

import java.util.List;

public interface ChucVuService {

    List<ChucVuResponse> findAll(String userId);

    ChucVuResponse findById(String id);

    ChucVuResponse create(ChucVuRequest request);

    ChucVuResponse update(String id, ChucVuRequest request);

    void delete(String id);
}
