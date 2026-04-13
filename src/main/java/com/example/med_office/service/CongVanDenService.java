package com.example.med_office.service;

import com.example.med_office.dto.CongVanDenCreateRequest;
import com.example.med_office.dto.CongVanDenResponse;

public interface CongVanDenService {

    CongVanDenResponse create(CongVanDenCreateRequest request);
}
