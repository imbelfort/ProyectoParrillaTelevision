$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $MyInvocation.MyCommand.Path
$lib = Join-Path $root "lib"
$classes = Join-Path $root "build\classes"

Write-Host "==========================================================================" -ForegroundColor Cyan
Write-Host "  SISTEMA DE GESTION DE PARRILLA DE TELEVISION (GRUPO 12 SC)" -ForegroundColor Cyan
Write-Host "  CONEXION DIRECTA AL SERVIDOR INSTITUCIONAL (SIN DOCKER)" -ForegroundColor Cyan
Write-Host "==========================================================================" -ForegroundColor Cyan

# 1. Configuracion institucional del servidor de la universidad (mail.tecnoweb.org.bo)
Write-Host "[INFO] Configurando variables de entorno para Grupo 12 SC..." -ForegroundColor Yellow
$env:DB_HOST = "mail.tecnoweb.org.bo"
$env:DB_PORT = "5432"
$env:DB_NAME = "db_grupo12sc"
$env:DB_USER = "grupo12sc"
$env:DB_PASSWORD = "grup012grup012*"

$env:MAIL_HOST = "mail.tecnoweb.org.bo"
$env:MAIL_SMTP_PORT = "25"
$env:MAIL_POP_PORT = "110"
$env:MAIL_IMAP_PORT = "143"
$env:MAIL_USER = "grupo12sc"
$env:MAIL_PASSWORD = "grup012grup012*"
$env:MAIL_ADDRESS = "grupo12sc@tecnoweb.org.bo"

Write-Host "[OK] Base de datos: jdbc:postgresql://$($env:DB_HOST):$($env:DB_PORT)/$($env:DB_NAME)" -ForegroundColor Green
Write-Host "[OK] Servidor Correo: $($env:MAIL_HOST) (Cuenta: $($env:MAIL_ADDRESS))" -ForegroundColor Green

# 2. Descarga de librerias requeridas (si no existen)
$dependencies = @{
    "activation-1.1.1.jar"  = "https://repo1.maven.org/maven2/javax/activation/activation/1.1.1/activation-1.1.1.jar"
    "gson-2.8.2.jar"        = "https://repo1.maven.org/maven2/com/google/code/gson/gson/2.8.2/gson-2.8.2.jar"
    "javax.mail.jar"        = "https://repo1.maven.org/maven2/com/sun/mail/javax.mail/1.6.2/javax.mail-1.6.2.jar"
    "json-20231013.jar"     = "https://repo1.maven.org/maven2/org/json/json/20231013/json-20231013.jar"
    "json-simple-1.1.1.jar" = "https://repo1.maven.org/maven2/com/googlecode/json-simple/json-simple/1.1.1/json-simple-1.1.1.jar"
    "okhttp-3.11.0.jar"     = "https://repo1.maven.org/maven2/com/squareup/okhttp3/okhttp/3.11.0/okhttp-3.11.0.jar"
    "okio-1.14.0.jar"       = "https://repo1.maven.org/maven2/com/squareup/okio/okio/1.14.0/okio-1.14.0.jar"
    "postgresql-42.3.0.jar" = "https://repo1.maven.org/maven2/org/postgresql/postgresql/42.3.0/postgresql-42.3.0.jar"
}

New-Item -ItemType Directory -Force -Path $lib, $classes | Out-Null

foreach ($dependency in $dependencies.GetEnumerator()) {
    $target = Join-Path $lib $dependency.Key
    if (-not (Test-Path $target)) {
        Write-Host "[INFO] Descargando $($dependency.Key)..." -ForegroundColor Yellow
        curl.exe -fL $dependency.Value -o $target
        if ($LASTEXITCODE -ne 0) {
            throw "No se pudo descargar $($dependency.Key)"
        }
    }
}

# 3. Construccion del classpath y compilacion
$classpath = ((Get-ChildItem -LiteralPath $lib -Filter "*.jar" -File |
        ForEach-Object { $_.FullName }) -join ";")
$sources = Get-ChildItem -LiteralPath (Join-Path $root "src") -Recurse -Filter "*.java" |
    ForEach-Object { $_.FullName }

$sourcesFile = Join-Path $root "sources_build.txt"
[System.IO.File]::WriteAllLines($sourcesFile, $sources, (New-Object System.Text.UTF8Encoding($false)))

Write-Host "[INFO] Compilando codigo fuente Java (UTF-8)..." -ForegroundColor Yellow
& javac -encoding UTF-8 -cp $classpath -d $classes "@$sourcesFile"
if ($LASTEXITCODE -ne 0) {
    Remove-Item -Force -Path $sourcesFile -ErrorAction SilentlyContinue
    throw "La compilación falló"
}
Remove-Item -Force -Path $sourcesFile -ErrorAction SilentlyContinue
Write-Host "[OK] Compilacion completada con exito." -ForegroundColor Green

# 4. Ejecucion del demonio principal en consola
Write-Host "[INFO] Iniciando Consola y Demonio de Correo (proyectomailgrupo12sc.Principal)..." -ForegroundColor Cyan
Write-Host "==========================================================================" -ForegroundColor Cyan
& java "-Dfile.encoding=UTF-8" -cp "$classpath;$classes" proyectomailgrupo12sc.Principal
