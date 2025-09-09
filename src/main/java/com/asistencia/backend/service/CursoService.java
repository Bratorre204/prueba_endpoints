package com.asistencia.backend.service;

import com.asistencia.backend.dto.CursoRequest;
import com.asistencia.backend.dto.CursoResponse;
import com.asistencia.backend.model.RolNombre;
import com.asistencia.backend.model.*;
import com.asistencia.backend.model.Usuario;
import com.asistencia.backend.repository.CursoRepository;
import com.asistencia.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CursoService {

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private UserRepository userRepository;

    public CursoResponse crearCurso(CursoRequest request, String profesorCorreo) {
        // 1. Verificar que el usuario sea profesor
        Usuario profesor = userRepository.findByCorreoAndDeleteLogicFalse(profesorCorreo)
                .orElseThrow(() -> new RuntimeException("Profesor no encontrado"));

        // Verificar que tenga rol de profesor
        boolean esProfesor = profesor.getRoles().stream()
                .anyMatch(rol -> rol.getNombre() == RolNombre.PROFESOR);

        if (!esProfesor) {
            throw new RuntimeException("Solo los profesores pueden crear cursos");
        }

        // 2. Crear el curso
        Curso curso = Curso.builder()
                .nombre_curso(request.getNombre())
                .descripcion_curso(request.getDescripcion())
                .profesor(profesor)
                .build();

        // 3. Establecer auditoría
        curso.setCreateUser(profesorCorreo);
        curso.setCreateDate(LocalDateTime.now());
        curso.setDeleteLogic(false);

        // 4. Guardar curso
        Curso savedCurso = cursoRepository.save(curso);

        // 5. Convertir a DTO de respuesta
        return convertToResponse(savedCurso);
    }

    @Transactional(readOnly = true)
    public List<CursoResponse> obtenerCursosPorProfesor(String profesorCorreo) {
        Usuario profesor = userRepository.findByCorreoAndDeleteLogicFalse(profesorCorreo)
                .orElseThrow(() -> new RuntimeException("Profesor no encontrado"));

        List<Curso> cursos = cursoRepository.findByProfesorId(profesor.getId_usuario());

        return cursos.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CursoResponse> obtenerTodosCursosActivos() {
        List<Curso> cursos = cursoRepository.findAllActive();

        return cursos.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CursoResponse obtenerCursoPorId(Long id) {
        Curso curso = cursoRepository.findByIdAndActive(id)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado o no disponible"));

        return convertToResponse(curso);
    }

    public CursoResponse actualizarCurso(Long id, CursoRequest request, String profesorCorreo) {
        Curso curso = cursoRepository.findByIdAndActive(id)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado"));

        // Verificar que sea el profesor del curso o admin
        Usuario usuarioActual = userRepository.findByCorreoAndDeleteLogicFalse(profesorCorreo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        boolean esProfesorDelCurso = curso.getProfesor().getCorreo().equals(profesorCorreo);
        boolean esAdmin = usuarioActual.getRoles().stream()
                .anyMatch(rol -> rol.getNombre() == RolNombre.PROFESOR);

        if (!esProfesorDelCurso && !esAdmin) {
            throw new RuntimeException("Solo el profesor del curso puede modificarlo");
        }

        // Actualizar campos
        curso.setNombre_curso(request.getNombre());
        curso.setDescripcion_curso(request.getDescripcion());

        // Auditoría
        curso.setUpdateUser(profesorCorreo);
        curso.setUpdateDate(LocalDateTime.now());

        Curso updatedCurso = cursoRepository.save(curso);
        return convertToResponse(updatedCurso);
    }

    public void eliminarCurso(Long id, String profesorCorreo) {
        Curso curso = cursoRepository.findByIdAndActive(id)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado"));

        // Verificar que sea el profesor del curso o admin
        Usuario usuarioActual = userRepository.findByCorreoAndDeleteLogicFalse(profesorCorreo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        boolean esProfesorDelCurso = curso.getProfesor().getCorreo().equals(profesorCorreo);
        boolean esAdmin = usuarioActual.getRoles().stream()
                .anyMatch(rol -> rol.getNombre() == RolNombre.PROFESOR);

        if (!esProfesorDelCurso && !esAdmin) {
            throw new RuntimeException("Solo el profesor del curso o un administrador pueden eliminarlo");
        }

        // Eliminación lógica
        curso.setDeleteLogic(true);
        curso.setDeleteUser(profesorCorreo);
        curso.setDeleteDate(LocalDateTime.now());

        cursoRepository.save(curso);
    }

    private CursoResponse convertToResponse(Curso curso) {
        if (curso == null) {
            return null;
        }

        CursoResponse.ProfesorInfo profesorInfo = null;
        if (curso.getProfesor() != null) {
            profesorInfo = CursoResponse.ProfesorInfo.builder()
                    .id(curso.getProfesor().getId_usuario())
                    .nombre(curso.getProfesor().getNombre())
                    .apellidos(curso.getProfesor().getApellido())
                    .correo(curso.getProfesor().getCorreo())
                    .build();
        }
        return CursoResponse.builder()
                .id(curso.getId_curso())
                .nombre(curso.getNombre_curso())
                .descripcion(curso.getDescripcion_curso())
                .profesor(profesorInfo)
                .build();
    }
}

