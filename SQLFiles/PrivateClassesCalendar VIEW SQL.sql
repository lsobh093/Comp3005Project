CREATE OR REPLACE VIEW PrivateClassesCalendar AS
SELECT ta.availabilityID, trns.trainer_fName, trns.trainer_lName, ta.status, ta.date, ta.start_time, ta.end_time
FROM TrainerAvailability ta
JOIN SessionType st ON ta.sessionType = st.sessionType_id
JOIN Trainers trns ON ta.trainerID = trns.trainerID
WHERE st.description = 'Private';