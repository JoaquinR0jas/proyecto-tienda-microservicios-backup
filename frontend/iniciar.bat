@echo off
setlocal
title FreshFits - Iniciar frontend
cd /d "%~dp0"

echo ============================================
echo   FreshFits - arrancando el frontend
echo ============================================
echo.

rem --- buscar un python que de verdad funcione ---
rem (en algunos PC el comando "python" es solo un alias tonto de la
rem Microsoft Store, por eso probamos y sino usamos "py")
set "PY="
python --version >nul 2>nul
if not errorlevel 1 (
    set "PY=python"
) else (
    py -3 --version >nul 2>nul
    if not errorlevel 1 (
        set "PY=py -3"
    )
)

if not defined PY (
    echo No encontre python en el sistema.
    echo Descargalo desde https://www.python.org/downloads/
    echo y al instalar marca "Add python.exe to PATH".
    pause
    exit /b 1
)

rem --- crear el entorno virtual si no existe ---
if not exist ".venv\Scripts\python.exe" (
    echo.
    echo Creando el entorno virtual...
    %PY% -m venv .venv
    if errorlevel 1 (
        echo Fallo al crear el entorno virtual.
        pause
        exit /b 1
    )
)

echo.
echo Primer arranque: instalando Flask (solo la primera vez)...
".venv\Scripts\python.exe" -m pip install --quiet --disable-pip-version-check flask
if errorlevel 1 (
    echo Fallo al instalar Flask. Revisa tu conexion a internet.
    pause
    exit /b 1
)

echo.
echo Listo. Abriendo el navegador en http://127.0.0.1:5000 ...
start "" http://127.0.0.1:5000

echo Si la pestaña no se abre sola, ve a http://127.0.0.1:5000
echo Para detener el servidor, cierra esta ventana o presiona Ctrl+C
echo.

".venv\Scripts\python.exe" app.py

pause
