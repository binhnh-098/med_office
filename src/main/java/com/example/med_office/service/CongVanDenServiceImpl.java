package com.example.med_office.service;

import com.example.med_office.dto.CongVanDenCreateRequest;
import com.example.med_office.dto.CongVanDenResponse;
import com.example.med_office.dto.PagedResponse;
import com.example.med_office.entity.CongVanDen;
import com.example.med_office.entity.NhaCungCap;
import com.example.med_office.repository.CongVanDenRepository;
import com.example.med_office.repository.NhaCungCapRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class CongVanDenServiceImpl implements CongVanDenService {

    private final CongVanDenRepository congVanDenRepository;
    private final NhaCungCapRepository nhaCungCapRepository;

    public CongVanDenServiceImpl(CongVanDenRepository congVanDenRepository, NhaCungCapRepository nhaCungCapRepository) {
        this.congVanDenRepository = congVanDenRepository;
        this.nhaCungCapRepository = nhaCungCapRepository;
    }

    @Override
    public CongVanDenResponse create(CongVanDenCreateRequest request) {
        if (congVanDenRepository.existsBySoCongVanIgnoreCase(request.getSoCongVan().trim())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "So cong van da ton tai");
        }

        NhaCungCap nhaCungCap = nhaCungCapRepository.findById(request.getDonViGuiId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Don vi gui khong ton tai"));

        CongVanDen entity = new CongVanDen();
        entity.setSoCongVan(request.getSoCongVan().trim());
        entity.setSoDen(request.getSoDen());
        entity.setTieuDe(request.getTieuDe());
        entity.setNoiDungTomTat(request.getNoiDungTomTat());
        entity.setDonViGuiId(request.getDonViGuiId());
        entity.setDonViGui(nhaCungCap.getTenNhaCungCap());
        entity.setNguoiKy(request.getNguoiKy());
        entity.setNgayVanBan(request.getNgayVanBan());
        entity.setNgayNhan(request.getNgayNhan());
        entity.setMucDoKhan(request.getMucDoKhan());
        entity.setMucDoMat(request.getMucDoMat());
        entity.setPhongBanXuLyId(request.getPhongBanXuLyId());
        entity.setNguoiXuLyId(request.getNguoiXuLyId());
        entity.setNguonNhan(request.getNguonNhan());
        entity.setHanXuLy(request.getHanXuLy());
        entity.setDoKhanXuLy(request.getDoKhanXuLy());
        entity.setLoaiVanBanId(request.getLoaiVanBanId());
        entity.setLinhVucId(request.getLinhVucId());
        entity.setHoSoId(request.getHoSoId());
        entity.setSoTrang(request.getSoTrang());
        entity.setSoBan(request.getSoBan());
        entity.setTrichYeu(request.getTrichYeu());
        entity.setGhiChu(request.getGhiChu());
        entity.setYKienChiDao(request.getYKienChiDao());
        entity.setTepDinhKemChinh(request.getTepDinhKemChinh());
        entity.setDaDoc(request.getDaDoc());
        entity.setDaXuLy(request.getDaXuLy());
        entity.setIsDeleted(request.getIsDeleted());
        entity.setNguoiTaoId(request.getNguoiTaoId());
        entity.setNguoiCapNhatId(request.getNguoiCapNhatId());
        entity.setTrangThai(request.getTrangThai());

        CongVanDen saved = congVanDenRepository.save(entity);
        return toResponse(saved);
    }

    @Override
    public PagedResponse<CongVanDenResponse> findAll(
            int page,
            int size,
            String keyword,
            String trangThai,
            String donViGuiId,
            Boolean daXuLy,
            Boolean daDoc,
            LocalDate ngayNhanFrom,
            LocalDate ngayNhanTo,
            LocalDate ngayVanBanFrom,
            LocalDate ngayVanBanTo
    ) {
        validateDateRange("ngay nhan", ngayNhanFrom, ngayNhanTo);
        validateDateRange("ngay van ban", ngayVanBanFrom, ngayVanBanTo);

        Page<CongVanDen> result = congVanDenRepository.findAll(
                buildSpecification(
                        keyword,
                        trangThai,
                        donViGuiId,
                        daXuLy,
                        daDoc,
                        ngayNhanFrom,
                        ngayNhanTo,
                        ngayVanBanFrom,
                        ngayVanBanTo
                ),
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "ngayTao", "id"))
        );

        List<CongVanDenResponse> items = result.getContent()
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

    private CongVanDenResponse toResponse(CongVanDen entity) {
        return new CongVanDenResponse(
                entity.getId(),
                entity.getSoCongVan(),
                entity.getSoDen(),
                entity.getTieuDe(),
                entity.getNoiDungTomTat(),
                entity.getDonViGuiId(),
                entity.getDonViGui(),
                entity.getNguoiKy(),
                entity.getNgayVanBan(),
                entity.getNgayNhan(),
                entity.getMucDoKhan(),
                entity.getMucDoMat(),
                entity.getPhongBanXuLyId(),
                entity.getNguoiXuLyId(),
                entity.getNguonNhan(),
                entity.getHanXuLy(),
                entity.getDoKhanXuLy(),
                entity.getLoaiVanBanId(),
                entity.getLinhVucId(),
                entity.getHoSoId(),
                entity.getSoTrang(),
                entity.getSoBan(),
                entity.getTrichYeu(),
                entity.getGhiChu(),
                entity.getYKienChiDao(),
                entity.getTepDinhKemChinh(),
                entity.getTrangThai(),
                entity.getNgayTao(),
                entity.getNgayCapNhat(),
                entity.getDaDoc(),
                entity.getDaXuLy(),
                entity.getIsDeleted(),
                entity.getNguoiTaoId(),
                entity.getNguoiCapNhatId()
        );
    }

    private Specification<CongVanDen> buildSpecification(
            String keyword,
            String trangThai,
            String donViGuiId,
            Boolean daXuLy,
            Boolean daDoc,
            LocalDate ngayNhanFrom,
            LocalDate ngayNhanTo,
            LocalDate ngayVanBanFrom,
            LocalDate ngayVanBanTo
    ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (keyword != null && !keyword.isBlank()) {
                String normalizedKeyword = "%" + keyword.trim().toLowerCase(Locale.ROOT) + "%";
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("soCongVan")), normalizedKeyword),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("soDen")), normalizedKeyword),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("tieuDe")), normalizedKeyword),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("trichYeu")), normalizedKeyword),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("donViGui")), normalizedKeyword),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("nguoiKy")), normalizedKeyword)
                ));
            }

            if (trangThai != null && !trangThai.isBlank()) {
                predicates.add(criteriaBuilder.equal(
                        criteriaBuilder.lower(root.get("trangThai")),
                        trangThai.trim().toLowerCase(Locale.ROOT)
                ));
            }

            if (donViGuiId != null) {
                predicates.add(criteriaBuilder.equal(root.get("donViGuiId"), donViGuiId));
            }

            if (daXuLy != null) {
                predicates.add(criteriaBuilder.equal(root.get("daXuLy"), daXuLy));
            }

            if (daDoc != null) {
                predicates.add(criteriaBuilder.equal(root.get("daDoc"), daDoc));
            }

            if (ngayNhanFrom != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("ngayNhan"), ngayNhanFrom));
            }

            if (ngayNhanTo != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("ngayNhan"), ngayNhanTo));
            }

            if (ngayVanBanFrom != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("ngayVanBan"), ngayVanBanFrom));
            }

            if (ngayVanBanTo != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("ngayVanBan"), ngayVanBanTo));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private void validateDateRange(String fieldName, LocalDate from, LocalDate to) {
        if (from != null && to != null && from.isAfter(to)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Khoang " + fieldName + " khong hop le");
        }
    }
}
