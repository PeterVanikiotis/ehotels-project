# eHotels Database Project

# Group Members
Peter Vanikiotis, ID: 300379232
Anthony Appa-Ross, ID: 300348726
Nilavan Athavan, ID: 300354870

# Project Structure

backend/
Contains the Java Spring Boot application

controller/
REST API endpoints (CustomerController, EmployeeController)

model/
Data Transfer Objects (DTOs) for requests and responses

repository/
Database interaction using SQL queries

BackendApplication.java
Main entry point for the Spring Boot application


resources/

static/
Frontend HTML files:
index.html – entry page
customer.html – customer interface
employee.html – employee interface

schema.sql
Database schema (tables, constraints)

populate.sql
Initial data population

queries.sql
Custom SQL queries

trigger_tests.sql
Trigger-related test scripts

application.properties
Database configuration


# Overview
This project implements a hotel booking and renting system using:
PostgreSQL for the database
Java Spring Boot for the backend
HTML/CSS/JavaScript for the frontend