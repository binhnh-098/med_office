package com.example.med_office.service;

import com.example.med_office.dto.ContractDTOs.*;
import org.springframework.data.domain.Page;

public interface ContractService {
    Page<ContractResponse> getContracts(String keyword, String status, String employeeId, String currentUsername, int page, int size);
    ContractResponse getContractDetail(String id, String currentUsername);
    ContractResponse createContract(ContractUpsertRequest request, String currentUsername);
    ContractResponse updateContract(String id, ContractUpsertRequest request, String currentUsername);
    void deleteContract(String id, String currentUsername);
}
