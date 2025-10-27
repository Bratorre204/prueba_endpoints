# Usar imagen oficial de OpenJDK 21 para la construcción
FROM openjdk:21-jdk-slim AS builder

# Instalar herramientas necesarias
RUN apt-get update && apt-get install -y \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Establecer directorio de trabajo
WORKDIR /app

# Copiar archivos de Gradle
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Dar permisos de ejecución al wrapper de Gradle
RUN chmod +x gradlew

# Descargar dependencias (esto se cachea si no cambian los archivos de configuración)
RUN ./gradlew dependencies --no-daemon

# Copiar código fuente
COPY src src

# Construir la aplicación
RUN ./gradlew build --no-daemon -x test

# Imagen de producción más ligera
FROM openjdk:21-jre-slim

# Instalar herramientas básicas
RUN apt-get update && apt-get install -y \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Crear usuario no-root para seguridad
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Establecer directorio de trabajo
WORKDIR /app

# Copiar el JAR construido desde la etapa anterior
COPY --from=builder /app/build/libs/asistencia-0.0.1-SNAPSHOT.jar app.jar

# Cambiar propiedad del archivo al usuario no-root
RUN chown appuser:appuser app.jar

# Cambiar al usuario no-root
USER appuser

# Exponer el puerto (Render usa el puerto definido en la variable de entorno PORT)
EXPOSE 8080

# Configurar variables de entorno para Render
ENV SPRING_PROFILES_ACTIVE=prod
ENV SERVER_PORT=${PORT:-8080}

# Comando de inicio con optimizaciones para contenedores
ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-XX:+UseG1GC", \
    "-XX:+UseStringDeduplication", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", \
    "app.jar"]
