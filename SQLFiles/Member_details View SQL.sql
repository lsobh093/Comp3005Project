CREATE OR REPLACE VIEW MemberDetails AS
SELECT 
    mr.memberID,
    mr.first_name,
    mr.last_name,
    mr.date_of_birth,
    mr.gender,
    mr.email,
    mr.phone_number,
    mr.emergency_contact_name,
    mr.emergency_contact_number,
    fg.goalName, -- Display goalName instead of goalID
    mhm.date_recorded AS health_metrics_recorded_date,
    mhm.weight,
    mhm.height,
    mhm.body_fat_percentage,
    mhm.resting_heart_rate,
    mhm.blood_pressure
FROM 
    MemberRegistration mr
LEFT JOIN 
    UserGoal ug ON mr.memberID = ug.memberID
LEFT JOIN 
    MemberHealthMetrics mhm ON mr.memberID = mhm.memberID
LEFT JOIN 
    FitnessGoal fg ON ug.goalID = fg.goalID;