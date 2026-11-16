@echo off
echo =================================================================
echo   INICIANDO CONTENEDOR DOCKER - PARRILLA TV + WEBMAIL ROUNDCUBE
echo =================================================================
docker compose up -d --build
echo.
echo [OK] Contenedores levantados con exito:
echo   - Webmail Roundcube: http://localhost:8085
echo   - PostgreSQL: localhost:5432 (db_grupo12sc)
echo   - MailServer (Greenmail): POP3:110, IMAP:143, SMTP:25
echo   - App Java Demonio: parrilla-tv-server
echo.
echo Mostrando logs en tiempo real (Presione Ctrl+C para salir de los logs):
docker compose logs -f
