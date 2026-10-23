@echo off
echo =================================================================
echo   INICIANDO CONTENEDOR DOCKER - PARRILLA TV SERVER
echo =================================================================
docker compose up -d --build
echo.
echo [OK] Contenedor levantado en segundo plano.
echo Mostrando logs en tiempo real (Presione Ctrl+C para salir de los logs):
docker compose logs -f
