DROP TABLE IF EXISTS UserType CASCADE;
DROP TABLE IF EXISTS AdminStaff CASCADE;
DROP TABLE IF EXISTS AdminUserAccounts CASCADE;
DROP TABLE IF EXISTS Trainers CASCADE;
DROP TABLE IF EXISTS TrainerUserAccounts CASCADE;
DROP TABLE IF EXISTS MemberRegistration CASCADE;
DROP TABLE IF EXISTS MemberUserAccounts CASCADE;
DROP TABLE IF EXISTS MemberLoginHistory CASCADE;
DROP TABLE IF EXISTS Subscriptions CASCADE;
DROP TABLE IF EXISTS InvoicesMemberShip CASCADE;
DROP TABLE IF EXISTS SubscribesTo CASCADE;
DROP TABLE IF EXISTS ProcessMemberShipPayment CASCADE;
DROP TABLE IF EXISTS FitnessGoal CASCADE;
DROP TABLE IF EXISTS UserGoal CASCADE;
DROP TABLE IF EXISTS MemberHealthMetrics CASCADE;
DROP TABLE IF EXISTS HealthStatistics CASCADE;
DROP TABLE IF EXISTS UserMeals CASCADE;
DROP TABLE IF EXISTS AllWorkouts CASCADE;
DROP TABLE IF EXISTS UserWorkouts CASCADE;
DROP TABLE IF EXISTS Achievements CASCADE;
DROP TABLE IF EXISTS SessionType CASCADE;
DROP TABLE IF EXISTS TrainerAvailability CASCADE;
DROP TABLE IF EXISTS Room CASCADE;
DROP TABLE IF EXISTS Classes CASCADE;
DROP TABLE IF EXISTS ClassEnrollment CASCADE;
DROP TABLE IF EXISTS InvoicesTrainerSession CASCADE;
DROP TABLE IF EXISTS MemberPurchaseTrainerSession CASCADE;
DROP TABLE IF EXISTS Equipment CASCADE;
DROP TABLE IF EXISTS EquipmentMaintenance CASCADE;

DROP INDEX IF EXISTS idx_member_name CASCADE;
DROP INDEX IF EXISTS idx_username CASCADE;

-- Summary: Stores different types of users along with descriptions (e.g., member, admin staff, trainer)
CREATE TABLE UserType(
	usersTypeID INTEGER PRIMARY KEY,
	userDescription VARCHAR(50) NOT NULL -- member, admin staff, trainer
);

-- Summary: Stores information about administrative staff members including their personal details and user type
CREATE TABLE AdminStaff(
	adminID INTEGER PRIMARY KEY, -- characterised by 1000
	usersTypeID INTEGER NOT NULL,
	admin_fName VARCHAR (50) NOT NULL,
	admin_lName VARCHAR (50) NOT NULL,
	adminEmail VARCHAR (100) NOT NULL UNIQUE,
	FOREIGN KEY (usersTypeID) REFERENCES UserType(usersTypeID)
);

-- Summary: Stores login credentials for administrative staff members to access the system
CREATE TABLE AdminUserAccounts (
    no_admin SERIAL PRIMARY KEY,
	adminID INTEGER NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    pswd VARCHAR(255) NOT NULL,
    FOREIGN KEY (adminID) REFERENCES AdminStaff(adminID)
);

-- Summary: Stores information about trainers including their personal details and user type
CREATE TABLE Trainers (
    trainerID INTEGER PRIMARY KEY, -- characterised by 2000
    usersTypeID INTEGER NOT NULL,
    trainer_fName VARCHAR(50) NOT NULL,
	trainer_lName VARCHAR (50) NOT NULL,
    trainerEmail VARCHAR(100) UNIQUE NOT NULL,
    FOREIGN KEY (usersTypeID) REFERENCES UserType(usersTypeID)
);

-- Summary: Stores login credentials for trainers to access the system
CREATE TABLE TrainerUserAccounts (
    no_trainers SERIAL PRIMARY KEY,
	trainerID INTEGER NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    pswd VARCHAR(255) NOT NULL,
    FOREIGN KEY (trainerID) REFERENCES Trainers(trainerID)
);

-- Summary: Stores information about registered members including personal details and user type
CREATE TABLE MemberRegistration (
    memberID INTEGER PRIMARY KEY, -- characterised by 3000
	usersTypeID INTEGER NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    date_of_birth DATE,
    gender VARCHAR(10),
    address VARCHAR(100),
    email VARCHAR(100) UNIQUE NOT NULL,
    phone_number VARCHAR(20),
    emergency_contact_name VARCHAR(100),
    emergency_contact_number VARCHAR(20),
	FOREIGN KEY (usersTypeID) REFERENCES UserType(usersTypeID)
);

-- Summary: Index to optimize searches based on member first_name
CREATE INDEX idx_member_name ON MemberRegistration (first_name);

-- Summary: Stores login credentials for members to access the system
CREATE TABLE MemberUserAccounts (
    no_members SERIAL PRIMARY KEY,
	memberID INTEGER NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    pswd VARCHAR(255) NOT NULL,
    FOREIGN KEY (memberID) REFERENCES MemberRegistration(memberID)
);

-- Summary: Index to optimize searches based on member usernames
CREATE INDEX idx_username ON MemberUserAccounts (username);

-- Summary: Records the login history of members, including login timestamps
CREATE TABLE MemberLoginHistory (
    loginID SERIAL PRIMARY KEY,
    memberID INTEGER NOT NULL,
    loginDate TIMESTAMP NOT NULL,
    FOREIGN KEY (memberID) REFERENCES MemberRegistration(memberID)
);

-- Summary: Stores details of available subscription plans and their prices
CREATE TABLE Subscriptions (
    subscription_id INTEGER PRIMARY KEY,
    subscription_type VARCHAR(50) NOT NULL, -- basic, premium
    price DECIMAL(10, 2) NOT NULL
);

-- Summary: Stores invoices generated for membership subscriptions
CREATE TABLE InvoicesMemberShip (
    invoice_id SERIAL PRIMARY KEY,
    amount DECIMAL(10, 2) NOT NULL,
    date_created DATE NOT NULL
);

-- Summary: Records the payment processing details for membership invoices
CREATE TABLE ProcessMemberShipPayment(
	paymentProcessed SERIAL PRIMARY KEY,
	invoice_id INTEGER,
	processedBy INTEGER,
	date_processed DATE,
	FOREIGN KEY (processedBy) REFERENCES AdminStaff (adminID),
	FOREIGN KEY (invoice_id) REFERENCES InvoicesMembership(invoice_id)
);

-- Summary: Tracks membership subscriptions, payment status, and validity dates for members
CREATE TABLE SubscribesTo (
    memberID INTEGER NOT NULL,
    subscription_id INTEGER NOT NULL,
    invoice_id INTEGER NOT NULL,
	paid BOOLEAN DEFAULT FALSE,
    payment_date DATE,
	end_date DATE,
	active_subscription BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (memberID) REFERENCES MemberRegistration(memberID),
    FOREIGN KEY (subscription_id) REFERENCES Subscriptions(subscription_id),
    FOREIGN KEY (invoice_id) REFERENCES InvoicesMembership(invoice_id)
);

-- Summary: Stores fitness goals with their unique identifiers and net calorie targets
CREATE TABLE FitnessGoal (
        goalID INTEGER PRIMARY KEY,
        goalName VARCHAR(255) NOT NULL UNIQUE,
        netCaloriesGoal INTEGER NOT NULL -- Net calorie in for a week
);

-- Summary: Associates members with their fitness goals and tracks goal updates
CREATE TABLE UserGoal (
		usrgoal SERIAL PRIMARY KEY,
        memberID INTEGER,
        goalID INTEGER,
		updated_Date DATE,
		end_date DATE,
        FOREIGN KEY (memberID) REFERENCES MemberRegistration(memberID),
        FOREIGN KEY (goalID) REFERENCES FitnessGoal (goalID)
);

-- Summary: Records health metrics for members, such as weight, height, body fat percentage, etc.
CREATE TABLE MemberHealthMetrics (
    statistic_id SERIAL PRIMARY KEY,
    memberID INTEGER,
    date_recorded DATE NOT NULL,
    weight DECIMAL(5,2), -- in kilograms
    height DECIMAL(5,2), -- in centimeters
    body_fat_percentage DECIMAL(5,2),
    resting_heart_rate INTEGER, -- in beats per minute
    blood_pressure INTEGER, -- in mmHg
	FOREIGN KEY (memberID) REFERENCES MemberRegistration(memberID)
);

-- Summary: Records health statistics such as weight, body fat percentage, heart rate, and blood pressure for members over time
CREATE TABLE HealthStatistics (
    metric_id SERIAL PRIMARY KEY,
    memberID INTEGER,
    metric_date DATE NOT NULL,
    average_weight DECIMAL(5,2), -- Calculated average weight over a period
    average_body_fat_percentage DECIMAL(5,2), -- Calculated average body fat percentage over a period
    average_resting_heartRate DECIMAL(5,2),
    average_blood_pressure DECIMAL(5,2), -- Calculated average blood pressure over a period
	FOREIGN KEY (memberID) REFERENCES MemberRegistration(memberID)
);

-- Summary: Records meals consumed by members along with their calorie intake
CREATE TABLE UserMeals (
    mealID SERIAL PRIMARY KEY,
    memberID INTEGER,
    mealDate DATE NOT NULL,
    mealDescription VARCHAR(255) NOT NULL,
    calories INTEGER NOT NULL,
    FOREIGN KEY (memberID) REFERENCES MemberRegistration(memberID)
);

-- Summary: Stores various types of workouts and their corresponding calorie burning rates
CREATE TABLE AllWorkouts (
    workoutID INTEGER PRIMARY KEY,
    workoutName VARCHAR(255) NOT NULL UNIQUE,
    caloriesBurnedPerMinute DECIMAL(5,2) NOT NULL
);

-- Summary: Records workouts performed by members including start and end times, duration, and calories burned
CREATE TABLE UserWorkouts (
    memberID INTEGER,
    workoutID INTEGER,
    startTime TIMESTAMP NOT NULL,
    endTime TIMESTAMP NOT NULL,
    duration INTEGER, -- Duration in minutes (EndTime - StartTime)
    caloriesBurned DECIMAL (10,2), -- AvgCaloriesBurnedPerMinute * Duration
	weight INTEGER, -- weight carried if applicable
	numberofSets INTEGER, -- number of sets if applicable
    FOREIGN KEY (memberID) REFERENCES MemberRegistration(memberID),
    FOREIGN KEY (workoutID) REFERENCES AllWorkouts (workoutID)
);

-- Summary: Tracks members' achievements in reaching their calorie goals
CREATE TABLE Achievements (
	achieve_ID SERIAL PRIMARY KEY,
	memberID INTEGER,
	caloriesGoal TEXT NOT NULL, -- reached or not reached. calculated from weekly goal calorie. sum of total meal and burned by exercise and compared to goal.
	dateUpdated date,	
	FOREIGN KEY (memberID) REFERENCES MemberRegistration(memberID)
);

-- Summary: Defines types of training sessions, such as private or class sessions
CREATE TABLE SessionType (
    sessionType_id INTEGER PRIMARY KEY,
    description VARCHAR(20) UNIQUE
);

-- Summary: Records the availability of trainers for different types of training sessions
CREATE TABLE TrainerAvailability (
    availabilityID SERIAL PRIMARY KEY,
	trainerID INTEGER NOT NULL,
    sessionType INTEGER NOT NULL,
    date DATE,
    start_time TIME,
    end_time TIME,
	status VARCHAR(20) DEFAULT 'Available',
    FOREIGN KEY (sessionType) REFERENCES SessionType(sessionType_id),
	FOREIGN KEY (trainerID) REFERENCES Trainers (trainerID)
);

-- Summary: Stores information about available rooms for conducting training sessions
CREATE TABLE Room (
	roomID INTEGER PRIMARY KEY,
	room_name VARCHAR(20),
	capacity INTEGER	
);

-- Summary: Manages the booking of rooms for training classes along with enrollment details
CREATE TABLE Classes (
    class_id SERIAL PRIMARY KEY,
    room_id INTEGER,
    assigned_by_id INTEGER NOT NULL, -- Staff admin who made the booking room decision
	availabilityID INTEGER NOT NULL, -- staff assigns the rooms to the availabilityID,
	no_members_enrolled INTEGER, 
	status VARCHAR(50) DEFAULT 'Open', -- available or full
    FOREIGN KEY (room_id) REFERENCES Room(roomID),
    FOREIGN KEY (assigned_by_id) REFERENCES AdminStaff(adminID),
	FOREIGN KEY (availabilityID) REFERENCES TrainerAvailability(availabilityID)
);

-- Summary: Tracks class enrollments for members and their enrollment status
CREATE TABLE ClassEnrollment (
    class_enrol SERIAL PRIMARY KEY,
	availabilityID INTEGER NOT NULL,
    memberID INTEGER NOT NULL,
	enrollment_date DATE,
	cancellation_date DATE,
	status VARCHAR(20),
    UNIQUE (availabilityID, memberID), -- Ensure a member can enroll in only one session at a time
    FOREIGN KEY (availabilityID) REFERENCES TrainerAvailability(availabilityID),
    FOREIGN KEY (memberID) REFERENCES MemberRegistration(memberID)
);

-- Summary: Stores invoices generated for trainer sessions and tracks payment status
CREATE TABLE InvoicesTrainerSession (
    invoice_id SERIAL PRIMARY KEY,
    processedby INT, --Staff that proccessed the invoice
	amount DECIMAL(10, 2) DEFAULT 50.00,
    paid BOOLEAN DEFAULT FALSE,
    payment_date DATE,
	processed_date DATE,
	FOREIGN KEY (processedby) REFERENCES AdminStaff(adminID)
);

-- Summary: Records purchases made by members for trainer sessions, including enrollment details and invoice information
CREATE TABLE MemberPurchaseTrainerSession(
	memberID INTEGER NOT NULL,
	availabilityID INTEGER NOT NULL,
	invoice_id INTEGER,
	enrollment_date DATE NOT NULL,
	FOREIGN KEY (memberID) REFERENCES MemberRegistration(memberID),
    FOREIGN KEY (availabilityID) REFERENCES TrainerAvailability(availabilityID),
    FOREIGN KEY (invoice_id) REFERENCES InvoicesTrainerSession(invoice_id)
);

-- Summary: Stores information about gym equipment available for use
CREATE TABLE Equipment (
	equipID INTEGER PRIMARY KEY,
	equipName VARCHAR (60) NOT NULL UNIQUE	
);

-- Summary: Tracks maintenance activities for gym equipment, including costs and descriptions
CREATE TABLE EquipmentMaintenance (
    maintenanceID SERIAL PRIMARY KEY,
    equipID INTEGER NOT NULL,
	adminID INTEGER NOT NULL, -- maintained by staff member _
    maintenance_date DATE NOT NULL,
    maintenance_type VARCHAR(100) NOT NULL,
    maintenance_description TEXT,
    maintenance_cost DECIMAL(10, 2),
    FOREIGN KEY (equipID) REFERENCES Equipment(equipID),
	FOREIGN KEY (adminID) REFERENCES AdminStaff(adminID)
);