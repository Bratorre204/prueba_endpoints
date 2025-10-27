# 🚀 Despliegue en Render - Sistema de Asistencia UNIPAZ

## 📋 Archivos de Configuración

Este proyecto incluye todos los archivos necesarios para desplegar la aplicación Spring Boot en Render:

- `Dockerfile` - Configuración de contenedor Docker optimizada
- `.dockerignore` - Archivos excluidos del build
- `src/main/resources/application-prod.properties` - Configuración de producción
- `render.yaml` - Configuración específica de Render

## 🔧 Variables de Entorno Requeridas

Configura estas variables en el dashboard de Render:

```
DATABASE_URL=jdbc:postgresql://tu-host:5432/tu-database
DB_USERNAME=tu-usuario-db
DB_PASSWORD=tu-contraseña-db
```

## 📦 Características del Dockerfile

- ✅ **Multi-stage build** para imagen optimizada
- ✅ **OpenJDK 21** (compatible con tu proyecto)
- ✅ **Usuario no-root** para seguridad
- ✅ **Optimizaciones JVM** para contenedores
- ✅ **Puerto dinámico** compatible con Render

## 🚀 Pasos para Desplegar

1. **Sube el código** a GitHub
2. **Conecta el repositorio** en Render
3. **Selecciona Docker** como entorno
4. **Configura las variables** de entorno
5. **Despliega** la aplicación

## 🔍 Verificación

- ✅ Proyecto compila correctamente
- ✅ JAR se genera sin errores
- ✅ Configuración compatible con Render
- ✅ Variables de entorno configuradas
- ✅ Dockerfile optimizado

## 📊 Especificaciones Técnicas

- **Java**: OpenJDK 21
- **Framework**: Spring Boot 3.5.5
- **Base de datos**: PostgreSQL
- **Build tool**: Gradle
- **Puerto**: Dinámico (Render)
- **Perfil**: `prod`

¡Tu aplicación está lista para desplegarse en Render! 🎉
