package com.example.med_office.service;

import com.example.med_office.dto.NguoiDungResponse;
import com.example.med_office.dto.NguoiDungRoleUpdateRequest;

import java.util.List;

public interface NguoiDungService {

    List<NguoiDungResponse> getUsers();

    NguoiDungResponse updateUserRole(String id, NguoiDungRoleUpdateRequest request);
}
