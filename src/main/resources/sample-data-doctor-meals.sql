-- Insert 3 sample data records for doctor_meal_registrations
INSERT INTO doctor_meal_registrations (week_year, week_number, username, payload_json, created_at)
VALUES 
(
  2026, 20, 'doctor1', 
  '{"week":{"year":2026,"number":20},"requester":{"username":"doctor1","name":"Nguyen Van A"},"items":[{"dayOfWeek":"Thứ 2","mealId":"lunch","dishes":[{"dishId":"d1","name":"Cơm tấm sườn bì chả","unitPrice":40000,"servingTime":"11:30","quantity":1}]}],"summary":{"totalAmount":40000}}', 
  NOW()
),
(
  2026, 20, 'doctor2', 
  '{"week":{"year":2026,"number":20},"requester":{"username":"doctor2","name":"Tran Thi B"},"items":[{"dayOfWeek":"Thứ 3","mealId":"lunch","dishes":[{"dishId":"d2","name":"Phở bò tái nạm","unitPrice":45000,"servingTime":"11:30","quantity":1}]}],"summary":{"totalAmount":45000}}', 
  NOW()
),
(
  2026, 20, 'doctor3', 
  '{"week":{"year":2026,"number":20},"requester":{"username":"doctor3","name":"Le Van C"},"items":[{"dayOfWeek":"Thứ 4","mealId":"dinner","dishes":[{"dishId":"d3","name":"Bún chả Hà Nội","unitPrice":50000,"servingTime":"17:30","quantity":1}]}],"summary":{"totalAmount":50000}}', 
  NOW()
);
