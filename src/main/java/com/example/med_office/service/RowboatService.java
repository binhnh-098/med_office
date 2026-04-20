package com.example.med_office.service;

import com.example.med_office.dto.RowboatChatRequest;
import com.example.med_office.dto.RowboatChatResponse;

public interface RowboatService {

    RowboatChatResponse chat(RowboatChatRequest request);
}
