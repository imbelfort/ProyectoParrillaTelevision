FROM eclipse-temurin:8-jdk

WORKDIR /app

# Copiar librerias
COPY lib/ /app/lib/

# Copiar codigo fuente
COPY src/ /app/src/

# Compilar proyecto en Linux
RUN mkdir -p /app/build/classes && \
    find src -name "*.java" > sources.txt && \
    javac -encoding UTF-8 -cp "lib/*" -d build/classes @sources.txt && \
    rm sources.txt

# Variables y credenciales hardcodeadas (PostgreSQL en mail.tecnoweb.org.bo, POP3 en mail.tecnoweb.org.bo)

# Ejecutar el sistema 100% en consola / demonio de correos
ENTRYPOINT ["java", "-cp", "build/classes:lib/*", "proyectomailgrupo12sc.Consola"]
