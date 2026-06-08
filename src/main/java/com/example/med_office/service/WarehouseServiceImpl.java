package com.example.med_office.service;

import com.example.med_office.dto.WarehouseHierarchyItem;
import com.example.med_office.dto.WarehouseHierarchyUpdateRequest;
import com.example.med_office.dto.WarehouseManagerResponse;
import com.example.med_office.dto.WarehousePageResponse;
import com.example.med_office.dto.WarehouseRequest;
import com.example.med_office.dto.WarehouseResponse;
import com.example.med_office.dto.WarehouseStatusUpdateRequest;
import com.example.med_office.dto.WarehouseSummaryResponse;
import com.example.med_office.entity.HoSoNhanVien;
import com.example.med_office.entity.Warehouse;
import com.example.med_office.entity.WarehouseManager;
import com.example.med_office.entity.WarehouseManagerId;
import com.example.med_office.repository.HoSoNhanVienRepository;
import com.example.med_office.repository.WarehouseManagerRepository;
import com.example.med_office.repository.WarehouseRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class WarehouseServiceImpl implements WarehouseService {

    private static final String ACTIVE = "ACTIVE";
    private static final String INACTIVE = "INACTIVE";
    private static final Map<String, String> SORT_FIELDS = Map.of(
            "createdAt", "createdAt",
            "updatedAt", "updatedAt",
            "code", "code",
            "name", "name",
            "location", "location",
            "status", "status"
    );

    private final WarehouseRepository warehouseRepository;
    private final WarehouseManagerRepository warehouseManagerRepository;
    private final HoSoNhanVienRepository hoSoNhanVienRepository;
    private final WarehousePermissionScopeService warehousePermissionScopeService;

    public WarehouseServiceImpl(
            WarehouseRepository warehouseRepository,
            WarehouseManagerRepository warehouseManagerRepository,
            HoSoNhanVienRepository hoSoNhanVienRepository,
            WarehousePermissionScopeService warehousePermissionScopeService
    ) {
        this.warehouseRepository = warehouseRepository;
        this.warehouseManagerRepository = warehouseManagerRepository;
        this.hoSoNhanVienRepository = hoSoNhanVienRepository;
        this.warehousePermissionScopeService = warehousePermissionScopeService;
    }

    @Override
    @Transactional(readOnly = true)
    public WarehousePageResponse findAll(String keyword, String status, int page, int size, String sort) {
        Set<String> warehouseScope = resolveWarehouseScope();
        if (!warehousePermissionScopeService.isAdmin() && warehouseScope.isEmpty()) {
            return new WarehousePageResponse(List.of(), page, size, 0, 0);
        }

        Page<Warehouse> result = warehouseRepository.findAll(
                buildSpecification(keyword, status, warehouseScope),
                PageRequest.of(page, size, parseSort(sort))
        );
        Map<String, List<WarehouseManagerResponse>> managersByWarehouseId = getManagersByWarehouseIds(
                result.getContent().stream().map(Warehouse::getId).toList()
        );
        return new WarehousePageResponse(
                result.getContent().stream()
                        .map(warehouse -> toResponse(warehouse, managersByWarehouseId.getOrDefault(warehouse.getId(), List.of())))
                        .toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public WarehouseResponse findById(String id) {
        Warehouse warehouse = requireWarehouse(id);
        assertWarehouseAccess(warehouse.getId(), "Ban khong co quyen truy cap kho nay.");
        return toResponse(warehouse, getManagersByWarehouseIds(List.of(id)).getOrDefault(id, List.of()));
    }

    @Override
    @Transactional
    public WarehouseResponse create(WarehouseRequest request) {
        String code = trim(request.code());
        if (warehouseRepository.existsByCodeIgnoreCase(code)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ma kho da ton tai.");
        }
        validateManagerIds(request.managerIds());
        validateParentChange(null, request.parentWarehouseId());

        Warehouse warehouse = new Warehouse();
        applyRequest(warehouse, request);
        Warehouse savedWarehouse = warehouseRepository.save(warehouse);
        replaceManagers(savedWarehouse.getId(), request.managerIds());
        return findById(savedWarehouse.getId());
    }

    @Override
    @Transactional
    public WarehouseResponse update(String id, WarehouseRequest request) {
        Warehouse warehouse = requireWarehouse(id);
        String code = trim(request.code());
        if (warehouseRepository.existsByCodeIgnoreCaseAndIdNot(code, id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ma kho da ton tai.");
        }
        validateManagerIds(request.managerIds());
        validateParentChange(id, request.parentWarehouseId());

        applyRequest(warehouse, request);
        Warehouse savedWarehouse = warehouseRepository.save(warehouse);
        replaceManagers(id, request.managerIds());
        return findById(savedWarehouse.getId());
    }

    @Override
    @Transactional
    public WarehouseResponse updateStatus(String id, WarehouseStatusUpdateRequest request) {
        Warehouse warehouse = requireWarehouse(id);
        warehouse.setStatus(request.active() ? ACTIVE : INACTIVE);
        warehouseRepository.save(warehouse);
        return findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WarehouseHierarchyItem> getHierarchy() {
        return buildHierarchy(findWarehousesInScope());
    }

    @Override
    @Transactional
    public List<WarehouseHierarchyItem> updateHierarchy(WarehouseHierarchyUpdateRequest request) {
        List<Warehouse> warehouses = warehouseRepository.findAll();
        Map<String, Warehouse> warehousesById = warehouses.stream()
                .collect(Collectors.toMap(Warehouse::getId, Function.identity()));
        Map<String, String> parentByWarehouseId = getParentByWarehouseId(warehouses);
        Set<String> updatedWarehouseIds = new HashSet<>();

        for (WarehouseHierarchyUpdateRequest.WarehouseParentUpdate update : request.warehouses()) {
            String id = trim(update.id());
            if (!updatedWarehouseIds.add(id)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Danh sach cau hinh kho bi trung.");
            }
            if (!warehousesById.containsKey(id)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kho khong ton tai.");
            }
            parentByWarehouseId.put(id, normalizeId(update.parentWarehouseId()));
        }
        validateHierarchy(parentByWarehouseId);

        updatedWarehouseIds.forEach(id -> warehousesById.get(id).setParentWarehouseId(parentByWarehouseId.get(id)));
        warehouseRepository.saveAll(updatedWarehouseIds.stream().map(warehousesById::get).toList());
        return buildHierarchy(warehouses);
    }

    @Override
    @Transactional(readOnly = true)
    public WarehouseSummaryResponse getSummary() {
        List<Warehouse> warehouses = findWarehousesInScope();
        long totalWarehouses = warehouses.size();
        long activeWarehouses = warehouses.stream()
                .filter(warehouse -> ACTIVE.equalsIgnoreCase(warehouse.getStatus()))
                .count();
        return new WarehouseSummaryResponse(totalWarehouses, activeWarehouses, totalWarehouses - activeWarehouses, 0);
    }

    private Specification<Warehouse> buildSpecification(String keyword, String status, Set<String> warehouseScope) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (warehouseScope != null && !warehouseScope.isEmpty()) {
                predicates.add(root.get("id").in(warehouseScope));
            }
            if (keyword != null && !keyword.isBlank()) {
                String normalizedKeyword = "%" + keyword.trim().toLowerCase(Locale.ROOT) + "%";
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("code")), normalizedKeyword),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), normalizedKeyword),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("location")), normalizedKeyword)
                ));
            }
            if (status != null && !status.isBlank()) {
                predicates.add(criteriaBuilder.equal(criteriaBuilder.upper(root.get("status")), status.trim().toUpperCase(Locale.ROOT)));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private Sort parseSort(String value) {
        String[] tokens = value == null ? new String[0] : value.split(",", 2);
        String property = SORT_FIELDS.getOrDefault(tokens.length > 0 ? tokens[0].trim() : "", "createdAt");
        Sort.Direction direction = tokens.length > 1 && "asc".equalsIgnoreCase(tokens[1].trim())
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        return Sort.by(direction, property).and(Sort.by(direction, "id"));
    }

    private void applyRequest(Warehouse warehouse, WarehouseRequest request) {
        warehouse.setCode(trim(request.code()));
        warehouse.setName(trim(request.name()));
        warehouse.setType(trim(request.type()));
        warehouse.setLocation(trim(request.location()));
        warehouse.setNote(trim(request.note()));
        warehouse.setStatus(Boolean.FALSE.equals(request.active()) ? INACTIVE : ACTIVE);
        warehouse.setParentWarehouseId(normalizeId(request.parentWarehouseId()));
    }

    private void validateManagerIds(List<String> managerIds) {
        List<String> normalizedIds = normalizeIds(managerIds);
        if (hoSoNhanVienRepository.findAllById(normalizedIds).size() != normalizedIds.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nguoi phu trach kho khong ton tai.");
        }
    }

    private void replaceManagers(String warehouseId, List<String> managerIds) {
        warehouseManagerRepository.deleteByIdWarehouseId(warehouseId);
        warehouseManagerRepository.saveAll(normalizeIds(managerIds).stream()
                .map(employeeProfileId -> {
                    WarehouseManager manager = new WarehouseManager();
                    manager.setId(new WarehouseManagerId(warehouseId, employeeProfileId));
                    return manager;
                })
                .toList());
    }

    private Map<String, List<WarehouseManagerResponse>> getManagersByWarehouseIds(Collection<String> warehouseIds) {
        if (warehouseIds.isEmpty()) {
            return Map.of();
        }
        List<WarehouseManager> managers = warehouseManagerRepository.findByIdWarehouseIdIn(warehouseIds);
        Map<String, HoSoNhanVien> profilesById = hoSoNhanVienRepository.findAllById(
                        managers.stream().map(manager -> manager.getId().getEmployeeProfileId()).toList()
                ).stream()
                .collect(Collectors.toMap(HoSoNhanVien::getId, Function.identity()));
        return managers.stream()
                .map(manager -> Map.entry(manager.getId().getWarehouseId(), profilesById.get(manager.getId().getEmployeeProfileId())))
                .filter(entry -> entry.getValue() != null)
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        LinkedHashMap::new,
                        Collectors.mapping(entry -> toManagerResponse(entry.getValue()), Collectors.toList())
                ));
    }

    private WarehouseManagerResponse toManagerResponse(HoSoNhanVien profile) {
        return new WarehouseManagerResponse(profile.getId(), profile.getCode(), profile.getName(), profile.getPhone());
    }

    private WarehouseResponse toResponse(Warehouse warehouse, List<WarehouseManagerResponse> managers) {
        return new WarehouseResponse(
                warehouse.getId(),
                warehouse.getCode(),
                warehouse.getName(),
                warehouse.getType(),
                warehouse.getLocation(),
                warehouse.getNote(),
                warehouse.getStatus(),
                0,
                warehouse.getParentWarehouseId(),
                managers,
                warehouse.getCreatedAt(),
                warehouse.getUpdatedAt()
        );
    }

    private List<WarehouseHierarchyItem> buildHierarchy(List<Warehouse> warehouses) {
        Map<String, List<WarehouseManagerResponse>> managersByWarehouseId = getManagersByWarehouseIds(
                warehouses.stream().map(Warehouse::getId).toList()
        );
        Set<String> visibleWarehouseIds = warehouses.stream()
                .map(Warehouse::getId)
                .collect(Collectors.toSet());
        Map<String, List<Warehouse>> childrenByParentId = warehouses.stream()
                .filter(warehouse -> warehouse.getParentWarehouseId() != null)
                .collect(Collectors.groupingBy(Warehouse::getParentWarehouseId));
        return warehouses.stream()
                .filter(warehouse -> warehouse.getParentWarehouseId() == null
                        || !visibleWarehouseIds.contains(warehouse.getParentWarehouseId()))
                .sorted(Comparator.comparing(Warehouse::getCode, String.CASE_INSENSITIVE_ORDER).thenComparing(Warehouse::getId))
                .map(warehouse -> toHierarchyItem(warehouse, childrenByParentId, managersByWarehouseId))
                .toList();
    }

    private WarehouseHierarchyItem toHierarchyItem(
            Warehouse warehouse,
            Map<String, List<Warehouse>> childrenByParentId,
            Map<String, List<WarehouseManagerResponse>> managersByWarehouseId
    ) {
        List<WarehouseHierarchyItem> children = childrenByParentId.getOrDefault(warehouse.getId(), List.of()).stream()
                .sorted(Comparator.comparing(Warehouse::getCode, String.CASE_INSENSITIVE_ORDER).thenComparing(Warehouse::getId))
                .map(child -> toHierarchyItem(child, childrenByParentId, managersByWarehouseId))
                .toList();
        return new WarehouseHierarchyItem(
                warehouse.getId(),
                warehouse.getCode(),
                warehouse.getName(),
                warehouse.getParentWarehouseId(),
                warehouse.getStatus(),
                managersByWarehouseId.getOrDefault(warehouse.getId(), List.of()),
                children
        );
    }

    private void validateParentChange(String warehouseId, String parentWarehouseId) {
        Map<String, String> parentByWarehouseId = getParentByWarehouseId(warehouseRepository.findAll());
        if (warehouseId != null) {
            parentByWarehouseId.put(warehouseId, normalizeId(parentWarehouseId));
        } else if (normalizeId(parentWarehouseId) != null && !parentByWarehouseId.containsKey(normalizeId(parentWarehouseId))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kho cha khong ton tai.");
        }
        validateHierarchy(parentByWarehouseId);
    }

    private void validateHierarchy(Map<String, String> parentByWarehouseId) {
        for (Map.Entry<String, String> entry : parentByWarehouseId.entrySet()) {
            String warehouseId = entry.getKey();
            String parentWarehouseId = entry.getValue();
            if (warehouseId.equals(parentWarehouseId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kho khong the la cha cua chinh no.");
            }
            if (parentWarehouseId != null && !parentByWarehouseId.containsKey(parentWarehouseId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kho cha khong ton tai.");
            }
        }
        for (String warehouseId : parentByWarehouseId.keySet()) {
            Set<String> visited = new HashSet<>();
            String currentId = warehouseId;
            while (currentId != null) {
                if (!visited.add(currentId)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Khong the chon kho truc thuoc lam kho cha.");
                }
                currentId = parentByWarehouseId.get(currentId);
            }
        }
    }

    private Map<String, String> getParentByWarehouseId(List<Warehouse> warehouses) {
        Map<String, String> parentByWarehouseId = new HashMap<>();
        warehouses.forEach(warehouse -> parentByWarehouseId.put(warehouse.getId(), warehouse.getParentWarehouseId()));
        return parentByWarehouseId;
    }

    private Warehouse requireWarehouse(String id) {
        return warehouseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Khong tim thay kho."));
    }

    private List<Warehouse> findWarehousesInScope() {
        if (warehousePermissionScopeService.isAdmin()) {
            return warehouseRepository.findAll(Sort.by("code").ascending().and(Sort.by("id").ascending()));
        }

        Set<String> warehouseScope = resolveWarehouseScope();
        if (warehouseScope.isEmpty()) {
            return List.of();
        }

        return warehouseRepository.findAllById(warehouseScope).stream()
                .sorted(Comparator.comparing(Warehouse::getCode, String.CASE_INSENSITIVE_ORDER).thenComparing(Warehouse::getId))
                .toList();
    }

    private Set<String> resolveWarehouseScope() {
        return warehousePermissionScopeService.isAdmin() ? Set.of() : warehousePermissionScopeService.getManagedWarehouseIds();
    }

    private void assertWarehouseAccess(String warehouseId, String message) {
        warehousePermissionScopeService.assertWarehouseAccess(warehouseId, message);
    }

    private List<String> normalizeIds(List<String> values) {
        if (values == null) {
            return List.of();
        }
        return values.stream()
                .map(this::normalizeId)
                .filter(value -> value != null)
                .collect(Collectors.toCollection(LinkedHashSet::new))
                .stream()
                .toList();
    }

    private String normalizeId(String value) {
        String normalized = trim(value);
        return normalized == null || normalized.isBlank() ? null : normalized;
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }
}

