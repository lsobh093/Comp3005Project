-- Insert data into UserType table
INSERT INTO UserType (usersTypeID, userDescription) VALUES
    (1, 'Admin Staff'),
    (2, 'Trainer'),
    (3, 'Member');

-- Insert data into AdminStaff table
INSERT INTO AdminStaff (adminID, usersTypeID, admin_fName, admin_lName, adminEmail) VALUES
    (1000, 1, 'Joe', 'Staff', 'admin1@example.com'),
	(1001, 1, 'Mo', 'Staff', 'admin2@example.com'),
	(1002, 1, 'Kay', 'Staff', 'admin3@example.com');

-- Insert data into AdminUserAccounts table
INSERT INTO AdminUserAccounts (adminID, username, pswd) VALUES
    (1000, 'admin1', 'password1'),
	(1001, 'admin2', 'password2'),
	(1002, 'admin3', 'password3');

-- Insert data into Trainers table
INSERT INTO Trainers (trainerID, usersTypeID, trainer_fName, trainer_lName, trainerEmail) VALUES
    (2000, 2, 'John', 'Doe', 'john.doe@example.com'),
	(2001, 2, 'Emily', 'Smith', 'emily.smith@example.com'),
    (2002, 2, 'Michael', 'Johnson', 'michael.johnson@example.com');

-- Insert data into TrainerUserAccounts table
INSERT INTO TrainerUserAccounts (trainerID, username, pswd) VALUES
    (2000, 'trainer', 'trainerpassword'),
	(2001, 'emily', 'trainerpassword1'),
    (2002, 'michael', 'trainerpassword2');

-- Insert data into MemberRegistration table
INSERT INTO MemberRegistration (memberID, usersTypeID, first_name, last_name, date_of_birth, gender, address, email, phone_number, emergency_contact_name, emergency_contact_number) VALUES
    (3000, 3, 'Alice', 'Johnson', '1990-05-15', 'Female', '123 Main St, City', 'alice.johnson@example.com', '123-456-7890', 'Bob Johnson', '987-654-3210'),
	(3001, 3, 'Ali', 'Jnson', '1990-05-15', 'Female', '123 Main St, City', 'alice.on@example.com', '123-436-7890', 'Bob J43hnson', '987-654-3210');

-- Insert data into MemberUserAccounts table
INSERT INTO MemberUserAccounts (memberID, username, pswd) VALUES
    (3000, 'alice', 'memberpassword'),
	(3001, 'mo', 'salah');

-- Insert data into MemberLoginHistory table
INSERT INTO MemberLoginHistory (memberID, loginDate) VALUES
    (3000, '2024-04-09 08:00:00'); -- Example login for member with ID 3000

-- Insert data into Subscriptions table
INSERT INTO Subscriptions (subscription_id, subscription_type, price) VALUES
(1, 'Basic', 50.00),
(2, 'Premium', 100.00);

-- Insert sample data into InvoicesMemberShip table
INSERT INTO InvoicesMemberShip (amount, date_created) VALUES
(50.00, '2024-04-01'),
(50.00, '2024-04-01'),
(50.00, '2024-05-01');

-- Insert sample data into ProcessMemberShipPayment table
INSERT INTO ProcessMemberShipPayment (invoice_id, processedBy, date_processed) VALUES
(1, NULL, NULL),
(2, NULL, NULL),
(3, 1001, '2024-05-23');

-- Insert sample data into SubscribesTo table
INSERT INTO SubscribesTo (memberID, subscription_id, invoice_id, paid, payment_date, end_date, active_subscription) VALUES
(3000, 1, 1, TRUE, '2024-04-03', '2025-05-02', TRUE);

-- Insert data into FitnessGoal table. Calories for the week.
INSERT INTO FitnessGoal (goalID, goalName, netCaloriesGoal) VALUES
    (1, 'Weight Loss', -3000),
    (2, 'Muscle Gain', 5000),
    (3, 'Maintenance', 0);

-- Insert data into UserGoal table
INSERT INTO UserGoal (memberID, goalID, updated_Date, end_date) VALUES
    (3000, 1, '2024-04-04', '2024-04-11');
	--(3000, 2, '2024-05-01', '2024-05-09');
	
-- MemberHealthMetrics: the data for this table is randomly generated and inserted by the application
--Example insertion:
INSERT INTO MemberHealthMetrics (memberID, date_recorded, weight, height, body_fat_percentage, resting_heart_rate, blood_pressure) VALUES
(3000, '2024-04-20', 75.5, 175, 20.5, 70, 120);

--HealthStatistics: the data for this table is calculated from the above table (MemberHealhMetrics) by the trigger function (calculatehealthstatistics())
--Example insertion:
--INSERT INTO HealthStatistics(memberID, metric_date, average_weight, average_body_fat_percentage, average_resting_heartRate, average_blood_pressure) VALUES
--(3000, '2024-04-20', 75.5, 20.5, 70, 120);

--Insert data into AllWorkouts table
INSERT INTO AllWorkouts (workoutID, workoutName, caloriesBurnedPerMinute) VALUES
(1, 'Running', 10.5),
(2, 'Pushups', 5.0),
(3, 'Swimming', 12.0),
(4, 'Cycling', 8.5),
(5, 'Weightlifting', 6.0),
(6, 'HIIT', 13.5),
(7, 'Walking', 5.0),
(8, 'Jump Rope', 10.0),
(9, 'Squats', 6.0),
(10, 'Deadlifts', 8.0);

--Insert data into UserWorkouts table
INSERT INTO UserWorkouts (memberID, workoutID, startTime, endTime, duration, caloriesBurned, weight, numberofSets)
VALUES 
    (3000, 1, '2024-04-04 08:00:00', '2024-04-04 08:30:00', NULL, NULL, 0, 0),
    (3000, 2, '2024-04-04 09:00:00', '2024-04-04 09:30:00', NULL, NULL, 0, 0),
    (3000, 2, '2024-04-05 10:00:00', '2024-04-05 10:20:00', NULL, NULL, 0, 0),
	(3000, 4, '2024-04-05 10:30:00', '2024-04-05 10:50:00', NULL, NULL, 0, 0),
	(3000, 5, '2024-04-06 09:00:00', '2024-04-06 10:20:00', NULL, NULL, 0, 0),
	(3000, 6, '2024-04-07 08:00:00', '2024-04-07 09:00:00', NULL, NULL, 0, 0),
	(3000, 7, '2024-04-08 08:00:00', '2024-04-08 09:00:00', NULL, NULL, 0, 0),
	(3000, 8, '2024-04-09 10:00:00', '2024-04-09 11:20:00', NULL, NULL, 0, 0),
	(3000, 9, '2024-04-09 11:00:00', '2024-04-09 11:20:00', NULL, NULL, 0, 4);
	--(3000, 10, '2024-05-08 11:00:00', '2024-05-08 11:20:00', NULL, 44, 0, 2);
	
-- Insert data into UserMeals table
INSERT INTO UserMeals (memberID, mealDate, mealDescription, calories) VALUES
(3000, '2024-04-04', 'Scrambled eggs with spinach and toast', 350),
(3000, '2024-04-04', 'Grilled chicken salad with balsamic vinaigrette', 450),
(3000, '2024-04-05', 'Baked salmon with roasted vegetables', 500),
(3000, '2024-04-06', 'Junk food', 500),
(3000, '2024-04-07', 'BBQ burgers and hot dogs', 600),
(3000, '2024-04-08', 'Baked potatoes', 200),
(3000, '2024-04-09', 'Raw vegetables', 100),
(3000, '2024-04-10', 'Baked salmon with roasted vegetables', 500),
(3000, '2024-05-08', 'batata', 100);

--Insert data into SessionType table
INSERT INTO SessionType (sessionType_id, description) VALUES
(1, 'Private'),
(2, 'Class');

--Insert data into TrainerAvailability table
INSERT INTO TrainerAvailability (trainerID, sessionType, date, start_time, end_time)
VALUES
    (2000, 1, '2024-04-12', '09:00:00', '10:00:00'),
    (2000, 1, '2024-04-13', '11:00:00', '12:00:00'),
    (2000, 2, '2024-04-14', '14:00:00', '16:00:00'),
    (2000, 1, '2024-04-15', '16:00:00', '17:00:00'),
    (2000, 1, '2024-04-16', '09:00:00', '11:00:00'),
    (2001, 1, '2024-04-12', '09:00:00', '11:00:00'),
    (2001, 2, '2024-04-13', '11:00:00', '12:00:00'),
    (2001, 1, '2024-04-14', '13:00:00', '14:00:00'),
    (2001, 2, '2024-04-15', '09:00:00', '11:00:00'),
    (2001, 1, '2024-04-16', '09:00:00', '10:00:00'),
    (2002, 2, '2024-04-12', '09:00:00', '12:00:00'),
    (2002, 1, '2024-04-13', '09:00:00', '10:00:00'),
    (2002, 2, '2024-04-14', '13:00:00', '15:00:00'),
    (2002, 1, '2024-04-15', '09:00:00', '11:00:00'),
    (2002, 1, '2024-04-16', '09:00:00', '10:00:00'),
	(2001, 2, '2024-04-14', '14:00:00', '16:00:00'),
	(2001, 2, '2024-04-20', '14:00:00', '16:00:00'),
	(2001, 2, '2024-04-21', '14:00:00', '16:00:00');

-- Insert data into Room table 
INSERT INTO Room (roomID, room_name, capacity) VALUES
(101, 'Room A', 5),
(102, 'Room B', 5),
(103, 'Room C', 5),
(104, 'Room D', 10),
(105, 'Room E', 20);

--Insert data into Classes table
INSERT INTO Classes (room_id, assigned_by_id, availabilityID, no_members_enrolled) VALUES
(101, 1000, 3, 2),
(102, 1001, 7, Null),
(101, 1000, 9, NULL),
(104, 1002, 11, NULL),
(103, 1000, 13, NULL);

--Insert data into ClassEnrollment table
INSERT INTO ClassEnrollment (availabilityID, memberID, enrollment_date, cancellation_date, status) VALUES
(3, 3000, '2024-04-12', NULL, 'Booked'), -- Member 1 enrolled in availabilityID 1
(3, 3001, '2024-04-12', NULL, 'Booked'); -- Member 2 enrolled in availabilityID 1

--Insert data into InvoicesTrainerSession
INSERT INTO InvoicesTrainerSession (processedby, amount, paid, payment_date, processed_date) VALUES 
(1000, 50.00, TRUE, '2024-04-14', NULL);

-- Insert data into MemberPurchaseTrainerSession
INSERT INTO MemberPurchaseTrainerSession (memberID, availabilityID, invoice_id, enrollment_date)VALUES
(3000, 1, 1, '2024-03-10'), -- Member 1 purchased availabilityID 1 on 2024-03-10 with invoice 1
(3000, 2, NULL, '2024-03-12'), -- Member 2 purchased availabilityID 3 on 2024-03-12 with invoice 2
(3001, 4, NULL, '2024-03-22'),
(3001, 5, NULL, '2024-03-22'),
(3000, 7, NULL, '2024-03-22');

-- Insert data into Equipment table
INSERT INTO Equipment (equipID, equipName) VALUES
(1, 'Treadmill'),
(2, 'Dumbbells'),
(3, 'Elliptical Trainer'),
(4, 'Barbell'),
(5, 'Stationary Bike'),
(6, 'Bench Press'),
(7, 'Rowing Machine'),
(8, 'Kettlebell'),
(9, 'Pull-Up Bar'),
(10, 'Jump Rope');

-- Insert data into EquipmentMaintenance table
INSERT INTO EquipmentMaintenance (equipID, adminID, maintenance_date, maintenance_type, maintenance_description, maintenance_cost) VALUES
(1, 1000, '2024-03-15', 'Routine Maintenance', 'Cleaned and lubricated treadmill belt', 50.00),
(2, 1001, '2024-03-15', 'Repair', 'Replaced worn-out rubber grips on dumbbells', 30.00),
(3, 1002, '2024-03-15', 'Repair', 'Adjusted resistance levels on elliptical trainer', 40.00);