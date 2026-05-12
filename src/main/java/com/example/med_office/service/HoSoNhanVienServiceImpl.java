package com.example.med_office.service;

import com.example.med_office.dto.HoSoNhanVienRequest;
import com.example.med_office.dto.HoSoNhanVienResponse;
import com.example.med_office.dto.PagedResponse;
import com.example.med_office.entity.HoSoNhanVien;
import com.example.med_office.repository.HoSoNhanVienRepository;
import com.example.med_office.repository.NguoiDungRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class HoSoNhanVienServiceImpl implements HoSoNhanVienService {

    private final HoSoNhanVienRepository hoSoNhanVienRepository;
    private final NguoiDungRepository nguoiDungRepository;

    public HoSoNhanVienServiceImpl(
            HoSoNhanVienRepository hoSoNhanVienRepository,
            NguoiDungRepository nguoiDungRepository
    ) {
        this.hoSoNhanVienRepository = hoSoNhanVienRepository;
        this.nguoiDungRepository = nguoiDungRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<HoSoNhanVienResponse> findAll(
            int page,
            int size,
            String keyword,
            Boolean active,
            Integer gender,
            Boolean onlineBooking,
            Long nguoiDungId
    ) {
        Page<HoSoNhanVien> result = hoSoNhanVienRepository.findAll(
                buildSpecification(keyword, active, gender, onlineBooking, nguoiDungId),
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt", "id"))
        );

        List<HoSoNhanVienResponse> items = result.getContent()
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

    @Override
    @Transactional(readOnly = true)
    public HoSoNhanVienResponse findById(Long id) {
        return toResponse(findHoSoNhanVien(id));
    }

    @Override
    @Transactional
    public HoSoNhanVienResponse create(HoSoNhanVienRequest request) {
        String code = trim(request.code());
        validateRequiredFields(code, trim(request.name()));
        validateLockedRange(request.lockedFrom(), request.lockedTo());
        validateNguoiDungLink(request.nguoiDungId(), null);
        if (hoSoNhanVienRepository.existsByCodeIgnoreCase(code)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ma ho so nhan vien da ton tai");
        }

        HoSoNhanVien hoSoNhanVien = new HoSoNhanVien();
        applyRequest(hoSoNhanVien, request);
        return toResponse(hoSoNhanVienRepository.save(hoSoNhanVien));
    }

    @Override
    @Transactional
    public HoSoNhanVienResponse update(Long id, HoSoNhanVienRequest request) {
        HoSoNhanVien hoSoNhanVien = findHoSoNhanVien(id);
        String code = trim(request.code());
        validateRequiredFields(code, trim(request.name()));
        validateLockedRange(request.lockedFrom(), request.lockedTo());
        validateNguoiDungLink(request.nguoiDungId(), id);
        if (hoSoNhanVienRepository.existsByCodeIgnoreCaseAndIdNot(code, id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ma ho so nhan vien da ton tai");
        }

        applyRequest(hoSoNhanVien, request);
        return toResponse(hoSoNhanVienRepository.save(hoSoNhanVien));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        HoSoNhanVien hoSoNhanVien = findHoSoNhanVien(id);
        hoSoNhanVienRepository.delete(hoSoNhanVien);
    }

    @Override
    @Transactional(readOnly = true)
    public String exportCsv() {
        List<HoSoNhanVien> hoSoNhanVienList = hoSoNhanVienRepository.findAll(Sort.by(Sort.Direction.ASC, "code", "id"));
        String header = String.join(",", List.of(
                "id",
                "nguoiDungId",
                "code",
                "name",
                "birthDate",
                "gender",
                "identityNumber",
                "socialInsurance",
                "email",
                "phone",
                "degree",
                "specialty",
                "specialtyName",
                "academicTitle",
                "academicTitleName",
                "certificate",
                "position",
                "positionName",
                "honorTitle",
                "signingPin",
                "signingAccount",
                "signingOtp",
                "invoicePassword",
                "avatarImage",
                "signatureImage",
                "lockedFrom",
                "lockedTo",
                "prescriptionAccount",
                "prescriptionPassword",
                "onlineBooking",
                "active",
                "note"
        ));

        String rows = hoSoNhanVienList.stream()
                .map(this::toCsvRow)
                .collect(Collectors.joining("\n"));

        return "\uFEFF" + header + (rows.isBlank() ? "" : "\n" + rows);
    }

    private HoSoNhanVien findHoSoNhanVien(Long id) {
        return hoSoNhanVienRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Khong tim thay ho so nhan vien"));
    }

    private Specification<HoSoNhanVien> buildSpecification(
            String keyword,
            Boolean active,
            Integer gender,
            Boolean onlineBooking,
            Long nguoiDungId
    ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (keyword != null && !keyword.isBlank()) {
                String normalizedKeyword = "%" + keyword.trim().toLowerCase(Locale.ROOT) + "%";
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("code")), normalizedKeyword),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), normalizedKeyword),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("identityNumber")), normalizedKeyword),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("socialInsurance")), normalizedKeyword),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), normalizedKeyword),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("phone")), normalizedKeyword),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("specialtyName")), normalizedKeyword),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("certificate")), normalizedKeyword),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("positionName")), normalizedKeyword)
                ));
            }

            if (active != null) {
                predicates.add(criteriaBuilder.equal(root.get("active"), active));
            }

            if (gender != null) {
                predicates.add(criteriaBuilder.equal(root.get("gender"), gender));
            }

            if (onlineBooking != null) {
                predicates.add(criteriaBuilder.equal(root.get("onlineBooking"), onlineBooking));
            }

            if (nguoiDungId != null) {
                predicates.add(criteriaBuilder.equal(root.get("nguoiDungId"), nguoiDungId));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private void applyRequest(HoSoNhanVien hoSoNhanVien, HoSoNhanVienRequest request) {
        hoSoNhanVien.setNguoiDungId(request.nguoiDungId());
        hoSoNhanVien.setCode(trim(request.code()));
        hoSoNhanVien.setName(trim(request.name()));
        hoSoNhanVien.setBirthDate(request.birthDate());
        hoSoNhanVien.setGender(request.gender());
        hoSoNhanVien.setIdentityNumber(trim(request.identityNumber()));
        hoSoNhanVien.setSocialInsurance(trim(request.socialInsurance()));
        hoSoNhanVien.setEmail(trim(request.email()));
        hoSoNhanVien.setPhone(trim(request.phone()));
        hoSoNhanVien.setDegree(trim(request.degree()));
        hoSoNhanVien.setSpecialty(trim(request.specialty()));
        hoSoNhanVien.setSpecialtyName(trim(request.specialtyName()));
        hoSoNhanVien.setAcademicTitle(trim(request.academicTitle()));
        hoSoNhanVien.setAcademicTitleName(trim(request.academicTitleName()));
        hoSoNhanVien.setCertificate(trim(request.certificate()));
        hoSoNhanVien.setPosition(trim(request.position()));
        hoSoNhanVien.setPositionName(trim(request.positionName()));
        hoSoNhanVien.setHonorTitle(trim(request.honorTitle()));
        hoSoNhanVien.setSigningPin(trim(request.signingPin()));
        hoSoNhanVien.setSigningAccount(trim(request.signingAccount()));
        hoSoNhanVien.setSigningOtp(trim(request.signingOtp()));
        hoSoNhanVien.setInvoicePassword(trim(request.invoicePassword()));
        hoSoNhanVien.setAvatarImage(trim(request.avatarImage()));
        hoSoNhanVien.setSignatureImage(trim(request.signatureImage()));
        hoSoNhanVien.setLockedFrom(request.lockedFrom());
        hoSoNhanVien.setLockedTo(request.lockedTo());
        hoSoNhanVien.setPrescriptionAccount(trim(request.prescriptionAccount()));
        hoSoNhanVien.setPrescriptionPassword(trim(request.prescriptionPassword()));
        hoSoNhanVien.setOnlineBooking(Objects.requireNonNullElse(request.onlineBooking(), false));
        hoSoNhanVien.setActive(Objects.requireNonNullElse(request.active(), true));
        hoSoNhanVien.setNote(trim(request.note()));
    }

    private HoSoNhanVienResponse toResponse(HoSoNhanVien hoSoNhanVien) {
        return new HoSoNhanVienResponse(
                hoSoNhanVien.getId(),
                hoSoNhanVien.getNguoiDungId(),
                hoSoNhanVien.getCode(),
                hoSoNhanVien.getName(),
                hoSoNhanVien.getBirthDate(),
                hoSoNhanVien.getGender(),
                hoSoNhanVien.getIdentityNumber(),
                hoSoNhanVien.getSocialInsurance(),
                hoSoNhanVien.getEmail(),
                hoSoNhanVien.getPhone(),
                hoSoNhanVien.getDegree(),
                hoSoNhanVien.getSpecialty(),
                hoSoNhanVien.getSpecialtyName(),
                hoSoNhanVien.getAcademicTitle(),
                hoSoNhanVien.getAcademicTitleName(),
                hoSoNhanVien.getCertificate(),
                hoSoNhanVien.getPosition(),
                hoSoNhanVien.getPositionName(),
                hoSoNhanVien.getHonorTitle(),
                hoSoNhanVien.getSigningPin(),
                hoSoNhanVien.getSigningAccount(),
                hoSoNhanVien.getSigningOtp(),
                hoSoNhanVien.getInvoicePassword(),
                hoSoNhanVien.getAvatarImage(),
                hoSoNhanVien.getSignatureImage(),
                hoSoNhanVien.getLockedFrom(),
                hoSoNhanVien.getLockedTo(),
                hoSoNhanVien.getPrescriptionAccount(),
                hoSoNhanVien.getPrescriptionPassword(),
                hoSoNhanVien.getOnlineBooking(),
                hoSoNhanVien.getActive(),
                hoSoNhanVien.getNote()
        );
    }

    private String toCsvRow(HoSoNhanVien hoSoNhanVien) {
        return Stream.of(
                        hoSoNhanVien.getId(),
                        hoSoNhanVien.getNguoiDungId(),
                        hoSoNhanVien.getCode(),
                        hoSoNhanVien.getName(),
                        hoSoNhanVien.getBirthDate(),
                        hoSoNhanVien.getGender(),
                        hoSoNhanVien.getIdentityNumber(),
                        hoSoNhanVien.getSocialInsurance(),
                        hoSoNhanVien.getEmail(),
                        hoSoNhanVien.getPhone(),
                        hoSoNhanVien.getDegree(),
                        hoSoNhanVien.getSpecialty(),
                        hoSoNhanVien.getSpecialtyName(),
                        hoSoNhanVien.getAcademicTitle(),
                        hoSoNhanVien.getAcademicTitleName(),
                        hoSoNhanVien.getCertificate(),
                        hoSoNhanVien.getPosition(),
                        hoSoNhanVien.getPositionName(),
                        hoSoNhanVien.getHonorTitle(),
                        hoSoNhanVien.getSigningPin(),
                        hoSoNhanVien.getSigningAccount(),
                        hoSoNhanVien.getSigningOtp(),
                        hoSoNhanVien.getInvoicePassword(),
                        hoSoNhanVien.getAvatarImage(),
                        hoSoNhanVien.getSignatureImage(),
                        hoSoNhanVien.getLockedFrom(),
                        hoSoNhanVien.getLockedTo(),
                        hoSoNhanVien.getPrescriptionAccount(),
                        hoSoNhanVien.getPrescriptionPassword(),
                        hoSoNhanVien.getOnlineBooking(),
                        hoSoNhanVien.getActive(),
                        hoSoNhanVien.getNote()
                )
                .map(this::csvValue)
                .collect(Collectors.joining(","));
    }

    private String csvValue(Object value) {
        if (value == null) {
            return "";
        }
        String text = value.toString();
        if (text.contains("\"") || text.contains(",") || text.contains("\n") || text.contains("\r")) {
            return "\"" + text.replace("\"", "\"\"") + "\"";
        }
        return text;
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }

    private void validateLockedRange(LocalDate lockedFrom, LocalDate lockedTo) {
        if (lockedFrom != null && lockedTo != null && lockedFrom.isAfter(lockedTo)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Khoang ngay khoa ho so khong hop le");
        }
    }

    private void validateRequiredFields(String code, String name) {
        if (code == null || code.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ma ho so nhan vien khong duoc de trong");
        }
        if (name == null || name.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ten ho so nhan vien khong duoc de trong");
        }
    }

    private void validateNguoiDungLink(Long nguoiDungId, Long currentHoSoId) {
        if (nguoiDungId == null) {
            return;
        }

        if (!nguoiDungRepository.existsById(nguoiDungId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nguoi dung khong ton tai");
        }

        boolean linked = currentHoSoId == null
                ? hoSoNhanVienRepository.existsByNguoiDungId(nguoiDungId)
                : hoSoNhanVienRepository.existsByNguoiDungIdAndIdNot(nguoiDungId, currentHoSoId);
        if (linked) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Nguoi dung da co ho so nhan vien");
        }
    }
}
