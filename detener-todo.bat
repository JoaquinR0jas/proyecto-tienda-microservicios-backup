@echo off
chcp 65001 >nul
title FreshFits - detener todo
echo ==========================================
echo   FreshFits - apagando el proyecto
echo ==========================================
echo.

echo Cerrando microservicios (java -jar)...
powershell -NoProfile -Command "Get-CimInstance Win32_Process | Where-Object { $_.CommandLine -like '*-1.0.0.jar*' } | ForEach-Object { Stop-Process -Id $_.ProcessId -Force -ErrorAction SilentlyContinue }"

echo Cerrando el frontend (Vite)...
powershell -NoProfile -Command "Get-CimInstance Win32_Process | Where-Object { $_.CommandLine -like '*frontend-react*' } | ForEach-Object { Stop-Process -Id $_.ProcessId -Force -ErrorAction SilentlyContinue }"

echo.
echo MySQL queda encendido para que el proximo arranque sea mas rapido.
echo Si tambien lo quieres apagar, ejecuta:
echo   C:\xampp\mysql\bin\mysqladmin.exe -u root -h 127.0.0.1 -P 3307 shutdown
echo.
pause
