@echo off
echo =================================================================
echo   EJECUTANDO SUITE COMPLETA DE PRUEBAS DEL SISTEMA
echo =================================================================
if not exist "build\classes" mkdir build\classes
dir /s /b src\*.java > sources.txt
javac -encoding UTF-8 -cp "lib/*" -d build/classes @sources.txt
del sources.txt

dir /s /b test\*.java > test_sources.txt
javac -encoding UTF-8 -cp "build/classes;lib/*" -d build/classes @test_sources.txt
del test_sources.txt

echo.
echo [1/3] Ejecutando Pruebas de Sintaxis y Codigos de Error (41 Casos)...
java -cp "build/classes;lib/*" test.PruebaCodigosError

echo.
echo [2/3] Ejecutando Prueba de Conexion POP3...
java -cp "build/classes;lib/*" test.PruebaConexionPOP

echo.
echo [3/4] Ejecutando Prueba de Comandos Rapidos (8 Casos de Uso + SMTP)...
java -cp "build/classes;lib/*" test.PruebaFlujoCompleto

echo.
echo [4/5] Ejecutando Prueba de Ciclo de Vida Completo (CRUD Paso a Paso E2E)...
java -cp "build/classes;lib/*" test.PruebaCicloVidaCompleto

echo.
echo [5/5] Ejecutando Prueba en Consola Simulando Roles Operativos...
java -cp "build/classes;lib/*" test.PruebaConsolaPorRoles

echo.
echo =================================================================
echo   TODAS LAS PRUEBAS COMPLETADAS
echo =================================================================
pause
