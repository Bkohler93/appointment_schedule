# Appointment Scheduler

This application is the Performance Assessment for C195 (Software II) at Western Governor's University. The purpose is is to enable a global consulting organization
to schedule and view appointments for their customers.

Author: Brett Kohler
Contact Information: bkohle9@wgu.edu
Date: 04/13/2023

Built using IntelliJ IDEA 2022.3.3 (Community Edition), Java Development Kit 17.0.6, and JavaFX-SDK 17.0.6.

## Running the program (using IntelliJ IDEA 2022.3.3 (Community Edition)
This project requires a MySQL database to be running on the system with user 'sqlUser' and password 'Passw0rd!'.

Click File > New Project from Version Control

Copy the Repo URL and paste it in the prompt in IntelliJ. Wait for IntelliJ to download dependencies and set up the project. Once this is complete
go to File > Project Structure > Libraries and add mysql-connector-8.0.32 and javafx 17.0.6 as project dependencies.

Next click on Add Configuration in the top toolbar. Name the configuration Main (to match the entry file of the project). Type 'Main' and select the Main class
that is part of the project (com.example.appointment_schedule).

Now launch the application by hitting the green play symbol. User 'test' and password 'test' can be used to log in to the application.
In the default database the first appointments are scheduled in 2020, and the program initializes to the current month in the schedule for appointments. 
Navigate to the default appointments by navigating with the '<' symbol next to the current Month/Year near the top of the window.

The application's login window can be viewed in French as well. Set the system default language to French (France) fr_FR
in order to view the page in French.

## Additional Report
The additional report lists the total number of appointments scheduled for a selected country.

## Dependencies
mysql-connector-8.0.32
javafx 17.0.6
