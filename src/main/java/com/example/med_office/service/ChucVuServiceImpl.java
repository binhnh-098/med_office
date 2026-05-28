package com.example.med_office.service;

import com.example.med_office.dto.ChucVuRequest;
import com.example.med_office.dto.ChucVuResponse;
import com.example.med_office.entity.ChucVu;
import com.example.med_office.repository.ChucVuRepository;
import com.example.med_office.repository.NguoiDungRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;

@Service
public class ChucVuServiceImpl implements ChucVuService {

    private final ChucVuRepository chucVuRepository;
    private final NguoiDungRepository nguoiDungRepository;

    public ChucVuServiceImpl(
            ChucVuRepository chucVuRepository,
            NguoiDungRepository nguoiDungRepository
    ) {
        this.chucVuRepository = chucVuRepository;
        this.nguoiDungRepository = nguoiDungRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChucVuResponse> findAll(String userId) {
        List<ChucVu> chucVuList = userId == null
                ? chucVuRepository.findAll()
                : chucVuRepository.findByUserIdOrderByTenChucVuAscIdAsc(userId);

        return chucVuList.stream()
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
        validateRequest(maChucVu, tenChucVu, request.userId());
        if (chucVuRepository.existsByUserIdAndMaChucVuIgnoreCase(request.userId(), maChucVu)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ma chuc vu cua nguoi dung da ton tai");
        }

        ChucVu chucVu = new ChucVu();
        applyRequest(chucVu, maChucVu, tenChucVu, request.userId());
        return toResponse(chucVuRepository.save(chucVu));
    }

    @Override
    @Transactional
    public ChucVuResponse update(String id, ChucVuRequest request) {
        ChucVu chucVu = findChucVu(id);
        String maChucVu = trim(request.maChucVu());
        String tenChucVu = trim(request.tenChucVu());
        validateRequest(maChucVu, tenChucVu, request.userId());
        if (chucVuRepository.existsByUserIdAndMaChucVuIgnoreCaseAndIdNot(request.userId(), maChucVu, id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ma chuc vu cua nguoi dung da ton tai");
        }

        applyRequest(chucVu, maChucVu, tenChucVu, request.userId());
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

    private void applyRequest(ChucVu chucVu, String maChucVu, String tenChucVu, String userId) {
        chucVu.setMaChucVu(maChucVu);
        chucVu.setTenChucVu(tenChucVu);
        chucVu.setUserId(userId);
    }

    private void validateRequest(String maChucVu, String tenChucVu, String userId) {
        if (maChucVu == null || maChucVu.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ma chuc vu khong duoc de trong");
        }
        if (tenChucVu == null || tenChucVu.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ten chuc vu khong duoc de trong");
        }
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User id khong duoc de trong");
        }
        if (!nguoiDungRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nguoi dung khong ton tai");
        }
    }

    private ChucVuResponse toResponse(ChucVu chucVu) {
        return new ChucVuResponse(
                chucVu.getId(),
                chucVu.getMaChucVu(),
                chucVu.getTenChucVu(),
                chucVu.getUserId()
        );
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }
}
