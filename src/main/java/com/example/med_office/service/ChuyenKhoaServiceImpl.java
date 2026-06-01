package com.example.med_office.service;

import com.example.med_office.dto.ChuyenKhoaRequest;
import com.example.med_office.dto.ChuyenKhoaResponse;
import com.example.med_office.entity.ChuyenKhoa;
import com.example.med_office.repository.ChuyenKhoaRepository;
import com.example.med_office.repository.NguoiDungRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;

@Service
public class ChuyenKhoaServiceImpl implements ChuyenKhoaService {

    private final ChuyenKhoaRepository chuyenKhoaRepository;
    private final NguoiDungRepository nguoiDungRepository;

    public ChuyenKhoaServiceImpl(
            ChuyenKhoaRepository chuyenKhoaRepository,
            NguoiDungRepository nguoiDungRepository
    ) {
        this.chuyenKhoaRepository = chuyenKhoaRepository;
        this.nguoiDungRepository = nguoiDungRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChuyenKhoaResponse> findAll() {
        return toSortedResponses(chuyenKhoaRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChuyenKhoaResponse> findByUserId(String userId) {
        return toSortedResponses(chuyenKhoaRepository.findByUserIdOrderByTenChuyenKhoaAscIdChuyenKhoaAsc(userId));
    }

    private List<ChuyenKhoaResponse> toSortedResponses(List<ChuyenKhoa> chuyenKhoaList) {
        return chuyenKhoaList.stream()
                .sorted(Comparator
                        .comparing(ChuyenKhoa::getTenChuyenKhoa, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(ChuyenKhoa::getIdChuyenKhoa))
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ChuyenKhoaResponse findById(String idChuyenKhoa) {
        return toResponse(findChuyenKhoa(idChuyenKhoa));
    }

    @Override
    @Transactional
    public ChuyenKhoaResponse create(ChuyenKhoaRequest request) {
        String tenChuyenKhoa = trim(request.tenChuyenKhoa());
        validateRequest(tenChuyenKhoa, request.userId());
        if (chuyenKhoaRepository.existsByUserIdAndTenChuyenKhoaIgnoreCase(request.userId(), tenChuyenKhoa)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Chuyen khoa cua nguoi dung da ton tai");
        }

        ChuyenKhoa chuyenKhoa = new ChuyenKhoa();
        chuyenKhoa.setTenChuyenKhoa(tenChuyenKhoa);
        chuyenKhoa.setUserId(request.userId());
        return toResponse(chuyenKhoaRepository.save(chuyenKhoa));
    }

    @Override
    @Transactional
    public ChuyenKhoaResponse update(String idChuyenKhoa, ChuyenKhoaRequest request) {
        ChuyenKhoa chuyenKhoa = findChuyenKhoa(idChuyenKhoa);
        String tenChuyenKhoa = trim(request.tenChuyenKhoa());
        validateRequest(tenChuyenKhoa, request.userId());
        if (chuyenKhoaRepository.existsByUserIdAndTenChuyenKhoaIgnoreCaseAndIdChuyenKhoaNot(
                request.userId(),
                tenChuyenKhoa,
                idChuyenKhoa
        )) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Chuyen khoa cua nguoi dung da ton tai");
        }

        chuyenKhoa.setTenChuyenKhoa(tenChuyenKhoa);
        chuyenKhoa.setUserId(request.userId());
        return toResponse(chuyenKhoaRepository.save(chuyenKhoa));
    }

    @Override
    @Transactional
    public void delete(String idChuyenKhoa) {
        chuyenKhoaRepository.delete(findChuyenKhoa(idChuyenKhoa));
    }

    private ChuyenKhoa findChuyenKhoa(String idChuyenKhoa) {
        return chuyenKhoaRepository.findById(idChuyenKhoa)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Khong tim thay chuyen khoa"));
    }

    private void validateRequest(String tenChuyenKhoa, String userId) {
        if (tenChuyenKhoa == null || tenChuyenKhoa.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ten chuyen khoa khong duoc de trong");
        }
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User id khong duoc de trong");
        }
        if (!nguoiDungRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nguoi dung khong ton tai");
        }
    }

    private ChuyenKhoaResponse toResponse(ChuyenKhoa chuyenKhoa) {
        return new ChuyenKhoaResponse(
                chuyenKhoa.getIdChuyenKhoa(),
                chuyenKhoa.getTenChuyenKhoa(),
                chuyenKhoa.getUserId()
        );
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }
}
