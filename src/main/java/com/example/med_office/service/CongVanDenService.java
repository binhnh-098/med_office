package com.example.med_office.service;

import com.example.med_office.dto.CongVanDenCreateRequest;
import com.example.med_office.dto.CongVanDenResponse;
import com.example.med_office.dto.PagedResponse;

import java.time.LocalDate;

public interface CongVanDenService {

    CongVanDenResponse create(CongVanDenCreateRequest request);

    PagedResponse<CongVanDenResponse> findAll(
            int page,
            int size,
            String keyword,
            String trangThai,
            String donViGuiId,
            Boolean daXuLy,
            Boolean daDoc,
            LocalDate ngayNhanFrom,
            LocalDate ngayNhanTo,
            LocalDate ngayVanBanFrom,
            LocalDate ngayVanBanTo
    );
}
