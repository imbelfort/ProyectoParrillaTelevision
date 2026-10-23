@echo off
echo =================================================================
echo   COMPILANDO Y EJECUTANDO SISTEMA PARRILLA TV (MODO CONSOLA)
echo =================================================================
if not exist "build\classes" mkdir build\classes
dir /s /b src\*.java > sources.txt
javac -encoding UTF-8 -cp "lib/*" -d build/classes @sources.txt
del sources.txt
echo [OK] Compilacion exitosa. Iniciando servidor en consola...
java -cp "build/classes;lib/*" proyectomailgrupo12sc.Consola
pause
