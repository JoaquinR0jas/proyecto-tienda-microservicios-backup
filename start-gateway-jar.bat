@echo off
set DIR=%~dp0
echo Starting API Gateway...
start "API-Gateway" cmd /c "java -jar %DIR%api-gateway\target\api-gateway-1.0.0.jar"
echo Gateway launching on http://localhost:8080
pause
