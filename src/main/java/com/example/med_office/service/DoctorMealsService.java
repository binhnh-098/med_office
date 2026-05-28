package com.example.med_office.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.example.med_office.dto.DoctorMealDishCreateRequest;
import com.example.med_office.dto.DoctorMealDishUpdateRequest;
import com.example.med_office.dto.DoctorMealRegistrationRequest;
import com.example.med_office.entity.DoctorMealDish;
import com.example.med_office.entity.DoctorMealRegistration;
import com.example.med_office.entity.DoctorMealRegistrationItem;
import com.example.med_office.entity.DoctorMealRegistrationItemSnapshot;
import com.example.med_office.repository.DoctorMealDishRepository;
import com.example.med_office.repository.DoctorMealRegistrationItemRepository;
import com.example.med_office.repository.DoctorMealRegistrationItemSnapshotRepository;
import com.example.med_office.repository.DoctorMealRegistrationRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class DoctorMealsService {

    private static final List<String> WEEK_DAYS = List.of(
            "Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "Chủ Nhật"
    );
    private static final List<String> MEAL_IDS = List.of("breakfast", "lunch", "dinner");
    private static final Map<String, String> MEAL_LABELS = Map.of(
            "breakfast", "Sáng",
            "lunch", "Trưa",
            "dinner", "Tối"
    );
    private static final ZoneId BUSINESS_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final List<DateTimeFormatter> SERVING_TIME_FORMATTERS = List.of(
            DateTimeFormatter.ofPattern("H:mm"),
            DateTimeFormatter.ofPattern("HH:mm"),
            DateTimeFormatter.ofPattern("H:mm:ss"),
            DateTimeFormatter.ofPattern("HH:mm:ss")
    );

    private final DoctorMealDishRepository dishRepository;
    private final DoctorMealRegistrationRepository registrationRepository;
    private final DoctorMealRegistrationItemRepository registrationItemRepository;
    private final DoctorMealRegistrationItemSnapshotRepository registrationItemSnapshotRepository;
    private final ObjectMapper objectMapper = JsonMapper.builder().findAndAddModules().build();

    public DoctorMealsService(
            DoctorMealDishRepository dishRepository,
            DoctorMealRegistrationRepository registrationRepository,
            DoctorMealRegistrationItemRepository registrationItemRepository,
            DoctorMealRegistrationItemSnapshotRepository registrationItemSnapshotRepository
    ) {
        this.dishRepository = dishRepository;
        this.registrationRepository = registrationRepository;
        this.registrationItemRepository = registrationItemRepository;
        this.registrationItemSnapshotRepository = registrationItemSnapshotRepository;
    }

    public Map<String, Object> getWeekData(int weekYear, int weekNumber, String username) {
        Map<String, Object> menuByDayMeal = emptyMenuByDayMeal();
        mergeSavedDishes(weekYear, weekNumber, menuByDayMeal);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("week", Map.of("year", weekYear, "number", weekNumber));
        body.put("menuByDayMeal", menuByDayMeal);
        body.put("selections", Map.of());
        return body;
    }

    public Map<String, Object> getDishesByDay(int weekYear, int weekNumber, String dayOfWeek, String username) {
        if (dayOfWeek == null || dayOfWeek.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing dayOfWeek");
        }
        String normalizedDayOfWeek = dayOfWeek.trim();

        Map<String, Object> meals = emptyMeals();
        List<Map<String, Object>> items = new ArrayList<>();

        for (DoctorMealDish dish : dishRepository.findByWeekYearAndWeekNumberAndDayOfWeekOrderByMealIdAscIdAsc(
                weekYear,
                weekNumber,
                normalizedDayOfWeek
        )) {
            Map<String, Object> dishItem = toDishByDayItem(dish);
            items.add(dishItem);

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> mealDishes = (List<Map<String, Object>>) meals
                    .computeIfAbsent(dish.getMealId(), k -> new ArrayList<Map<String, Object>>());
            mealDishes.add(dishItem);
        }

        Map<String, Object> out = new LinkedHashMap<>();
        out.put("weekYear", weekYear);
        out.put("weekNumber", weekNumber);
        out.put("dayOfWeek", normalizedDayOfWeek);
        out.put("meals", meals);
        out.put("items", items);
        out.put("totalDishes", items.size());
        out.put("totalQuantity", 0);
        return out;
    }

    @Transactional
    public Map<String, Object> createDishes(String principalName, DoctorMealDishCreateRequest request) {
        String mealId = normalizeRequired(request.getMealId());
        if (!MEAL_IDS.contains(mealId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "mealId must be one of breakfast, lunch, dinner");
        }

        LocalDate today = LocalDate.now(BUSINESS_ZONE);
        if (request.getDate().isBefore(today)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot add dishes to a past date");
        }

        String dayOfWeek = normalizeRequired(request.getDayOfWeek());
        String mealLabel = normalizeMealLabel(request.getMealLabel(), mealId);
        List<DoctorMealDish> entities = new ArrayList<>();

        for (DoctorMealDishCreateRequest.DishRequest dishRequest : request.getDishes()) {
            BigDecimal price = resolveNonNegativeAmount(dishRequest.getPrice(), dishRequest.getUnitPrice());
            BigDecimal unitPrice = resolveNonNegativeAmount(dishRequest.getUnitPrice(), dishRequest.getPrice());

            DoctorMealDish dish = new DoctorMealDish();
            dish.setWeekYear(request.getWeekYear());
            dish.setWeekNumber(request.getWeekNumber());
            dish.setDayOfWeek(dayOfWeek);
            dish.setDate(request.getDate());
            dish.setMealId(mealId);
            dish.setMealLabel(mealLabel);
            dish.setName(normalizeRequired(dishRequest.getName()));
            dish.setPrice(price);
            dish.setUnitPrice(unitPrice);
            dish.setCalories(dishRequest.getCalories());
            dish.setServingTime(blankToNull(dishRequest.getServingTime()));
            dish.setNote(blankToNull(dishRequest.getNote()));
            dish.setCreatedBy(principalName);
            entities.add(dish);
        }

        List<DoctorMealDish> savedDishes = dishRepository.saveAll(entities);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("weekYear", request.getWeekYear());
        data.put("weekNumber", request.getWeekNumber());
        data.put("dayOfWeek", dayOfWeek);
        data.put("date", request.getDate().toString());
        data.put("mealId", mealId);
        data.put("mealLabel", mealLabel);
        data.put("dishes", savedDishes.stream().map(this::toCreatedDishResponse).toList());
        return data;
    }

    @Transactional
    public Map<String, Object> createRegistration(String principalName, DoctorMealRegistrationRequest request) {
        DoctorMealRegistrationRequest.WeekRequest week = request.getWeek();
        DoctorMealRegistrationRequest.RequesterRequest requester = request.getRequester();
        DoctorMealRegistrationRequest.SummaryRequest summary = request.getSummary();

        DoctorMealRegistration entity = new DoctorMealRegistration();
        String requesterUsername = requester != null && blankToNull(requester.getUsername()) != null
                ? requester.getUsername().trim()
                : principalName;
        entity.setWeekYear(week.getYear());
        entity.setWeekNumber(week.getNumber());
        entity.setWeekLabel(blankToNull(week.getLabel()));
        entity.setWeekStartDate(week.getStartDate());
        entity.setWeekEndDate(week.getEndDate());
        entity.setUsername(principalName);
        entity.setRequesterUsername(requesterUsername);
        if (requester != null) {
            entity.setRequesterFullName(blankToNull(requester.getFullName()));
            entity.setRequesterDepartment(blankToNull(requester.getDepartment()));
            entity.setRequesterRole(blankToNull(requester.getRole()));
        }

        int totalQuantity = summary != null && summary.getTotalQuantity() != null ? summary.getTotalQuantity() : 0;
        BigDecimal totalAmount = summary != null && summary.getTotalAmount() != null ? summary.getTotalAmount() : BigDecimal.ZERO;

        for (DoctorMealRegistrationRequest.ItemRequest requestItem : request.getItems()) {
            DoctorMealRegistrationItem item = new DoctorMealRegistrationItem();
            item.setDate(requestItem.getDate());
            item.setDayOfWeek(blankToNull(requestItem.getDayOfWeek()));
            item.setMealId(blankToNull(requestItem.getMealId()));
            item.setMealLabel(blankToNull(requestItem.getMealLabel()));
            item.setMealQuantity(nonNegativeOrZero(requestItem.getMealQuantity()));
            item.setMealAmount(nonNullAmount(requestItem.getMealAmount()));

            List<DoctorMealRegistrationRequest.MealSnapshotRequest> snapshots = requestItem.getMealSnapshots();
            if (snapshots == null || snapshots.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Each registration item must have mealSnapshots");
            }
            for (DoctorMealRegistrationRequest.MealSnapshotRequest snapshotRequest : snapshots) {
                DoctorMealRegistrationItemSnapshot snapshot = new DoctorMealRegistrationItemSnapshot();
                snapshot.setName(normalizeRequired(snapshotRequest.getName()));
                snapshot.setServingTime(blankToNull(snapshotRequest.getServingTime()));
                snapshot.setQuantity(nonNegativeOrZero(snapshotRequest.getQuantity()));
                snapshot.setUnitPrice(nonNullAmount(snapshotRequest.getUnitPrice()));
                snapshot.setLineTotal(nonNullAmount(snapshotRequest.getLineTotal()));
                item.addMealSnapshot(snapshot);
            }

            entity.addItem(item);
            if (summary == null) {
                totalQuantity += item.getMealQuantity();
                totalAmount = totalAmount.add(item.getMealAmount());
            }
        }

        entity.setTotalQuantity(totalQuantity);
        entity.setTotalAmount(totalAmount);
        entity.setPayloadJson(objectMapper.valueToTree(request).toString());

        DoctorMealRegistration saved = registrationRepository.save(entity);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", saved.getId());
        data.put("createdAt", saved.getCreatedAt().toString());
        data.put("weekYear", saved.getWeekYear());
        data.put("weekNumber", saved.getWeekNumber());
        data.put("totalQuantity", saved.getTotalQuantity());
        data.put("totalAmount", saved.getTotalAmount());
        return data;
    }

    @Transactional
    public Map<String, Object> updateDish(String id, String principalName, DoctorMealDishUpdateRequest request) {
        DoctorMealDish dish = getOwnedDish(id, principalName);
        ensureDishDateIsEditable(dish.getDate(), "Cannot update dishes from a past date");

        if (request.getWeekYear() != null) {
            dish.setWeekYear(request.getWeekYear());
        }
        if (request.getWeekNumber() != null) {
            dish.setWeekNumber(request.getWeekNumber());
        }
        if (request.getDayOfWeek() != null) {
            dish.setDayOfWeek(normalizeRequired(request.getDayOfWeek()));
        }
        if (request.getDate() != null) {
            ensureDishDateIsEditable(request.getDate(), "Cannot move dishes to a past date");
            dish.setDate(request.getDate());
        }
        if (request.getMealId() != null) {
            String mealId = normalizeRequired(request.getMealId());
            if (!MEAL_IDS.contains(mealId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "mealId must be one of breakfast, lunch, dinner");
            }
            dish.setMealId(mealId);
            if (request.getMealLabel() != null) {
                dish.setMealLabel(normalizeMealLabel(request.getMealLabel(), mealId));
            }
        } else if (request.getMealLabel() != null) {
            dish.setMealLabel(normalizeMealLabel(request.getMealLabel(), dish.getMealId()));
        }
        if (request.getName() != null) {
            dish.setName(normalizeRequired(request.getName()));
        }
        if (request.getPrice() != null) {
            dish.setPrice(resolveNonNegativeAmount(request.getPrice(), null));
        }
        if (request.getUnitPrice() != null) {
            dish.setUnitPrice(resolveNonNegativeAmount(request.getUnitPrice(), null));
        }
        if (request.getCalories() != null) {
            dish.setCalories(request.getCalories());
        }
        if (request.getServingTime() != null) {
            dish.setServingTime(blankToNull(request.getServingTime()));
        }
        if (request.getNote() != null) {
            dish.setNote(blankToNull(request.getNote()));
        }

        DoctorMealDish saved = dishRepository.save(dish);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("dish", toDishResponse(saved));
        return data;
    }

    public Map<String, Object> listRegistrations(
            int weekYear,
            int weekNumber,
            String username,
            int limit,
            int offset
    ) {
        List<DoctorMealRegistration> all = findRegistrations(weekYear, weekNumber, username);

        int from = Math.min(Math.max(offset, 0), all.size());
        int to = Math.min(from + Math.min(Math.max(limit, 1), 200), all.size());
        List<DoctorMealRegistration> slice = all.subList(from, to);

        List<Map<String, Object>> items = new ArrayList<>();
        for (DoctorMealRegistration row : slice) {
            items.add(toListItem(row));
        }

        Map<String, Object> out = new LinkedHashMap<>();
        out.put("items", items);
        out.put("total", all.size());
        out.put("limit", limit);
        out.put("offset", offset);
        return out;
    }

    public Map<String, Object> getRegistrationDetail(String id, String username) {
        DoctorMealRegistration row = findRegistrationByIdAndUser(id, username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Registration not found"));

        Map<String, Object> detail = new LinkedHashMap<>();
        detail.put("id", row.getId());
        detail.put("createdAt", row.getCreatedAt().toString());
        detail.put("week", toWeekMap(row));
        detail.put("requester", toRequesterMap(row));
        detail.put("summary", toSummaryMap(row));
        List<Map<String, Object>> items = toRegistrationItemResponses(row);
        detail.put("items", items.isEmpty() ? toLegacyRegistrationItemResponses(row) : items);
        return detail;
    }

    private Map<String, Object> toDishByDayItem(String dayOfWeek, String mealId, JsonNode dish) {
        Map<String, Object> dishItem = new LinkedHashMap<>();
        dishItem.put("dayOfWeek", dayOfWeek);
        dishItem.put("mealId", mealId);
        dishItem.put("dishId", dish.path("dishId").asText(""));
        dishItem.put("id", dish.path("dishId").asText(""));
        dishItem.put("name", dish.path("name").asText(""));
        dishItem.put("unitPrice", dish.path("unitPrice").asDouble(0));
        dishItem.put("price", dish.path("unitPrice").asDouble(0));
        dishItem.put("servingTime", dish.path("servingTime").asText(""));
        dishItem.put("quantity", dish.path("quantity").asInt(0));
        return dishItem;
    }

    private Map<String, Object> toDishByDayItem(DoctorMealDish dish) {
        Map<String, Object> dishItem = new LinkedHashMap<>();
        dishItem.put("dayOfWeek", dish.getDayOfWeek());
        dishItem.put("date", dish.getDate().toString());
        dishItem.put("mealId", dish.getMealId());
        dishItem.put("mealLabel", dish.getMealLabel());
        dishItem.put("dishId", dish.getId());
        dishItem.put("id", dish.getId());
        dishItem.put("name", dish.getName());
        dishItem.put("unitPrice", dish.getUnitPrice());
        dishItem.put("price", dish.getPrice());
        dishItem.put("calories", dish.getCalories());
        dishItem.put("servingTime", dish.getServingTime());
        dishItem.put("note", dish.getNote());
        dishItem.put("quantity", 0);
        dishItem.put("canDelete", canModifyDish(dish));
        return dishItem;
    }

    @Transactional
    public void deleteRegistration(String id, String username) {
        DoctorMealRegistration row = findRegistrationByIdAndUser(id, username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Registration not found"));
        registrationRepository.delete(row);
    }

    @Transactional
    public void deleteDish(String id, String principalName) {
        DoctorMealDish dish = getOwnedDish(id, principalName);
        ensureDishCanBeDeleted(dish);
        dishRepository.delete(dish);
    }

    @Transactional
    public Map<String, Object> deleteRegistrationItemDish(
            String registrationId,
            String itemId,
            String dishId,
            String username
    ) {
        DoctorMealRegistration registration = findRegistrationByIdAndUser(registrationId, username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Registration not found"));

        DoctorMealRegistrationItem item = registrationItemRepository.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Registration item not found"));
        if (!registration.getId().equals(item.getRegistration().getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Registration item not found");
        }

        DoctorMealRegistrationItemSnapshot snapshot = registrationItemSnapshotRepository.findById(dishId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Registration dish not found"));
        if (!item.getId().equals(snapshot.getRegistrationItem().getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Registration dish not found");
        }

        registrationItemSnapshotRepository.delete(snapshot);

        List<DoctorMealRegistrationItemSnapshot> remainingSnapshots = registrationItemSnapshotRepository
                .findByRegistrationItemIdOrderByIdAsc(item.getId())
                .stream()
                .filter(row -> !row.getId().equals(dishId))
                .toList();
        boolean itemDeleted = remainingSnapshots.isEmpty();
        if (itemDeleted) {
            registrationItemRepository.delete(item);
        } else {
            updateItemTotals(item, remainingSnapshots);
            registrationItemRepository.save(item);
        }

        updateRegistrationTotals(registration);
        syncRegistrationPayloadFromCurrentItems(registration);
        registrationRepository.save(registration);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("registrationId", registration.getId());
        data.put("itemId", item.getId());
        data.put("dishId", dishId);
        data.put("itemDeleted", itemDeleted);
        data.put("summary", toSummaryMap(registration));
        return data;
    }

    private Map<String, Object> toListItem(DoctorMealRegistration row) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", row.getId());
        item.put("createdAt", row.getCreatedAt().toString());
        item.put("requester", toRequesterMap(row));
        item.put("summary", toSummaryMap(row));
        item.put("week", toWeekMap(row));
        return item;
    }

    private Map<String, Object> toCreatedDishResponse(DoctorMealDish dish) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", dish.getId());
        item.put("name", dish.getName());
        item.put("price", dish.getPrice());
        item.put("unitPrice", dish.getUnitPrice());
        item.put("calories", dish.getCalories());
        item.put("servingTime", dish.getServingTime());
        item.put("note", dish.getNote());
        item.put("canDelete", canModifyDish(dish));
        return item;
    }

    private Map<String, Object> toDishResponse(DoctorMealDish dish) {
        Map<String, Object> item = toCreatedDishResponse(dish);
        item.put("weekYear", dish.getWeekYear());
        item.put("weekNumber", dish.getWeekNumber());
        item.put("dayOfWeek", dish.getDayOfWeek());
        item.put("date", dish.getDate().toString());
        item.put("mealId", dish.getMealId());
        item.put("mealLabel", dish.getMealLabel());
        item.put("createdBy", dish.getCreatedBy());
        item.put("createdAt", dish.getCreatedAt().toString());
        return item;
    }

    private DoctorMealDish getOwnedDish(String id, String principalName) {
        DoctorMealDish dish = dishRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dish not found"));
        if (!principalName.equals(dish.getCreatedBy())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot modify another user's dish");
        }
        return dish;
    }

    private List<DoctorMealRegistration> findRegistrations(int weekYear, int weekNumber, String username) {
        List<DoctorMealRegistration> rows = registrationRepository
                .findByWeekYearAndWeekNumberAndRequesterUsernameOrderByIdDesc(weekYear, weekNumber, username);
        if (!rows.isEmpty()) {
            return rows;
        }
        return registrationRepository.findByWeekYearAndWeekNumberAndUsernameOrderByIdDesc(weekYear, weekNumber, username);
    }

    private Optional<DoctorMealRegistration> findRegistrationByIdAndUser(String id, String username) {
        Optional<DoctorMealRegistration> row = registrationRepository.findByIdAndRequesterUsername(id, username);
        if (row.isPresent()) {
            return row;
        }
        return registrationRepository.findByIdAndUsername(id, username);
    }

    private int nonNegativeOrZero(Integer value) {
        return value == null ? 0 : Math.max(value, 0);
    }

    private BigDecimal nonNullAmount(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private Map<String, Object> toWeekMap(DoctorMealRegistration row) {
        Map<String, Object> week = new LinkedHashMap<>();
        week.put("year", row.getWeekYear());
        week.put("number", row.getWeekNumber());
        week.put("label", row.getWeekLabel());
        week.put("startDate", row.getWeekStartDate() == null ? null : row.getWeekStartDate().toString());
        week.put("endDate", row.getWeekEndDate() == null ? null : row.getWeekEndDate().toString());
        return week;
    }

    private Map<String, Object> toRequesterMap(DoctorMealRegistration row) {
        Map<String, Object> requester = new LinkedHashMap<>();
        requester.put("username", row.getRequesterUsername() != null ? row.getRequesterUsername() : row.getUsername());
        requester.put("fullName", row.getRequesterFullName());
        requester.put("department", row.getRequesterDepartment());
        requester.put("role", row.getRequesterRole());
        return requester;
    }

    private Map<String, Object> toSummaryMap(DoctorMealRegistration row) {
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("totalQuantity", row.getTotalQuantity());
        summary.put("totalAmount", row.getTotalAmount());
        return summary;
    }

    private List<Map<String, Object>> toRegistrationItemResponses(DoctorMealRegistration row) {
        List<Map<String, Object>> responses = new ArrayList<>();
        for (DoctorMealRegistrationItem item : registrationItemRepository.findByRegistrationIdOrderByIdAsc(row.getId())) {
            Map<String, Object> itemMap = new LinkedHashMap<>();
            itemMap.put("id", item.getId());
            itemMap.put("date", item.getDate().toString());
            itemMap.put("dayOfWeek", item.getDayOfWeek());
            itemMap.put("mealId", item.getMealId());
            itemMap.put("mealLabel", item.getMealLabel());
            itemMap.put("mealQuantity", item.getMealQuantity());
            itemMap.put("mealAmount", item.getMealAmount());
            itemMap.put("mealSnapshots", toSnapshotResponses(item));
            responses.add(itemMap);
        }
        return responses;
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> toLegacyRegistrationItemResponses(DoctorMealRegistration row) {
        List<Map<String, Object>> responses = new ArrayList<>();
        JsonNode root = readTree(row.getPayloadJson());
        JsonNode itemsNode = root.path("items");
        if (!itemsNode.isArray()) {
            return responses;
        }

        int index = 1;
        for (JsonNode itemNode : itemsNode) {
            Map<String, Object> itemMap = objectMapper.convertValue(itemNode, Map.class);
            itemMap.putIfAbsent("id", "legacy-line-" + index);

            Object mealSnapshots = itemMap.get("mealSnapshots");
            itemMap.put("mealSnapshots", mealSnapshots == null ? List.of() : mealSnapshots);
            responses.add(itemMap);
            index++;
        }
        return responses;
    }

    private List<Map<String, Object>> toSnapshotResponses(DoctorMealRegistrationItem item) {
        List<Map<String, Object>> responses = new ArrayList<>();
        for (DoctorMealRegistrationItemSnapshot snapshot : registrationItemSnapshotRepository
                .findByRegistrationItemIdOrderByIdAsc(item.getId())) {
            Map<String, Object> snapshotMap = new LinkedHashMap<>();
            snapshotMap.put("id", snapshot.getId());
            snapshotMap.put("name", snapshot.getName());
            snapshotMap.put("servingTime", snapshot.getServingTime());
            snapshotMap.put("quantity", snapshot.getQuantity());
            snapshotMap.put("unitPrice", snapshot.getUnitPrice());
            snapshotMap.put("lineTotal", snapshot.getLineTotal());
            responses.add(snapshotMap);
        }
        return responses;
    }

    private void updateItemTotals(
            DoctorMealRegistrationItem item,
            List<DoctorMealRegistrationItemSnapshot> snapshots
    ) {
        int mealQuantity = 0;
        BigDecimal mealAmount = BigDecimal.ZERO;
        for (DoctorMealRegistrationItemSnapshot snapshot : snapshots) {
            mealQuantity += nonNegativeOrZero(snapshot.getQuantity());
            mealAmount = mealAmount.add(nonNullAmount(snapshot.getLineTotal()));
        }
        item.setMealQuantity(mealQuantity);
        item.setMealAmount(mealAmount);
    }

    private void updateRegistrationTotals(DoctorMealRegistration registration) {
        int totalQuantity = 0;
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (DoctorMealRegistrationItem item : registrationItemRepository
                .findByRegistrationIdOrderByIdAsc(registration.getId())) {
            totalQuantity += nonNegativeOrZero(item.getMealQuantity());
            totalAmount = totalAmount.add(nonNullAmount(item.getMealAmount()));
        }
        registration.setTotalQuantity(totalQuantity);
        registration.setTotalAmount(totalAmount);
    }

    private void syncRegistrationPayloadFromCurrentItems(DoctorMealRegistration registration) {
        JsonNode existingPayload = readTree(registration.getPayloadJson());
        ObjectNode root = existingPayload.isObject()
                ? ((ObjectNode) existingPayload).deepCopy()
                : objectMapper.createObjectNode();

        ObjectNode summary = root.withObject("/summary");
        summary.put("totalQuantity", registration.getTotalQuantity());
        summary.put("totalAmount", registration.getTotalAmount());

        ArrayNode items = objectMapper.createArrayNode();
        for (DoctorMealRegistrationItem item : registrationItemRepository
                .findByRegistrationIdOrderByIdAsc(registration.getId())) {
            ObjectNode itemNode = objectMapper.createObjectNode();
            itemNode.put("id", item.getId());
            itemNode.put("date", item.getDate().toString());
            itemNode.put("dayOfWeek", item.getDayOfWeek());
            itemNode.put("mealId", item.getMealId());
            itemNode.put("mealLabel", item.getMealLabel());
            itemNode.put("mealQuantity", item.getMealQuantity());
            itemNode.put("mealAmount", item.getMealAmount());

            ArrayNode snapshots = objectMapper.createArrayNode();
            for (DoctorMealRegistrationItemSnapshot snapshot : registrationItemSnapshotRepository
                    .findByRegistrationItemIdOrderByIdAsc(item.getId())) {
                ObjectNode snapshotNode = objectMapper.createObjectNode();
                snapshotNode.put("id", snapshot.getId());
                snapshotNode.put("name", snapshot.getName());
                snapshotNode.put("servingTime", snapshot.getServingTime());
                snapshotNode.put("quantity", snapshot.getQuantity());
                snapshotNode.put("unitPrice", snapshot.getUnitPrice());
                snapshotNode.put("lineTotal", snapshot.getLineTotal());
                snapshots.add(snapshotNode);
            }
            itemNode.set("mealSnapshots", snapshots);
            items.add(itemNode);
        }
        root.set("items", items);
        registration.setPayloadJson(root.toString());
    }

    private void ensureDishDateIsEditable(LocalDate date, String message) {
        LocalDate today = LocalDate.now(BUSINESS_ZONE);
        if (date.isBefore(today)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }

    private JsonNode readTree(String json) {
        try {
            return objectMapper.readTree(json);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Stored payload is invalid JSON");
        }
    }

    @SuppressWarnings("unchecked")
    private void mergeMenuAndSelectionsFromPayload(
            JsonNode root,
            Map<String, Object> menuByDayMeal,
            Map<String, Map<String, Integer>> selections
    ) {
        JsonNode itemsNode = root.path("items");
        if (!itemsNode.isArray()) {
            return;
        }

        for (JsonNode item : itemsNode) {
            String dayOfWeek = item.path("dayOfWeek").asText("");
            String mealId = item.path("mealId").asText("");
            if (dayOfWeek.isEmpty() || mealId.isEmpty()) {
                continue;
            }

            Map<String, Object> dayMap = (Map<String, Object>) menuByDayMeal.computeIfAbsent(dayOfWeek, k -> new LinkedHashMap<>());
            List<Map<String, Object>> dishList = (List<Map<String, Object>>) dayMap.computeIfAbsent(mealId, k -> new ArrayList<>());

            String cellKey = dayOfWeek + "-" + mealId;
            Map<String, Integer> cellSelections = selections.computeIfAbsent(cellKey, k -> new LinkedHashMap<>());

            Set<String> seenDishIds = new LinkedHashSet<>();
            JsonNode dishesNode = item.path("dishes");
            if (dishesNode.isArray()) {
                for (JsonNode dish : dishesNode) {
                    String dishId = dish.path("dishId").asText("");
                    if (dishId.isEmpty()) {
                        continue;
                    }
                    int quantity = dish.path("quantity").asInt(0);
                    cellSelections.put(dishId, quantity);

                    if (!seenDishIds.contains(dishId)) {
                        seenDishIds.add(dishId);
                        Map<String, Object> dishObj = new LinkedHashMap<>();
                        dishObj.put("id", dishId);
                        dishObj.put("name", dish.path("name").asText(""));
                        dishObj.put("price", dish.path("unitPrice").asDouble(0));
                        String servingTime = dish.path("servingTime").asText("");
                        dishObj.put("servingTime", servingTime == null ? "" : servingTime);
                        dishList.add(dishObj);
                    }
                }
            }
        }
    }

    private Map<String, Object> emptyMenuByDayMeal() {
        Map<String, Object> out = new LinkedHashMap<>();
        for (String day : WEEK_DAYS) {
            Map<String, Object> meals = new LinkedHashMap<>();
            for (String mealId : MEAL_IDS) {
                meals.put(mealId, new ArrayList<Map<String, Object>>());
            }
            out.put(day, meals);
        }
        return out;
    }

    private Map<String, Object> emptyMeals() {
        Map<String, Object> meals = new LinkedHashMap<>();
        for (String mealId : MEAL_IDS) {
            meals.put(mealId, new ArrayList<Map<String, Object>>());
        }
        return meals;
    }

    @SuppressWarnings("unchecked")
    private void mergeSavedDishes(int weekYear, int weekNumber, Map<String, Object> menuByDayMeal) {
        for (DoctorMealDish dish : dishRepository.findByWeekYearAndWeekNumberOrderByDateAscMealIdAscIdAsc(weekYear, weekNumber)) {
            Map<String, Object> dayMap = (Map<String, Object>) menuByDayMeal
                    .computeIfAbsent(dish.getDayOfWeek(), key -> new LinkedHashMap<>());
            List<Map<String, Object>> dishList = (List<Map<String, Object>>) dayMap
                    .computeIfAbsent(dish.getMealId(), key -> new ArrayList<>());

            Map<String, Object> dishObj = new LinkedHashMap<>();
            dishObj.put("id", dish.getId());
            dishObj.put("name", dish.getName());
            dishObj.put("price", dish.getPrice());
            dishObj.put("unitPrice", dish.getUnitPrice());
            dishObj.put("calories", dish.getCalories());
            dishObj.put("servingTime", dish.getServingTime());
            dishObj.put("note", dish.getNote());
            dishObj.put("date", dish.getDate().toString());
            dishObj.put("canDelete", canModifyDish(dish));
            dishList.add(dishObj);
        }
    }

    private String normalizeMealLabel(String mealLabel, String mealId) {
        String normalizedMealLabel = blankToNull(mealLabel);
        if (normalizedMealLabel != null) {
            return normalizedMealLabel;
        }
        return MEAL_LABELS.get(mealId);
    }

    private BigDecimal resolveNonNegativeAmount(BigDecimal preferred, BigDecimal fallback) {
        BigDecimal value = preferred != null ? preferred : fallback;
        if (value == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Each dish must have price or unitPrice");
        }
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dish price and unitPrice must be greater than or equal to 0");
        }
        return value;
    }

    private int requirePositiveInt(JsonNode node, String fieldName) {
        if (node == null || node.isNull() || !node.isIntegralNumber() || !node.canConvertToInt()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + " must be a positive integer");
        }
        int value = node.asInt();
        if (value <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + " must be a positive integer");
        }
        return value;
    }

    private void ensureDishCanBeDeleted(DoctorMealDish dish) {
        if (!canModifyDish(dish)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete dishes from a past time");
        }
    }

    private boolean canModifyDish(DoctorMealDish dish) {
        LocalDate today = LocalDate.now(BUSINESS_ZONE);
        LocalDate date = dish.getDate();
        if (date.isBefore(today)) {
            return false;
        }
        if (date.isAfter(today)) {
            return true;
        }
        LocalTime servingTime = parseServingTime(dish.getServingTime());
        return servingTime == null || servingTime.isAfter(LocalTime.now(BUSINESS_ZONE));
    }

    private LocalTime parseServingTime(String value) {
        String normalized = blankToNull(value);
        if (normalized == null) {
            return null;
        }
        for (DateTimeFormatter formatter : SERVING_TIME_FORMATTERS) {
            try {
                return LocalTime.parse(normalized, formatter);
            } catch (DateTimeParseException ignored) {
                // Try the next supported serving time format.
            }
        }
        return null;
    }

    private String normalizeRequired(String value) {
        String normalized = blankToNull(value);
        if (normalized == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Required value must not be blank");
        }
        return normalized;
    }

    private String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
