package com.example.med_office.service;

import com.example.med_office.dto.ChuyenKhoaRequest;
import com.example.med_office.dto.ChuyenKhoaResponse;

import java.util.List;

public interface ChuyenKhoaService {

    List<ChuyenKhoaResponse> findAll();

    List<ChuyenKhoaResponse> findByUserId(String userId);

    ChuyenKhoaResponse findById(String idChuyenKhoa);

    ChuyenKhoaResponse create(ChuyenKhoaRequest request);

    ChuyenKhoaResponse update(String idChuyenKhoa, ChuyenKhoaRequest request);

    void delete(String idChuyenKhoa);
}
