package com.example.med_office.service;

import com.example.med_office.dto.HoSoNhanVienRequest;
import com.example.med_office.dto.HoSoNhanVienResponse;
import com.example.med_office.dto.PagedResponse;

public interface HoSoNhanVienService {

    PagedResponse<HoSoNhanVienResponse> findAll(
            int page,
            int size,
            String keyword,
            Boolean active,
            Integer gender,
            Boolean onlineBooking,
            Long nguoiDungId
    );

    HoSoNhanVienResponse findById(Long id);

    HoSoNhanVienResponse create(HoSoNhanVienRequest request);

    HoSoNhanVienResponse update(Long id, HoSoNhanVienRequest request);

    void delete(Long id);

    String exportCsv();
}
