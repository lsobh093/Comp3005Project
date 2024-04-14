CREATE VIEW ClassAvailability AS
SELECT 
    ta.availabilityID,
    ta.trainerID,
    tr.trainer_fName,
    tr.trainer_lName,
    ta.date,
    ta.start_time,
    ta.end_time,
    ta.status,
    c.room_id
FROM 
    TrainerAvailability ta
JOIN 
    Trainers tr ON ta.trainerID = tr.trainerID
LEFT JOIN 
    Classes c ON ta.availabilityID = c.availabilityID
WHERE 
    ta.sessionType = (SELECT sessionType_id FROM SessionType WHERE description = 'Class');