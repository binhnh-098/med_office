package com.example.med_office.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.example.med_office.entity.DoctorMealRegistration;
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

    private final DoctorMealRegistrationRepository registrationRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public DoctorMealsService(
            DoctorMealRegistrationRepository registrationRepository
    ) {
        this.registrationRepository = registrationRepository;
    }

    public Map<String, Object> getWeekData(int weekYear, int weekNumber, String username) {
        Optional<DoctorMealRegistration> latest = registrationRepository
                .findFirstByWeekYearAndWeekNumberAndUsernameOrderByIdDesc(weekYear, weekNumber, username);

        Map<String, Object> menuByDayMeal = emptyMenuByDayMeal();
        Map<String, Map<String, Integer>> selections = new LinkedHashMap<>();

        if (latest.isEmpty()) {
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("menuByDayMeal", menuByDayMeal);
            body.put("selections", selections);
            return body;
        }

        JsonNode root = readTree(latest.get().getPayloadJson());
        mergeMenuAndSelectionsFromPayload(root, menuByDayMeal, selections);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("menuByDayMeal", menuByDayMeal);
        body.put("selections", selections);
        body.put("lastRegistrationId", latest.get().getId());
        body.put("lastRegistrationAt", latest.get().getCreatedAt().toString());
        return body;
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

    @Transactional
    public void deleteRegistration(Long id, String username) {
        DoctorMealRegistration row = registrationRepository.findByIdAndUsername(id, username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Registration not found"));
        registrationRepository.delete(row);
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
}
