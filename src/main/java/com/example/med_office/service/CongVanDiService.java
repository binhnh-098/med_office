package com.example.med_office.service;

import com.example.med_office.dto.CongVanDiResponse;
import com.example.med_office.dto.PagedResponse;

import java.time.LocalDate;

public interface CongVanDiService {

    PagedResponse<CongVanDiResponse> findAll(
            int page,
            int size,
            String keyword,
            String trangThai,
            Integer nguoiKyId,
            LocalDate ngayBanHanhFrom,
            LocalDate ngayBanHanhTo
    );
}
