package com.example.med_office.service;

import com.example.med_office.dto.LoginResponse;
import com.example.med_office.dto.SignupRequest;

public interface AuthService {

    LoginResponse getLoginResponse(String username);

    LoginResponse signup(SignupRequest request);
}
