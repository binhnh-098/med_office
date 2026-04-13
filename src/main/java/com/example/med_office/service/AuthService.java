package com.example.med_office.service;

import com.example.med_office.dto.LoginResponse;

public interface AuthService {

    LoginResponse getLoginResponse(String username);
}
