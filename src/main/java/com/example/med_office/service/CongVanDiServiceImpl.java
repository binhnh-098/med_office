package com.example.med_office.service;

import com.example.med_office.dto.CongVanDiResponse;
import com.example.med_office.dto.PagedResponse;
import com.example.med_office.entity.CongVanDi;
import com.example.med_office.repository.CongVanDiRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class CongVanDiServiceImpl implements CongVanDiService {

    private final CongVanDiRepository congVanDiRepository;

    public CongVanDiServiceImpl(CongVanDiRepository congVanDiRepository) {
        this.congVanDiRepository = congVanDiRepository;
    }

    @Override
    public PagedResponse<CongVanDiResponse> findAll(
            int page,
            int size,
            String keyword,
            String trangThai,
            Integer nguoiKyId,
            LocalDate ngayBanHanhFrom,
            LocalDate ngayBanHanhTo
    ) {
        validateDateRange(ngayBanHanhFrom, ngayBanHanhTo);

        Page<CongVanDi> result = congVanDiRepository.findAll(
                buildSpecification(keyword, trangThai, nguoiKyId, ngayBanHanhFrom, ngayBanHanhTo),
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "ngayTao", "id"))
        );

        List<CongVanDiResponse> items = result.getContent()
                .stream()
                .map(this::toResponse)
                .toList();

        return new PagedResponse<>(
                items,
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.hasNext(),
                result.hasPrevious()
        );
    }

    private Specification<CongVanDi> buildSpecification(
            String keyword,
            String trangThai,
            Integer nguoiKyId,
            LocalDate ngayBanHanhFrom,
            LocalDate ngayBanHanhTo
    ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (keyword != null && !keyword.isBlank()) {
                String normalizedKeyword = "%" + keyword.trim().toLowerCase(Locale.ROOT) + "%";
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("soCongVan")), normalizedKeyword),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("tieuDe")), normalizedKeyword),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("noiDungTomTat")), normalizedKeyword),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("donViNhan")), normalizedKeyword)
                ));
            }

            if (trangThai != null && !trangThai.isBlank()) {
                predicates.add(criteriaBuilder.equal(
                        criteriaBuilder.lower(root.get("trangThai")),
                        trangThai.trim().toLowerCase(Locale.ROOT)
                ));
            }

            if (nguoiKyId != null) {
                predicates.add(criteriaBuilder.equal(root.get("nguoiKyId"), nguoiKyId));
            }

            if (ngayBanHanhFrom != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("ngayBanHanh"), ngayBanHanhFrom));
            }

            if (ngayBanHanhTo != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("ngayBanHanh"), ngayBanHanhTo));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private CongVanDiResponse toResponse(CongVanDi entity) {
        return new CongVanDiResponse(
                entity.getId(),
                entity.getSoCongVan(),
                entity.getTieuDe(),
                entity.getNoiDungTomTat(),
                entity.getDonViNhan(),
                entity.getNgayBanHanh(),
                entity.getNguoiKyId(),
                entity.getTrangThai(),
                entity.getNgayTao(),
                entity.getNgayCapNhat()
        );
    }

    private void validateDateRange(LocalDate from, LocalDate to) {
        if (from != null && to != null && from.isAfter(to)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Khoang ngay ban hanh khong hop le");
        }
    }
}
