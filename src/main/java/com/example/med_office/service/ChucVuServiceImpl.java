package com.example.med_office.service;

import com.example.med_office.dto.ChucVuRequest;
import com.example.med_office.dto.ChucVuResponse;
import com.example.med_office.entity.ChucVu;
import com.example.med_office.repository.ChucVuRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;

@Service
public class ChucVuServiceImpl implements ChucVuService {

    private final ChucVuRepository chucVuRepository;

    public ChucVuServiceImpl(ChucVuRepository chucVuRepository) {
        this.chucVuRepository = chucVuRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChucVuResponse> findAll() {
        return chucVuRepository.findAll().stream()
                .sorted(Comparator
                        .comparing(ChucVu::getTenChucVu, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(ChucVu::getId))
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ChucVuResponse findById(String id) {
        return toResponse(findChucVu(id));
    }

    @Override
    @Transactional
    public ChucVuResponse create(ChucVuRequest request) {
        String maChucVu = trim(request.maChucVu());
        String tenChucVu = trim(request.tenChucVu());
        validateRequest(maChucVu, tenChucVu);
        if (chucVuRepository.existsByMaChucVuIgnoreCase(maChucVu)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ma chuc vu da ton tai");
        }

        ChucVu chucVu = new ChucVu();
        applyRequest(chucVu, maChucVu, tenChucVu);
        return toResponse(chucVuRepository.save(chucVu));
    }

    @Override
    @Transactional
    public ChucVuResponse update(String id, ChucVuRequest request) {
        ChucVu chucVu = findChucVu(id);
        String maChucVu = trim(request.maChucVu());
        String tenChucVu = trim(request.tenChucVu());
        validateRequest(maChucVu, tenChucVu);
        if (chucVuRepository.existsByMaChucVuIgnoreCaseAndIdNot(maChucVu, id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ma chuc vu da ton tai");
        }

        applyRequest(chucVu, maChucVu, tenChucVu);
        return toResponse(chucVuRepository.save(chucVu));
    }

    @Override
    @Transactional
    public void delete(String id) {
        chucVuRepository.delete(findChucVu(id));
    }

    private ChucVu findChucVu(String id) {
        return chucVuRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Khong tim thay chuc vu"));
    }

    private void applyRequest(ChucVu chucVu, String maChucVu, String tenChucVu) {
        chucVu.setMaChucVu(maChucVu);
        chucVu.setTenChucVu(tenChucVu);
    }

    private void validateRequest(String maChucVu, String tenChucVu) {
        if (maChucVu == null || maChucVu.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ma chuc vu khong duoc de trong");
        }
        if (tenChucVu == null || tenChucVu.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ten chuc vu khong duoc de trong");
        }
    }

    private ChucVuResponse toResponse(ChucVu chucVu) {
        return new ChucVuResponse(
                chucVu.getId(),
                chucVu.getMaChucVu(),
                chucVu.getTenChucVu()
        );
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }
}
