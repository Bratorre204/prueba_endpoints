-- ============================================
-- SCRIPT DE CREACIÓN DE BASE DE DATOS
-- Sistema de Asistencia con Geolocalización
-- ============================================

-- Crear base de datos
CREATE DATABASE asistencia_db;

-- Usar la base de datos
\c asistencia_db;

-- ============================================
-- TABLA DE ROLES
-- ============================================
CREATE TABLE roles (
    id_rol SERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(255),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insertar roles por defecto
INSERT INTO roles (nombre, descripcion) VALUES 
('PROFESOR', 'Rol para profesores que pueden crear sesiones'),
('ESTUDIANTE', 'Rol para estudiantes que pueden registrar asistencia'),
('ADMIN', 'Rol administrativo con acceso completo');

-- ============================================
-- TABLA DE USUARIOS
-- ============================================
CREATE TABLE usuarios (
    id_usuario SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    correo VARCHAR(100) NOT NULL UNIQUE,
    contrasena VARCHAR(255) NOT NULL,
    identificacion VARCHAR(20) NOT NULL UNIQUE,
    programa VARCHAR(100),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    creado_por VARCHAR(100),
    modificado_por VARCHAR(100)
);

-- ============================================
-- TABLA DE RELACIÓN USUARIOS-ROLES
-- ============================================
CREATE TABLE usuarios_roles (
    id_usuario INTEGER REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
    id_rol INTEGER REFERENCES roles(id_rol) ON DELETE CASCADE,
    PRIMARY KEY (id_usuario, id_rol)
);

-- ============================================
-- TABLA DE ASIGNATURAS (MATERIAS GENERALES)
-- ============================================
CREATE TABLE asignaturas (
    id SERIAL PRIMARY KEY,
    codigo VARCHAR(20) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT NOT NULL,
    abreviatura VARCHAR(10),
    creditos INTEGER CHECK (creditos > 0),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    creado_por VARCHAR(100),
    modificado_por VARCHAR(100)
);

-- ============================================
-- TABLA DE CURSOS (INSTANCIAS ESPECÍFICAS)
-- ============================================
CREATE TABLE cursos (
    id SERIAL PRIMARY KEY,
    codigo VARCHAR(15) UNIQUE NOT NULL,
    descripcion TEXT NOT NULL,
    id_asignatura INTEGER NOT NULL REFERENCES asignaturas(id) ON DELETE CASCADE,
    nombre VARCHAR(100) NOT NULL, -- Ej: "Programación I - Diurna"
    periodo VARCHAR(20) NOT NULL, -- Ej: "2024-1"
    turno VARCHAR(20) NOT NULL, -- DIURNA, NOCTURNA, etc.
    seccion VARCHAR(10), -- Ej: "A", "B", "C"
    aula VARCHAR(50), -- Ej: "Aula 101"
    horario VARCHAR(100), -- Ej: "Lunes 8:00-10:00"
    año INTEGER NOT NULL,
    semestre INTEGER NOT NULL CHECK (semestre IN (1, 2)),
    id_profesor INTEGER NOT NULL REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    creado_por VARCHAR(100),
    modificado_por VARCHAR(100)
);

-- ============================================
-- TABLA DE INSCRIPCIONES (USUARIOS-CURSOS)
-- ============================================
CREATE TABLE usuarios_cursos (
    id SERIAL PRIMARY KEY,
    id_usuario INTEGER NOT NULL REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
    id_curso INTEGER NOT NULL REFERENCES cursos(id) ON DELETE CASCADE,
    estado VARCHAR(20) DEFAULT 'ACTIVO' CHECK (estado IN ('ACTIVO', 'INACTIVO')),
    observaciones TEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    creado_por VARCHAR(100),
    modificado_por VARCHAR(100),
    UNIQUE(id_usuario, id_curso)
);

-- ============================================
-- TABLA DE SESIONES
-- ============================================
CREATE TABLE sesiones (
    id SERIAL PRIMARY KEY,
    id_profesor INTEGER NOT NULL REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
    id_curso INTEGER NOT NULL REFERENCES cursos(id) ON DELETE CASCADE,
    nombre VARCHAR(200) NOT NULL,
    descripcion TEXT,
    aula VARCHAR(100),
    latitud_profesor DECIMAL(10, 8) NOT NULL CHECK (latitud_profesor >= -90 AND latitud_profesor <= 90),
    longitud_profesor DECIMAL(11, 8) NOT NULL CHECK (longitud_profesor >= -180 AND longitud_profesor <= 180),
    radio_proximidad DECIMAL(8, 2) DEFAULT 6.0 CHECK (radio_proximidad > 0),
    fecha_inicio TIMESTAMP NOT NULL,
    fecha_fin TIMESTAMP,
    fecha_cierre TIMESTAMP,
    estado VARCHAR(20) DEFAULT 'ACTIVA' CHECK (estado IN ('ACTIVA', 'FINALIZADA', 'CANCELADA')),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    creado_por VARCHAR(100),
    modificado_por VARCHAR(100)
);

-- ============================================
-- TABLA DE DETALLES DE ASISTENCIA
-- ============================================
CREATE TABLE sesiones_detalle (
    id SERIAL PRIMARY KEY,
    id_sesion INTEGER NOT NULL REFERENCES sesiones(id) ON DELETE CASCADE,
    id_estudiante INTEGER NOT NULL REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
    fecha_firma TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    estado VARCHAR(20) NOT NULL CHECK (estado IN ('PRESENTE', 'AUSENTE', 'TARDIO', 'FUERA_RANGO')),
    latitud_estudiante DECIMAL(10, 8) NOT NULL CHECK (latitud_estudiante >= -90 AND latitud_estudiante <= 90),
    longitud_estudiante DECIMAL(11, 8) NOT NULL CHECK (longitud_estudiante >= -180 AND longitud_estudiante <= 180),
    distancia_metros DECIMAL(10, 2) NOT NULL CHECK (distancia_metros >= 0),
    observacion TEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    creado_por VARCHAR(100),
    modificado_por VARCHAR(100),
    UNIQUE(id_sesion, id_estudiante)
);

-- ============================================
-- ÍNDICES PARA OPTIMIZACIÓN
-- ============================================

-- Índices para usuarios
CREATE INDEX idx_usuarios_correo ON usuarios(correo);
CREATE INDEX idx_usuarios_roles_usuario ON usuarios_roles(id_usuario);
CREATE INDEX idx_usuarios_roles_rol ON usuarios_roles(id_rol);

-- Índices para cursos
CREATE INDEX idx_cursos_profesor ON cursos(id_profesor);
CREATE INDEX idx_cursos_periodo ON cursos(periodo);
CREATE INDEX idx_cursos_turno ON cursos(turno);
CREATE INDEX idx_usuarios_cursos_usuario ON usuarios_cursos(id_usuario);
CREATE INDEX idx_usuarios_cursos_curso ON usuarios_cursos(id_curso);
CREATE INDEX idx_usuarios_cursos_estado ON usuarios_cursos(estado);

-- Índices para sesiones
CREATE INDEX idx_sesiones_profesor ON sesiones(id_profesor);
CREATE INDEX idx_sesiones_curso ON sesiones(id_curso);
CREATE INDEX idx_sesiones_estado ON sesiones(estado);
CREATE INDEX idx_sesiones_fecha_creacion ON sesiones(fecha_creacion);

-- Índices para detalles de asistencia
CREATE INDEX idx_sesiones_detalle_sesion ON sesiones_detalle(id_sesion);
CREATE INDEX idx_sesiones_detalle_estudiante ON sesiones_detalle(id_estudiante);
CREATE INDEX idx_sesiones_detalle_estado ON sesiones_detalle(estado);
CREATE INDEX idx_sesiones_detalle_fecha_firma ON sesiones_detalle(fecha_firma);

-- ============================================
-- DATOS DE PRUEBA
-- ============================================

-- Insertar usuarios de prueba
INSERT INTO usuarios (nombre, apellido, correo, contrasena, identificacion, programa) VALUES 
('Juan', 'Pérez', 'juan.perez@unipaz.edu.co', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '12345678', 'Ingeniería de Sistemas'),
('María', 'González', 'maria.gonzalez@unipaz.edu.co', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '87654321', 'Ingeniería de Sistemas'),
('Carlos', 'Rodríguez', 'carlos.rodriguez@unipaz.edu.co', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '11223344', 'Ingeniería de Sistemas'),
('Ana', 'Martínez', 'ana.martinez@unipaz.edu.co', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '44332211', 'Ingeniería de Sistemas');

-- Asignar roles
INSERT INTO usuarios_roles (id_usuario, id_rol) VALUES 
(1, 1), -- Juan es profesor
(2, 2), -- María es estudiante
(3, 2), -- Carlos es estudiante
(4, 2); -- Ana es estudiante

-- Insertar asignaturas de prueba
INSERT INTO asignaturas (codigo, nombre, descripcion, abreviatura, creditos) VALUES 
('PROG001', 'Programación I', 'Fundamentos de programación orientada a objetos', 'PROG I', 3),
('BD001', 'Bases de Datos', 'Fundamentos de diseño y gestión de bases de datos', 'BD', 3),
('ED001', 'Estructuras de Datos', 'Algoritmos y estructuras de datos fundamentales', 'ED', 3),
('WEB001', 'Desarrollo Web', 'Desarrollo de aplicaciones web modernas', 'WEB', 4);

-- Insertar cursos de prueba (instancias específicas)
INSERT INTO cursos (codigo, descripcion, id_asignatura, nombre, periodo, turno, seccion, aula, horario, año, semestre, id_profesor) VALUES 
('603D12025A', 'Curso de Programación I en modalidad diurna para estudiantes de primer semestre', 1, 'Programación I - Diurna', '2025-1', 'DIURNA', 'A', 'Aula 101', 'Lunes 8:00-10:00', 2025, 1, 1),
('603N12025A', 'Curso de Programación I en modalidad nocturna para estudiantes de primer semestre', 1, 'Programación I - Nocturna', '2025-1', 'NOCTURNA', 'A', 'Aula 201', 'Lunes 18:00-20:00', 2025, 1, 1),
('604D12025A', 'Curso de Bases de Datos en modalidad diurna para estudiantes de primer semestre', 2, 'Bases de Datos - Diurna', '2025-1', 'DIURNA', 'A', 'Aula 102', 'Martes 8:00-10:00', 2025, 1, 1),
('604N12025A', 'Curso de Bases de Datos en modalidad nocturna para estudiantes de primer semestre', 2, 'Bases de Datos - Nocturna', '2025-1', 'NOCTURNA', 'A', 'Aula 202', 'Martes 18:00-20:00', 2025, 1, 1),
('605D12025A', 'Curso de Estructuras de Datos en modalidad diurna para estudiantes de primer semestre', 3, 'Estructuras de Datos - Diurna', '2025-1', 'DIURNA', 'A', 'Aula 103', 'Miércoles 8:00-10:00', 2025, 1, 1);

-- Inscribir estudiantes en cursos
INSERT INTO usuarios_cursos (id_usuario, id_curso, estado) VALUES 
(2, 1, 'ACTIVO'), -- María en Programación I - Diurna
(3, 1, 'ACTIVO'), -- Carlos en Programación I - Diurna
(4, 1, 'ACTIVO'), -- Ana en Programación I - Diurna
(2, 3, 'ACTIVO'), -- María en Bases de Datos - Diurna
(3, 3, 'ACTIVO'), -- Carlos en Bases de Datos - Diurna
(4, 5, 'ACTIVO'); -- Ana en Estructuras de Datos - Diurna

-- ============================================
-- FUNCIONES DE UTILIDAD
-- ============================================

-- Función para calcular distancia entre dos puntos geográficos
CREATE OR REPLACE FUNCTION calcular_distancia(
    lat1 DECIMAL, lon1 DECIMAL, 
    lat2 DECIMAL, lon2 DECIMAL
) RETURNS DECIMAL AS $$
DECLARE
    earth_radius DECIMAL := 6371000; -- Radio de la Tierra en metros
    lat1_rad DECIMAL;
    lat2_rad DECIMAL;
    delta_lat_rad DECIMAL;
    delta_lon_rad DECIMAL;
    a DECIMAL;
    c DECIMAL;
BEGIN
    lat1_rad := radians(lat1);
    lat2_rad := radians(lat2);
    delta_lat_rad := radians(lat2 - lat1);
    delta_lon_rad := radians(lon2 - lon1);
    
    a := sin(delta_lat_rad / 2) * sin(delta_lat_rad / 2) +
         cos(lat1_rad) * cos(lat2_rad) *
         sin(delta_lon_rad / 2) * sin(delta_lon_rad / 2);
    
    c := 2 * atan2(sqrt(a), sqrt(1 - a));
    
    RETURN earth_radius * c;
END;
$$ LANGUAGE plpgsql;

-- ============================================
-- VISTAS ÚTILES
-- ============================================

-- Vista para estadísticas de asistencia por curso
CREATE VIEW vista_estadisticas_curso AS
SELECT 
    c.id as curso_id,
    c.nombre as curso_nombre,
    c.periodo,
    COUNT(DISTINCT s.id) as total_sesiones,
    COUNT(DISTINCT uc.id_usuario) as total_estudiantes,
    COUNT(sd.id) as total_firmas,
    COUNT(CASE WHEN sd.estado = 'PRESENTE' THEN 1 END) as asistencias,
    ROUND(
        COUNT(CASE WHEN sd.estado = 'PRESENTE' THEN 1 END) * 100.0 / 
        NULLIF(COUNT(sd.id), 0), 2
    ) as porcentaje_asistencia
FROM cursos c
LEFT JOIN sesiones s ON c.id = s.id_curso
LEFT JOIN usuarios_cursos uc ON c.id = uc.id_curso AND uc.estado = 'ACTIVO'
LEFT JOIN sesiones_detalle sd ON s.id = sd.id_sesion
GROUP BY c.id, c.nombre, c.periodo;

-- Vista para estadísticas de estudiantes
CREATE VIEW vista_estadisticas_estudiante AS
SELECT 
    u.id_usuario,
    u.nombre || ' ' || u.apellido as nombre_completo,
    COUNT(sd.id) as total_firmas,
    COUNT(CASE WHEN sd.estado = 'PRESENTE' THEN 1 END) as asistencias,
    COUNT(CASE WHEN sd.estado = 'AUSENTE' THEN 1 END) as ausencias,
    COUNT(CASE WHEN sd.estado = 'TARDIO' THEN 1 END) as tardios,
    COUNT(CASE WHEN sd.estado = 'FUERA_RANGO' THEN 1 END) as fuera_rango,
    ROUND(
        COUNT(CASE WHEN sd.estado = 'PRESENTE' THEN 1 END) * 100.0 / 
        NULLIF(COUNT(sd.id), 0), 2
    ) as porcentaje_asistencia
FROM usuarios u
LEFT JOIN sesiones_detalle sd ON u.id_usuario = sd.id_estudiante
WHERE EXISTS (
    SELECT 1 FROM usuarios_roles ur 
    JOIN roles r ON ur.id_rol = r.id_rol 
    WHERE ur.id_usuario = u.id_usuario AND r.nombre = 'ESTUDIANTE'
)
GROUP BY u.id_usuario, u.nombre, u.apellido;

-- ============================================
-- COMENTARIOS FINALES
-- ============================================

-- La contraseña por defecto para todos los usuarios de prueba es: "password123"
-- Las coordenadas de ejemplo están en Bogotá, Colombia
-- El sistema está configurado para usar coordenadas en formato decimal
-- Los radios de proximidad están en metros
-- Todas las fechas se manejan en UTC
