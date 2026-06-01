package com.example.med_office.service;

import com.example.med_office.dto.HoSoNhanVienRequest;
import com.example.med_office.dto.HoSoNhanVienResponse;
import com.example.med_office.dto.ImportResultResponse;
import com.example.med_office.dto.PagedResponse;
import org.springframework.web.multipart.MultipartFile;

public interface HoSoNhanVienService {

    PagedResponse<HoSoNhanVienResponse> findAll(
            int page,
            int size,
            String keyword,
            Boolean active,
            Integer gender,
            Boolean onlineBooking,
            String nguoiDungId,
            Boolean hasNguoiDungId
    );

    HoSoNhanVienResponse findById(String id);

    HoSoNhanVienResponse create(HoSoNhanVienRequest request);

    HoSoNhanVienResponse update(String id, HoSoNhanVienRequest request);

    void delete(String id);

    String exportCsv();

    ImportResultResponse importExcel(MultipartFile file);
}
