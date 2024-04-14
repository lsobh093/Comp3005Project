CREATE OR REPLACE FUNCTION calculate_workout_metrics()
RETURNS TRIGGER AS $$
BEGIN
    -- Calculate duration in minutes
    NEW.duration = EXTRACT(EPOCH FROM (NEW.endTime - NEW.startTime)) / 60;
	
	-- Calculate calories burned based on exercise type
    SELECT caloriesBurnedPerMinute INTO NEW.caloriesBurned
    FROM AllWorkouts
    WHERE workoutID = NEW.workoutID;

    NEW.caloriesBurned := NEW.duration * NEW.caloriesBurned;

	RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER calculate_workout_metrics_trigger
BEFORE INSERT ON UserWorkouts
FOR EACH ROW
EXECUTE FUNCTION calculate_workout_metrics();