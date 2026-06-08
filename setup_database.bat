@echo off
title MySQL Database Setup
echo ===================================================
echo           MySQL Database Schema Setup
echo ===================================================
echo.

:: Check for MySQL command line client
where mysql >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] mysql Command Line client was not found in your PATH.
    echo Please make sure MySQL Server 8.0+ is installed and mysql is added to your PATH.
    echo.
    echo If MySQL is installed, you can also import the schema manually using:
    echo MySQL Workbench, phpMyAdmin, or any SQL tool of your choice.
    echo.
    echo Schema Path: lost-found-system/src/main/resources/db/schema.sql
    echo.
    pause
    exit /b 1
)

echo [INFO] Importing database schema into MySQL...
echo [INFO] You may be prompted to enter your MySQL root password.
echo.

mysql -u root -p < lost-found-system/src/main/resources/db/schema.sql

if %errorlevel% eq 0 (
    echo.
    echo [SUCCESS] Database 'campus_lost_found' and all tables/views/procedures created successfully!
) else (
    echo.
    echo [ERROR] Failed to import database schema. Please verify your password and connection.
)

pause
