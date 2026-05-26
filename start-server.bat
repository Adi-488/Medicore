@echo off
title CurePulse HMS — Standalone API Server
echo ====================================================================
echo                   CurePulse HMS Standalone API Server
echo ====================================================================
echo.
echo Starting the Spring Boot backend server...
echo Access Swagger Docs at: http://localhost:8080/swagger-ui.html
echo Access REST APIs at:     http://localhost:8080/api/dashboard/stats
echo.
echo Press Ctrl+C to stop the server at any time.
echo.
echo ====================================================================
echo.

java -jar target\Hospital_Management-0.0.1-SNAPSHOT.jar

pause
