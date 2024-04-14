# Comp3005Project
Welcome to my fitness management system:

Purpose:
To design and implement a fitness management system that allows members, trainers, and staff admin members to store, interact with, and retrieve data correspnding to distinct types of data. The ER model for the database and it's corresponding transformation into a database schema are provided (ER model.docx and SchemaDiagram.png). 

The following was assumed in order to design the database:
1.	UserType to AdminStaff: One-to-Many (1:N)
o	Each UserType can be associated with multiple AdminStaff members.
o	Each AdminStaff member belongs to exactly one UserType.
2.	AdminStaff to AdminUserAccounts: One-to-One (1:1)
o	Each AdminStaff member has exactly one login account.
o	Each login account belongs to exactly one AdminStaff member.
3.	UserType to Trainers: One-to-Many (1:N)
o	Each UserType can be associated with multiple Trainers.
o	Each Trainer belongs to exactly one UserType.
4.	Trainers to TrainerUserAccounts: One-to-One (1:1)
o	Each Trainer has exactly one login account.
o	Each login account belongs to exactly one Trainer.
5.	UserType to MemberRegistration: One-to-Many (1:N)
o	Each UserType can be associated with multiple members.
o	Each member belongs to exactly one UserType.
6.	MemberRegistration to MemberUserAccounts: One-to-One (1:1)
o	Each member has exactly one login account.
o	Each login account belongs to exactly one member.
7.	MemberRegistration to MemberLoginHistory: One-to-Many (1:N)
o	Each member can have multiple login history records.
o	Each login history record belongs to exactly one member.
8.	MemberRegistration to SubscribesTo: One-to-Many (1:N)
o	Each member can have multiple subscription records.
o	Each subscription record belongs to exactly one member.
9.	Subscriptions to SubscribesTo: One-to-Many (1:N)
o	Each subscription type can have multiple subscribers.
o	Each subscriber can have exactly one subscription type.
10.	MemberRegistration to UserGoal: One-to-Many (1:N)
o	Each member can have multiple fitness goals.
o	Each fitness goal belongs to exactly one member.
11.	MemberRegistration to MemberHealthMetrics: One-to-Many (1:N)
o	Each member can have multiple health metric records.
o	Each health metric record belongs to exactly one member.
12.	MemberRegistration to HealthStatistics: One-to-Many (1:N)
o	Each member can have multiple health statistic records.
o	Each health statistic record belongs to exactly one member.
13.	MemberRegistration to UserMeals: One-to-Many (1:N)
o	Each member can have multiple meal records.
o	Each meal record belongs to exactly one member.
14.	AllWorkouts to UserWorkouts: One-to-Many (1:N)
o	Each workout type can be associated with multiple workout instances.
o	Each workout instance belongs to exactly one workout type.
15.	MemberRegistration to UserWorkouts: One-to-Many (1:N)
o	Each member can have multiple workout records.
o	Each workout record belongs to exactly one member.
16.	MemberRegistration to Achievements: One-to-Many (1:N)
o	Each member can have multiple achievement records.
o	Each achievement record belongs to exactly one member.
17.	SessionType to TrainerAvailability: One-to-Many (1:N)
o	Each session type can be associated with multiple availability slots.
o	Each availability slot belongs to exactly one session type.
18.	Trainers to TrainerAvailability: One-to-Many (1:N)
o	Each trainer can have multiple availability slots.
o	Each availability slot belongs to exactly one trainer.
19.	Room to Classes: One-to-Many (1:N)
o	Each room can host multiple classes.
o	Each class is held in exactly one room.
20.	AdminStaff to Classes: One-to-Many (1:N)
o	Each staff member can assign multiple classes.
o	Each class is assigned by exactly one staff member.
21.	TrainerAvailability to Classes: One-to-Many (1:N)
o	Each availability slot can be assigned to multiple classes.
o	Each class is assigned to exactly one availability slot.
22.	TrainerAvailability to ClassEnrollment: One-to-Many (1:N)
o	Each availability slot can have multiple enrollments.
o	Each enrollment is associated with exactly one availability slot.
23.	MemberRegistration to ClassEnrollment: One-to-Many (1:N)
o	Each member can enroll in multiple classes.
o	Each enrollment is associated with exactly one member.
24.	AdminStaff to InvoicesTrainerSession: One-to-Many (1:N)
o	Each staff member can process multiple trainer session invoices.
o	Each trainer session invoice is processed by exactly one staff member.
25.	InvoicesTrainerSession to MemberPurchaseTrainerSession: One-to-Many (1:N)
o	Each trainer session invoice can be associated with multiple member purchases.
o	Each member purchase is associated with exactly one trainer session invoice.
26.	Equipment to EquipmentMaintenance: One-to-Many (1:N)
o	Each equipment can have multiple maintenance records.
o	Each maintenance record is associated with exactly one equipment.
27.	AdminStaff to EquipmentMaintenance: One-to-Many (1:N)
o	Each staff member can perform multiple maintenance activities.
o	Each maintenance activity is performed by exactly one staff member.

The files included within this github repository include:

Java files:

Main.java
UserInteraction.java
MemberFunctions.java
MemberRegistration.java
MemberDashboard.java
MemberAchievements.java
MemberExercises.java
MemberHealthFunctions.java
MemberNutrition.java
MemberPrivateRegistration.java
MemberRegistration.java
TrainerFunctions.java
AdminFunctions.java

SQL Files:
DDL File: ProjectDDL.sql
DML File: ProjectDML.sql

Created Views to enhance data viewing:
CalendarforMembers VIEW.sql
ClassAvailability VIEW.sql
Member_details VIEW SQL.sql
PrivateClassesCalendar VIEW SQL.sql
PrivateSessionsViewerForTrainer VIEW.sql
trainer_calendar VIEW SQL.sql

The functionality of each file and their purpose is explained in detail in separate files:
1. Java Files.txt
2. SQL Files.txt
