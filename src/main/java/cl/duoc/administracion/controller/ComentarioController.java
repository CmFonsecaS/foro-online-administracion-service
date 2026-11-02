package cl.duoc.administracion.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import cl.duoc.administracion.dto.ComentarioRequest;
import cl.duoc.administracion.dto.ComentarioResponse;
import cl.duoc.administracion.service.ComentarioService;

@RestController
@RequestMapping("/api/comentarios")
@Validated
public class ComentarioController {

    private final ComentarioService comentarioService;

    public ComentarioController(ComentarioService comentarioService) {
        this.comentarioService = comentarioService;
    }

    // Listar todos los comentarios
    @GetMapping
    public ResponseEntity<List<ComentarioResponse>> listar() {

        return ResponseEntity.ok(comentarioService.listar());
    }

    // Buscar comentario por ID
    @GetMapping("/{id}")
    public ResponseEntity<ComentarioResponse> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(comentarioService.buscarPorId(id));
    }

    // Crear comentario
    @PostMapping
    public ResponseEntity<ComentarioResponse> crear(
            @Valid @RequestBody ComentarioRequest request) {

        ComentarioResponse respuesta = comentarioService.crear(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    // Actualizar comentario
    @PutMapping("/{id}")
    public ResponseEntity<ComentarioResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ComentarioRequest request) {

        return ResponseEntity.ok(
                comentarioService.actualizar(id, request)
        );
    }

    // Eliminar comentario
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {

        comentarioService.eliminar(id);

        return ResponseEntity.noContent().build();
    }

    // Banear comentario
    @PatchMapping("/{id}/banear")
    public ResponseEntity<ComentarioResponse> banear(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                comentarioService.banearComentario(id)
        );
    }

    // Reactivar comentario
    @PatchMapping("/{id}/reactivar")
    public ResponseEntity<ComentarioResponse> reactivar(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                comentarioService.reactivarComentario(id)
        );
    }
}