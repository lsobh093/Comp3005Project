CREATE OR REPLACE VIEW TrainerCalendar AS
SELECT 
	ta.trainerID,
    st.description AS sessionType, -- Display session type description (private or class)
    ta.date, 
    ta.start_time, 
    ta.end_time, 
    ta.status 
FROM 
    TrainerAvailability ta
JOIN 
    SessionType st ON ta.sessionType = st.sessionType_id
ORDER BY 
    ta.date, ta.start_time;