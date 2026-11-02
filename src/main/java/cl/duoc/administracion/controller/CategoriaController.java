package cl.duoc.administracion.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import cl.duoc.administracion.dto.CategoriaRequest;
import cl.duoc.administracion.dto.CategoriaResponse;
import cl.duoc.administracion.service.CategoriaService;

@RestController
@RequestMapping("/api/categorias")
@Validated
public class CategoriaController {

    private final CategoriaService categoriaService;

    // Inyeccion de dependencias
    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    // GET: Listar todas las categorias
    @GetMapping
    public ResponseEntity<List<CategoriaResponse>> listarCategorias() {

        return ResponseEntity.ok(
            categoriaService.listarCategorias()
        );
    }

    // GET: Obtener categoria por ID
    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponse> obtenerCategoriaPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(
            categoriaService.obtenerCategoriaPorId(id)
        );
    }

    // POST: Crear categoria
    @PostMapping
    public ResponseEntity<CategoriaResponse> crearCategoria(
            @Valid @RequestBody CategoriaRequest request) {

        CategoriaResponse categoria =
                categoriaService.crearCategoria(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(categoria);
    }

    // PUT: Actualizar categoria
    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponse> actualizarCategoria(
            @PathVariable Long id,
            @Valid @RequestBody CategoriaRequest request) {

        return ResponseEntity.ok(
            categoriaService.actualizarCategoria(id, request)
        );
    }

    // DELETE: Eliminar categoria
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCategoria(
            @PathVariable Long id) {

        categoriaService.eliminarCategoria(id);

        return ResponseEntity.noContent().build();
    }
}