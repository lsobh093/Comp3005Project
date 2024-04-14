CREATE OR REPLACE FUNCTION check_trainer_conflict() RETURNS TRIGGER AS $$
BEGIN
    -- Check if there's a conflicting session for the trainer
    IF EXISTS (
        SELECT 1
        FROM TrainerAvailability AS ta
        WHERE ta.trainerID = NEW.trainerID
        AND ta.date = NEW.date
        AND (
            (NEW.start_time, NEW.end_time) OVERLAPS (ta.start_time, ta.end_time)
        )
    ) THEN
        RAISE EXCEPTION 'Trainer cannot book sessions at the same time';
    END IF;

    -- Check if there's already a session of a different type at the same time
    IF EXISTS (
        SELECT 1
        FROM TrainerAvailability AS ta
        WHERE ta.trainerID = NEW.trainerID
        AND ta.date = NEW.date
        AND ta.sessionType <> NEW.sessionType
        AND (
            (NEW.start_time, NEW.end_time) OVERLAPS (ta.start_time, ta.end_time)
        )
    ) THEN
        RAISE EXCEPTION 'Trainer cannot hold private and class sessions at the same time';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER prevent_trainer_conflict
BEFORE INSERT OR UPDATE ON TrainerAvailability
FOR EACH ROW
EXECUTE FUNCTION check_trainer_conflict();