CREATE OR REPLACE FUNCTION calculateHealthStatistics()
RETURNS TRIGGER AS $$
BEGIN
    -- Calculate the average health statistics
    INSERT INTO HealthStatistics (memberID, metric_date, average_weight, average_body_fat_percentage, average_resting_heartRate, average_blood_pressure)
    SELECT NEW.memberID,
           NEW.date_recorded,
           AVG(weight) AS average_weight,
           AVG(body_fat_percentage) AS average_body_fat_percentage,
           AVG(resting_heart_rate) AS average_resting_heartRate,
           AVG(blood_pressure) AS average_blood_pressure
    FROM MemberHealthMetrics
    WHERE memberID = NEW.memberID
          AND date_recorded BETWEEN (NEW.date_recorded - INTERVAL '7 DAY') AND NEW.date_recorded; -- Calculate averages over the past 7 days
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER calculateHealthStatisticsTrigger
AFTER INSERT ON MemberHealthMetrics
FOR EACH ROW
EXECUTE FUNCTION calculateHealthStatistics();