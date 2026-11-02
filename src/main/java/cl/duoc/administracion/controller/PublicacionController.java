package cl.duoc.administracion.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import cl.duoc.administracion.dto.PublicacionRequest;
import cl.duoc.administracion.dto.PublicacionResponse;
import cl.duoc.administracion.service.PublicacionService;

@RestController
@RequestMapping("/api/publicaciones")
public class PublicacionController {

    private final PublicacionService publicacionService;

    // Inyeccion de dependencias
    public PublicacionController(PublicacionService publicacionService) {
        this.publicacionService = publicacionService;
    }

    // GET: Listar publicaciones
    @GetMapping
    public ResponseEntity<List<PublicacionResponse>> listarPublicaciones() {

        return ResponseEntity.ok(
            publicacionService.listarPublicaciones()
        );
    }

    // GET: Consultar publicacion por ID
    @GetMapping("/{id}")
    public ResponseEntity<PublicacionResponse> obtenerPublicacionPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(
            publicacionService.obtenerPublicacionPorId(id)
        );
    }

    // POST: Crear publicacion
    @PostMapping
    public ResponseEntity<PublicacionResponse> crearPublicacion(
            @Valid @RequestBody PublicacionRequest request) {

        PublicacionResponse publicacion =
                publicacionService.crearPublicacion(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(publicacion);
    }

    // PUT: Actualizar publicacion
    @PutMapping("/{id}")
    public ResponseEntity<PublicacionResponse> actualizarPublicacion(
            @PathVariable Long id,
            @Valid @RequestBody PublicacionRequest request) {

        return ResponseEntity.ok(
            publicacionService.actualizarPublicacion(id, request)
        );
    }

    // PATCH: Banear publicacion
    @PatchMapping("/{id}/banear")
    public ResponseEntity<PublicacionResponse> banearPublicacion(
            @PathVariable Long id) {

        return ResponseEntity.ok(
            publicacionService.banearPublicacion(id)
        );
    }

    // PATCH: Reactivar publicacion
    @PatchMapping("/{id}/reactivar")
    public ResponseEntity<PublicacionResponse> reactivarPublicacion(
            @PathVariable Long id) {

        return ResponseEntity.ok(
            publicacionService.reactivarPublicacion(id)
        );
    }

    // DELETE: Eliminar publicacion
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPublicacion(
            @PathVariable Long id) {

        publicacionService.eliminarPublicacion(id);

        return ResponseEntity.noContent().build();
    }
}
