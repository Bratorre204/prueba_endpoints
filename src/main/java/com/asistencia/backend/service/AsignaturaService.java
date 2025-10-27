package com.asistencia.backend.service;

import com.asistencia.backend.dto.CrearAsignaturaRequest;
import com.asistencia.backend.model.Asignatura;
import com.asistencia.backend.repository.AsignaturaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AsignaturaService {
    
    private final AsignaturaRepository asignaturaRepository;
    
    public Page<Asignatura> getAllAsignaturas(Pageable pageable) {
        return asignaturaRepository.findAll(pageable);
    }
    
    public Asignatura getAsignaturaById(Long id) {
        return asignaturaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Asignatura no encontrada"));
    }
    
    public Asignatura getAsignaturaByCodigo(String codigo) {
        return asignaturaRepository.findByCodigo(codigo)
            .orElseThrow(() -> new RuntimeException("Asignatura no encontrada"));
    }
    
    public Asignatura crearAsignatura(CrearAsignaturaRequest request) {
        // Verificar si ya existe una asignatura con el mismo código
        if (asignaturaRepository.existsByCodigo(request.getCodigo())) {
            throw new RuntimeException("Ya existe una asignatura con el código: " + request.getCodigo());
        }
        
        Asignatura asignatura = Asignatura.builder()
            .codigo(request.getCodigo())
            .nombre(request.getNombre())
            .descripcion(request.getDescripcion())
            .abreviatura(request.getAbreviatura())
            .creditos(request.getCreditos())
            .build();
        
        return asignaturaRepository.save(asignatura);
    }
    
    public Asignatura actualizarAsignatura(Long id, CrearAsignaturaRequest request) {
        Asignatura asignatura = getAsignaturaById(id);
        
        // Verificar si el código ya existe en otra asignatura
        if (!asignatura.getCodigo().equals(request.getCodigo()) && 
            asignaturaRepository.existsByCodigo(request.getCodigo())) {
            throw new RuntimeException("Ya existe una asignatura con el código: " + request.getCodigo());
        }
        
        asignatura.setCodigo(request.getCodigo());
        asignatura.setNombre(request.getNombre());
        asignatura.setDescripcion(request.getDescripcion());
        asignatura.setAbreviatura(request.getAbreviatura());
        asignatura.setCreditos(request.getCreditos());
        
        return asignaturaRepository.save(asignatura);
    }
    
    public void eliminarAsignatura(Long id) {
        Asignatura asignatura = getAsignaturaById(id);
        
        // Verificar si tiene cursos asociados
        if (!asignatura.getCursos().isEmpty()) {
            throw new RuntimeException("No se puede eliminar una asignatura que tiene cursos asociados");
        }
        
        asignaturaRepository.delete(asignatura);
    }
    
    public List<Asignatura> buscarAsignaturas(String query) {
        return asignaturaRepository.findAll().stream()
            .filter(a -> a.getNombre().toLowerCase().contains(query.toLowerCase()) ||
                        a.getCodigo().toLowerCase().contains(query.toLowerCase()) ||
                        a.getDescripcion().toLowerCase().contains(query.toLowerCase()))
            .toList();
    }
}