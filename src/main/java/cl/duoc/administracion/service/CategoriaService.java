package cl.duoc.administracion.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.duoc.administracion.dto.CategoriaRequest;
import cl.duoc.administracion.dto.CategoriaResponse;
import cl.duoc.administracion.model.Categoria;
import cl.duoc.administracion.repository.CategoriaRepository;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    // Inyeccion de dependencias
    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    // GET: Listar todas las categorias
    @Transactional(readOnly = true)
    public List<CategoriaResponse> listarCategorias() {

        return categoriaRepository.findAll()
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    // GET: Obtener categoria por ID
    @Transactional(readOnly = true)
    public CategoriaResponse obtenerCategoriaPorId(Long id) {

        Categoria categoria = buscarCategoria(id);

        return convertirAResponse(categoria);
    }

    // POST: Crear categoria
    @Transactional
    public CategoriaResponse crearCategoria(CategoriaRequest request) {

        if (categoriaRepository.existsByNombre(request.nombre())) {
            throw new IllegalArgumentException(
                "Ya existe una categoria con ese nombre"
            );
        }

        Categoria categoria = new Categoria();

        categoria.setNombre(request.nombre());
        categoria.setDescripcion(request.descripcion());

        Categoria categoriaGuardada =
                categoriaRepository.save(categoria);

        return convertirAResponse(categoriaGuardada);
    }

    // PUT: Actualizar categoria
    @Transactional
    public CategoriaResponse actualizarCategoria(
            Long id,
            CategoriaRequest request) {

        Categoria categoria = buscarCategoria(id);

        categoriaRepository.findByNombre(request.nombre())
                .ifPresent(existente -> {
                    if (!existente.getIdCategoria().equals(id)) {
                        throw new IllegalArgumentException(
                            "Ya existe otra categoria con ese nombre"
                        );
                    }
                });

        categoria.setNombre(request.nombre());
        categoria.setDescripcion(request.descripcion());

        Categoria categoriaActualizada =
                categoriaRepository.save(categoria);

        return convertirAResponse(categoriaActualizada);
    }

    // DELETE: Eliminar categoria
    @Transactional
    public void eliminarCategoria(Long id) {

        Categoria categoria = buscarCategoria(id);

        try {
            categoriaRepository.delete(categoria);
            categoriaRepository.flush();

        } catch (DataIntegrityViolationException ex) {
            throw new IllegalStateException(
                "No se puede eliminar la categoria porque tiene publicaciones asociadas",
                ex
            );
        }
    }

    // Buscar entidad por ID
    private Categoria buscarCategoria(Long id) {

        return categoriaRepository.findById(id)
                .orElseThrow(() ->
                    new NoSuchElementException(
                        "No existe una categoria con ID: " + id
                    )
                );
    }

    // Convertir entidad a DTO
    private CategoriaResponse convertirAResponse(Categoria categoria) {

        return new CategoriaResponse(
            categoria.getIdCategoria(),
            categoria.getNombre(),
            categoria.getDescripcion()
        );
    }
}
