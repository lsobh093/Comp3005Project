CREATE VIEW PrivateSessionsViewerForTrainer AS
SELECT 
    ta.availabilityID,
    ta.trainerID,
    tr.trainer_fName,
    tr.trainer_lName,
    ta.date,
    ta.start_time,
    ta.end_time,
	m.memberID,
    m.first_name AS member_first_name,
    m.last_name AS member_last_name,
    mp.enrollment_date
FROM
    TrainerAvailability ta
JOIN 
    Trainers tr ON ta.trainerID = tr.trainerID
JOIN 
    MemberPurchaseTrainerSession mp ON ta.availabilityID = mp.availabilityID
JOIN 
    MemberRegistration m ON mp.memberID = m.memberID