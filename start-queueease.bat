@echo off
cd /d "%~dp0"
echo ===================================================
echo   Starting QueueEase - Multi-Business Platform
echo ===================================================
echo.
echo Ensuring MySQL service is running...
net start MySQL80 >nul 2>&1
echo.
echo Launching Spring Boot server on http://localhost:8080 ...
echo (The application serves both the Backend API and React Frontend)
echo.
start http://localhost:8080
java -jar target\queueease-1.0.0.jar
pause
