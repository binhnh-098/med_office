package com.example.med_office.service;

import com.example.med_office.dto.PagedResponse;
import com.example.med_office.dto.SupplierResponse;
import com.example.med_office.entity.NhaCungCap;
import com.example.med_office.repository.NhaCungCapRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class SupplierServiceImpl implements SupplierService {

    private final NhaCungCapRepository nhaCungCapRepository;

    public SupplierServiceImpl(NhaCungCapRepository nhaCungCapRepository) {
        this.nhaCungCapRepository = nhaCungCapRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<SupplierResponse> findAll(int page, int size, String keyword, String status) {
        Page<NhaCungCap> result = nhaCungCapRepository.findAll(
                buildSpecification(keyword, status),
                PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "tenNhaCungCap", "id"))
        );

        List<SupplierResponse> items = result.getContent().stream()
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

    private Specification<NhaCungCap> buildSpecification(String keyword, String status) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (keyword != null && !keyword.isBlank()) {
                String normalizedKeyword = "%" + keyword.trim().toLowerCase(Locale.ROOT) + "%";
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("maNhaCungCap")), normalizedKeyword),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("tenNhaCungCap")), normalizedKeyword)
                ));
            }

            if (status != null && !status.isBlank()) {
                predicates.add(criteriaBuilder.equal(
                        criteriaBuilder.upper(root.get("trangThai")),
                        status.trim().toUpperCase(Locale.ROOT)
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private SupplierResponse toResponse(NhaCungCap supplier) {
        return new SupplierResponse(
                supplier.getId(),
                supplier.getMaNhaCungCap(),
                supplier.getTenNhaCungCap(),
                supplier.getTrangThai(),
                supplier.getNgayTao()
        );
    }
}
