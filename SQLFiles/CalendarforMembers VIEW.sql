CREATE OR REPLACE VIEW ClassCalendarforMembers AS
SELECT c.availabilityID, r.room_name, s.description, trns.trainer_fName, trns.trainer_lName, ta.status, ta.start_time, ta.end_time, ta.date
FROM Classes c
JOIN TrainerAvailability ta ON c.availabilityID = ta.availabilityID
JOIN Room r ON c.room_id = r.roomID
JOIN SessionType s on s.sessionType_id = ta.sessionType
JOIN Trainers trns on ta.trainerID = trns.trainerID