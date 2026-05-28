package com.example.med_office.service;

import com.example.med_office.dto.HoSoNhanVienRequest;
import com.example.med_office.dto.HoSoNhanVienResponse;
import com.example.med_office.dto.ImportResultResponse;
import com.example.med_office.dto.PagedResponse;
import com.example.med_office.repository.ChucVuRepository;
import com.example.med_office.repository.ChuyenKhoaRepository;
import com.example.med_office.entity.HoSoNhanVien;
import com.example.med_office.repository.HoSoNhanVienRepository;
import com.example.med_office.repository.NguoiDungRepository;
import jakarta.persistence.criteria.Predicate;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class HoSoNhanVienServiceImpl implements HoSoNhanVienService {

    private final HoSoNhanVienRepository hoSoNhanVienRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final ChuyenKhoaRepository chuyenKhoaRepository;
    private final ChucVuRepository chucVuRepository;
    private static final List<DateTimeFormatter> EXCEL_DATE_FORMATS = List.of(
            DateTimeFormatter.ISO_LOCAL_DATE,
            DateTimeFormatter.ofPattern("d/M/yyyy"),
            DateTimeFormatter.ofPattern("d-M-yyyy"),
            DateTimeFormatter.ofPattern("d.M.yyyy")
    );

    public HoSoNhanVienServiceImpl(
            HoSoNhanVienRepository hoSoNhanVienRepository,
            NguoiDungRepository nguoiDungRepository,
            ChuyenKhoaRepository chuyenKhoaRepository,
            ChucVuRepository chucVuRepository
    ) {
        this.hoSoNhanVienRepository = hoSoNhanVienRepository;
        this.nguoiDungRepository = nguoiDungRepository;
        this.chuyenKhoaRepository = chuyenKhoaRepository;
        this.chucVuRepository = chucVuRepository;
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
            String nguoiDungId
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
    public HoSoNhanVienResponse findById(String id) {
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
    public HoSoNhanVienResponse update(String id, HoSoNhanVienRequest request) {
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
    public void delete(String id) {
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

    @Override
    @Transactional
    public ImportResultResponse importExcel(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File Excel khong duoc de trong");
        }

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getNumberOfSheets() > 0 ? workbook.getSheetAt(0) : null;
            if (sheet == null || sheet.getPhysicalNumberOfRows() < 2) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File Excel khong co du lieu");
            }

            Map<String, Integer> headers = readHeaders(sheet.getRow(0));
            int created = 0;
            int updated = 0;
            int skipped = 0;

            for (int index = 1; index <= sheet.getLastRowNum(); index++) {
                Row row = sheet.getRow(index);
                if (row == null || isBlankRow(row)) {
                    skipped++;
                    continue;
                }

                String code = cellText(row, headers, "code", "ma_nhan_vien", "ma_nv", "ho_so_nhan_vien_code");
                String name = cellText(row, headers, "name", "ho_ten", "ho_va_ten", "ten_nhan_vien", "ho_so_nhan_vien_name");
                if (code == null || code.isBlank() || name == null || name.isBlank()) {
                    skipped++;
                    continue;
                }

                HoSoNhanVien hoSoNhanVien = hoSoNhanVienRepository.findByCodeIgnoreCase(code)
                        .orElseGet(HoSoNhanVien::new);
                boolean isNew = hoSoNhanVien.getId() == null;

                try {
                    applyExcelRow(hoSoNhanVien, row, headers, code, name);
                } catch (RuntimeException ex) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Dong " + (index + 1) + " trong file Excel khong dung dinh dang"
                    );
                }
                hoSoNhanVienRepository.save(hoSoNhanVien);
                if (isNew) {
                    created++;
                } else {
                    updated++;
                }
            }

            return new ImportResultResponse(created, updated, skipped);
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File Excel khong dung mau hoac khong the import");
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File Excel khong dung mau hoac khong the import");
        }
    }

    private HoSoNhanVien findHoSoNhanVien(String id) {
        return hoSoNhanVienRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Khong tim thay ho so nhan vien"));
    }

    private Specification<HoSoNhanVien> buildSpecification(
            String keyword,
            Boolean active,
            Integer gender,
            Boolean onlineBooking,
            String nguoiDungId
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
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("specialty")), normalizedKeyword),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("certificate")), normalizedKeyword),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("position")), normalizedKeyword)
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
        hoSoNhanVien.setAcademicTitle(trim(request.academicTitle()));
        hoSoNhanVien.setAcademicTitleName(trim(request.academicTitleName()));
        hoSoNhanVien.setCertificate(trim(request.certificate()));
        hoSoNhanVien.setPosition(trim(request.position()));
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
                resolveSpecialtyName(hoSoNhanVien.getSpecialty()),
                hoSoNhanVien.getAcademicTitle(),
                hoSoNhanVien.getAcademicTitleName(),
                hoSoNhanVien.getCertificate(),
                hoSoNhanVien.getPosition(),
                resolvePositionName(hoSoNhanVien.getPosition()),
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
                        resolveSpecialtyName(hoSoNhanVien.getSpecialty()),
                        hoSoNhanVien.getAcademicTitle(),
                        hoSoNhanVien.getAcademicTitleName(),
                        hoSoNhanVien.getCertificate(),
                        hoSoNhanVien.getPosition(),
                        resolvePositionName(hoSoNhanVien.getPosition()),
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

    private void applyExcelRow(
            HoSoNhanVien hoSoNhanVien,
            Row row,
            Map<String, Integer> headers,
            String code,
            String name
    ) {
        hoSoNhanVien.setCode(trim(code));
        hoSoNhanVien.setName(trim(name));
        hoSoNhanVien.setNguoiDungId(cellText(row, headers, "nguoiDungId", "nguoi_dung_id", "user_id"));
        hoSoNhanVien.setBirthDate(cellDate(row, headers, "birthDate", "birth_date", "ngay_sinh"));
        hoSoNhanVien.setGender(cellGender(row, headers, "gender", "gioi_tinh"));
        hoSoNhanVien.setIdentityNumber(cellText(row, headers, "identityNumber", "identity_number", "cccd", "cmnd", "so_cccd", "so_cmnd"));
        hoSoNhanVien.setSocialInsurance(cellText(row, headers, "socialInsurance", "social_insurance", "bhxh", "so_bhxh"));
        hoSoNhanVien.setEmail(cellText(row, headers, "email"));
        hoSoNhanVien.setPhone(cellText(row, headers, "phone", "phone_number", "so_dien_thoai", "dien_thoai"));
        hoSoNhanVien.setDegree(cellText(row, headers, "degree", "bang_cap", "trinh_do"));
        hoSoNhanVien.setSpecialty(resolveSpecialtyId(cellText(row, headers, "specialty", "chuyen_khoa", "ten_chuyen_khoa", "chuyen_khoa_id", "id_chuyen_khoa")));
        hoSoNhanVien.setAcademicTitle(cellText(row, headers, "academicTitle", "academic_title", "hoc_ham"));
        hoSoNhanVien.setAcademicTitleName(cellText(row, headers, "academicTitleName", "academic_title_name", "ten_hoc_ham"));
        hoSoNhanVien.setCertificate(cellText(row, headers, "certificate", "chung_chi", "chung_chi_hanh_nghe"));
        hoSoNhanVien.setPosition(resolvePositionCode(cellText(row, headers, "position", "position_code", "ma_chuc_vu", "chuc_vu", "ten_chuc_vu", "chuc_vu_id")));
        hoSoNhanVien.setHonorTitle(cellText(row, headers, "honorTitle", "honor_title", "danh_hieu"));
        hoSoNhanVien.setSigningPin(cellText(row, headers, "signingPin", "signing_pin"));
        hoSoNhanVien.setSigningAccount(cellText(row, headers, "signingAccount", "signing_account"));
        hoSoNhanVien.setSigningOtp(cellText(row, headers, "signingOtp", "signing_otp"));
        hoSoNhanVien.setInvoicePassword(cellText(row, headers, "invoicePassword", "invoice_password"));
        hoSoNhanVien.setAvatarImage(cellText(row, headers, "avatarImage", "avatar_image"));
        hoSoNhanVien.setSignatureImage(cellText(row, headers, "signatureImage", "signature_image"));
        hoSoNhanVien.setLockedFrom(cellDate(row, headers, "lockedFrom", "locked_from"));
        hoSoNhanVien.setLockedTo(cellDate(row, headers, "lockedTo", "locked_to"));
        hoSoNhanVien.setPrescriptionAccount(cellText(row, headers, "prescriptionAccount", "prescription_account"));
        hoSoNhanVien.setPrescriptionPassword(cellText(row, headers, "prescriptionPassword", "prescription_password"));
        hoSoNhanVien.setOnlineBooking(Objects.requireNonNullElse(cellBoolean(row, headers, "onlineBooking", "online_booking"), false));
        hoSoNhanVien.setActive(Objects.requireNonNullElse(cellBoolean(row, headers, "active"), true));
        hoSoNhanVien.setNote(cellText(row, headers, "note"));
    }

    private Map<String, Integer> readHeaders(Row row) {
        if (row == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File Excel thieu dong tieu de");
        }
        Map<String, Integer> headers = new HashMap<>();
        for (Cell cell : row) {
            String text = normalizeHeader(cellText(cell));
            if (!text.isBlank()) {
                headers.put(text, cell.getColumnIndex());
            }
        }
        if (!headers.containsKey("code") && !headers.containsKey("ma_nhan_vien") && !headers.containsKey("ma_nv") && !headers.containsKey("ho_so_nhan_vien_code")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File Excel thieu cot ma nhan vien");
        }
        if (!headers.containsKey("name") && !headers.containsKey("ho_ten") && !headers.containsKey("ho_va_ten") && !headers.containsKey("ten_nhan_vien") && !headers.containsKey("ho_so_nhan_vien_name")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File Excel thieu cot ten nhan vien");
        }
        return headers;
    }

    private String cellText(Row row, Map<String, Integer> headers, String... names) {
        Integer index = headerIndex(headers, names);
        return index == null ? null : trim(cellText(row.getCell(index)));
    }

    private String cellText(Cell cell) {
        if (cell == null) {
            return null;
        }
        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue();
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            if (DateUtil.isCellDateFormatted(cell)) {
                return cell.getLocalDateTimeCellValue().toLocalDate().toString();
            }
            double value = cell.getNumericCellValue();
            long whole = (long) value;
            return value == whole ? Long.toString(whole) : Double.toString(value);
        }
        if (cell.getCellType() == CellType.BOOLEAN) {
            return Boolean.toString(cell.getBooleanCellValue());
        }
        if (cell.getCellType() == CellType.FORMULA) {
            return cell.getCellFormula();
        }
        return null;
    }

    private LocalDate cellDate(Row row, Map<String, Integer> headers, String... names) {
        Integer index = headerIndex(headers, names);
        if (index == null) {
            return null;
        }
        Cell cell = row.getCell(index);
        if (cell == null) {
            return null;
        }
        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return cell.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
        String text = trim(cellText(cell));
        if (text == null || text.isBlank()) {
            return null;
        }
        for (DateTimeFormatter formatter : EXCEL_DATE_FORMATS) {
            try {
                return LocalDate.parse(text, formatter);
            } catch (DateTimeParseException ignored) {
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ngay trong file Excel khong dung dinh dang");
    }

    private Integer cellInteger(Row row, Map<String, Integer> headers, String... names) {
        String text = cellText(row, headers, names);
        return text == null || text.isBlank() ? null : Integer.parseInt(text);
    }

    private Integer cellGender(Row row, Map<String, Integer> headers, String... names) {
        String text = cellText(row, headers, names);
        if (text == null || text.isBlank()) {
            return null;
        }
        String normalized = normalizeHeader(text);
        if (normalized.equals("nam") || normalized.equals("male")) {
            return 1;
        }
        if (normalized.equals("nu") || normalized.equals("female")) {
            return 2;
        }
        if (normalized.equals("khac") || normalized.equals("other")) {
            return 0;
        }
        return Integer.parseInt(text);
    }

    private Boolean cellBoolean(Row row, Map<String, Integer> headers, String... names) {
        String text = cellText(row, headers, names);
        if (text == null || text.isBlank()) {
            return null;
        }
        String normalized = text.trim().toLowerCase(Locale.ROOT);
        String noAccent = normalizeHeader(normalized);
        return noAccent.equals("true") || noAccent.equals("1") || noAccent.equals("yes") || noAccent.equals("co") || noAccent.equals("x");
    }

    private Integer headerIndex(Map<String, Integer> headers, String... names) {
        for (String name : names) {
            Integer index = headers.get(normalizeHeader(name));
            if (index != null) {
                return index;
            }
        }
        return null;
    }

    private boolean isBlankRow(Row row) {
        for (Cell cell : row) {
            String text = cellText(cell);
            if (text != null && !text.isBlank()) {
                return false;
            }
        }
        return true;
    }

    private String normalizeHeader(String value) {
        if (value == null) {
            return "";
        }
        String noAccent = Normalizer.normalize(value.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replace("đ", "d")
                .replace("Đ", "D");
        return noAccent.replaceAll("[^A-Za-z0-9]+", "_")
                .replaceAll("^_+|_+$", "")
                .toLowerCase(Locale.ROOT);
    }

    private String resolveSpecialtyId(String value) {
        String text = trim(value);
        if (text == null || text.isBlank()) {
            return null;
        }
        return chuyenKhoaRepository.findById(text)
                .map(chuyenKhoa -> chuyenKhoa.getIdChuyenKhoa())
                .or(() -> chuyenKhoaRepository.findByTenChuyenKhoaIgnoreCase(text).map(chuyenKhoa -> chuyenKhoa.getIdChuyenKhoa()))
                .orElse(text);
    }

    private String resolvePositionCode(String value) {
        String text = trim(value);
        if (text == null || text.isBlank()) {
            return null;
        }
        return chucVuRepository.findById(text)
                .map(chucVu -> chucVu.getMaChucVu())
                .or(() -> chucVuRepository.findByMaChucVuIgnoreCase(text).map(chucVu -> chucVu.getMaChucVu()))
                .or(() -> chucVuRepository.findByTenChucVuIgnoreCase(text).map(chucVu -> chucVu.getMaChucVu()))
                .orElse(text);
    }

    private String resolveSpecialtyName(String specialtyId) {
        if (specialtyId == null || specialtyId.isBlank()) {
            return null;
        }
        return chuyenKhoaRepository.findById(specialtyId)
                .map(chuyenKhoa -> chuyenKhoa.getTenChuyenKhoa())
                .orElse(null);
    }

    private String resolvePositionName(String positionCode) {
        if (positionCode == null || positionCode.isBlank()) {
            return null;
        }
        return chucVuRepository.findByMaChucVuIgnoreCase(positionCode)
                .map(chucVu -> chucVu.getTenChucVu())
                .or(() -> chucVuRepository.findById(positionCode).map(chucVu -> chucVu.getTenChucVu()))
                .orElse(null);
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

    private void validateNguoiDungLink(String nguoiDungId, String currentHoSoId) {
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
