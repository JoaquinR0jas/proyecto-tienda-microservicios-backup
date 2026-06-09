@echo off
title Stop All - Tienda Microservicios
echo ============================================
echo Deteniendo todos los procesos Java...
echo ============================================
echo.

powershell -Command "Get-Process -Name 'java' -ErrorAction SilentlyContinue | Stop-Process -Force"

echo Todos los procesos Java han sido detenidos.
echo.
echo NOTA: Si tiene otros procesos Java ajenos al proyecto,
echo estos se detendran tambien. Cierre las ventanas manualmente
echo si no desea detenerlos.
echo.
pause
