@echo off
title Smart Campus Lost & Found Launcher
echo ===================================================
echo     Smart Campus Lost & Found Management System
echo ===================================================
echo.

:: Check for Java
where java >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Java SDK was not found in your PATH.
    echo Please install Java JDK 17 or higher from:
    echo https://adoptium.net/
    echo.
    pause
    exit /b 1
)

:: Check for Maven
where mvn >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Maven was not found in your PATH.
    echo Please install Apache Maven from:
    echo https://maven.apache.org/download.cgi
    echo.
    pause
    exit /b 1
)

echo [INFO] Environment check passed. Launching application...
echo [INFO] Directory: lost-found-system/
echo.

cd lost-found-system
call mvn clean javafx:run

if %errorlevel% neq 0 (
    echo.
    echo [ERROR] Application exited with error code %errorlevel%.
    echo Please ensure your MySQL database is running and configured correctly.
)

pause
