package com.example.med_office.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.example.med_office.dto.DoctorMealDishCreateRequest;
import com.example.med_office.dto.DoctorMealDishUpdateRequest;
import com.example.med_office.entity.DoctorMealDish;
import com.example.med_office.entity.DoctorMealRegistration;
import com.example.med_office.repository.DoctorMealDishRepository;
import com.example.med_office.repository.DoctorMealRegistrationRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    private final DoctorMealDishRepository dishRepository;
    private final DoctorMealRegistrationRepository registrationRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public DoctorMealsService(
            DoctorMealDishRepository dishRepository,
            DoctorMealRegistrationRepository registrationRepository
    ) {
        this.dishRepository = dishRepository;
        this.registrationRepository = registrationRepository;
    }

    public Map<String, Object> getWeekData(int weekYear, int weekNumber, String username) {
        Optional<DoctorMealRegistration> latest = registrationRepository
                .findFirstByWeekYearAndWeekNumberAndUsernameOrderByIdDesc(weekYear, weekNumber, username);

        Map<String, Object> menuByDayMeal = emptyMenuByDayMeal();
        Map<String, Map<String, Integer>> selections = new LinkedHashMap<>();

        if (latest.isEmpty()) {
            mergeSavedDishes(weekYear, weekNumber, menuByDayMeal);
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("menuByDayMeal", menuByDayMeal);
            body.put("selections", selections);
            return body;
        }

        JsonNode root = readTree(latest.get().getPayloadJson());
        mergeMenuAndSelectionsFromPayload(root, menuByDayMeal, selections);
        mergeSavedDishes(weekYear, weekNumber, menuByDayMeal);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("menuByDayMeal", menuByDayMeal);
        body.put("selections", selections);
        body.put("lastRegistrationId", latest.get().getId());
        body.put("lastRegistrationAt", latest.get().getCreatedAt().toString());
        return body;
    }

    public Map<String, Object> getDishesByDay(int weekYear, int weekNumber, String dayOfWeek, String username) {
        if (dayOfWeek == null || dayOfWeek.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing dayOfWeek");
        }
        String normalizedDayOfWeek = dayOfWeek.trim();

        Optional<DoctorMealRegistration> latest = registrationRepository
                .findFirstByWeekYearAndWeekNumberAndUsernameOrderByIdDesc(weekYear, weekNumber, username);

        Map<String, Object> meals = emptyMeals();
        List<Map<String, Object>> items = new ArrayList<>();
        int totalQuantity = 0;

        if (latest.isPresent()) {
            JsonNode root = readTree(latest.get().getPayloadJson());
            JsonNode itemsNode = root.path("items");
            if (itemsNode.isArray()) {
                for (JsonNode item : itemsNode) {
                    String itemDay = item.path("dayOfWeek").asText("");
                    if (!normalizedDayOfWeek.equalsIgnoreCase(itemDay.trim())) {
                        continue;
                    }

                    String mealId = item.path("mealId").asText("");
                    JsonNode dishesNode = item.path("dishes");
                    if (!dishesNode.isArray()) {
                        continue;
                    }

                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> mealDishes = (List<Map<String, Object>>) meals
                            .computeIfAbsent(mealId, k -> new ArrayList<Map<String, Object>>());

                    for (JsonNode dish : dishesNode) {
                        Map<String, Object> dishItem = toDishByDayItem(normalizedDayOfWeek, mealId, dish);
                        items.add(dishItem);
                        mealDishes.add(dishItem);
                        totalQuantity += dish.path("quantity").asInt(0);
                    }
                }
            }
        }

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
        out.put("totalQuantity", totalQuantity);
        latest.ifPresent(row -> {
            out.put("lastRegistrationId", row.getId());
            out.put("lastRegistrationAt", row.getCreatedAt().toString());
        });
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
    public Map<String, Object> createRegistration(String principalName, JsonNode body) {
        if (!body.isObject()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Body must be a JSON object");
        }
        JsonNode weekNode = body.path("week");
        if (!weekNode.isObject()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing or invalid week object");
        }
        int weekYear = weekNode.path("year").asInt();
        int weekNumber = weekNode.path("number").asInt();

        ObjectNode normalized = body.deepCopy();
        ObjectNode requester = normalized.with("requester");
        requester.put("username", principalName);

        String payloadJson = normalized.toString();

        DoctorMealRegistration entity = new DoctorMealRegistration();
        entity.setWeekYear(weekYear);
        entity.setWeekNumber(weekNumber);
        entity.setUsername(principalName);
        entity.setPayloadJson(payloadJson);

        DoctorMealRegistration saved = registrationRepository.save(entity);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", saved.getId());
        data.put("createdAt", saved.getCreatedAt().toString());
        data.put("weekYear", weekYear);
        data.put("weekNumber", weekNumber);
        return data;
    }

    @Transactional
    public Map<String, Object> updateDish(Long id, String principalName, DoctorMealDishUpdateRequest request) {
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
        List<DoctorMealRegistration> all = registrationRepository
                .findByWeekYearAndWeekNumberAndUsernameOrderByIdDesc(weekYear, weekNumber, username);

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

    public Map<String, Object> getRegistrationDetail(Long id, String username) {
        DoctorMealRegistration row = registrationRepository.findByIdAndUsername(id, username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Registration not found"));

        JsonNode root = readTree(row.getPayloadJson());
        Map<String, Object> detail = new LinkedHashMap<>();
        detail.put("id", row.getId());
        detail.put("createdAt", row.getCreatedAt().toString());

        root.fields().forEachRemaining(entry -> {
            if (!"items".equals(entry.getKey())) {
                detail.put(entry.getKey(), objectMapper.convertValue(entry.getValue(), Object.class));
            }
        });

        JsonNode itemsNode = root.path("items");
        List<Map<String, Object>> itemsWithIds = new ArrayList<>();
        int index = 1;
        if (itemsNode.isArray()) {
            for (JsonNode item : itemsNode) {
                Map<String, Object> itemMap = objectMapper.convertValue(item, Map.class);
                itemMap.putIfAbsent("id", "line-" + index);
                itemsWithIds.add(itemMap);
                index++;
            }
        }
        detail.put("items", itemsWithIds);

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
        return dishItem;
    }

    @Transactional
    public void deleteRegistration(Long id, String username) {
        DoctorMealRegistration row = registrationRepository.findByIdAndUsername(id, username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Registration not found"));
        registrationRepository.delete(row);
    }

    @Transactional
    public void deleteDish(Long id, String principalName) {
        DoctorMealDish dish = getOwnedDish(id, principalName);
        ensureDishDateIsEditable(dish.getDate(), "Cannot delete dishes from a past date");
        dishRepository.delete(dish);
    }

    private Map<String, Object> toListItem(DoctorMealRegistration row) {
        JsonNode root = readTree(row.getPayloadJson());
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", row.getId());
        item.put("createdAt", row.getCreatedAt().toString());

        JsonNode requester = root.path("requester");
        Map<String, Object> requesterMap = objectMapper.convertValue(requester, Map.class);
        item.put("requester", requesterMap != null ? requesterMap : Map.of());

        JsonNode summary = root.path("summary");
        Map<String, Object> summaryMap = objectMapper.convertValue(summary, Map.class);
        item.put("summary", summaryMap != null ? summaryMap : Map.of());

        JsonNode week = root.path("week");
        Map<String, Object> weekMap = objectMapper.convertValue(week, Map.class);
        item.put("week", weekMap != null ? weekMap : Map.of());

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

    private DoctorMealDish getOwnedDish(Long id, String principalName) {
        DoctorMealDish dish = dishRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dish not found"));
        if (!principalName.equals(dish.getCreatedBy())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot modify another user's dish");
        }
        return dish;
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
