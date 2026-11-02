package cl.duoc.administracion.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.duoc.administracion.dto.PublicacionRequest;
import cl.duoc.administracion.dto.PublicacionResponse;
import cl.duoc.administracion.model.Publicacion;
import cl.duoc.administracion.repository.CategoriaRepository;
import cl.duoc.administracion.repository.PublicacionRepository;

@Service
public class PublicacionService {

    private final PublicacionRepository publicacionRepository;
    private final CategoriaRepository categoriaRepository;

    // Inyeccion de dependencias
    public PublicacionService(
            PublicacionRepository publicacionRepository,
            CategoriaRepository categoriaRepository) {

        this.publicacionRepository = publicacionRepository;
        this.categoriaRepository = categoriaRepository;
    }

    // GET: Listar publicaciones
    @Transactional(readOnly = true)
    public List<PublicacionResponse> listarPublicaciones() {

        return publicacionRepository.findAll()
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    // GET: Obtener publicacion por ID
    @Transactional(readOnly = true)
    public PublicacionResponse obtenerPublicacionPorId(Long id) {

        Publicacion publicacion = buscarPublicacion(id);

        return convertirAResponse(publicacion);
    }

    // POST: Crear publicacion
    @Transactional
    public PublicacionResponse crearPublicacion(PublicacionRequest request) {

        validarCategoria(request.idCategoria());

        Publicacion publicacion = new Publicacion();

        publicacion.setTitulo(request.titulo());
        publicacion.setContenido(request.contenido());
        publicacion.setIdUsuario(request.idUsuario());
        publicacion.setIdCategoria(request.idCategoria());
        publicacion.setEstado("ACTIVO");

        Publicacion guardada = publicacionRepository.save(publicacion);

        return convertirAResponse(guardada);
    }

    // PUT: Actualizar publicacion
    @Transactional
    public PublicacionResponse actualizarPublicacion(
            Long id,
            PublicacionRequest request) {

        Publicacion publicacion = buscarPublicacion(id);

        validarCategoria(request.idCategoria());

        publicacion.setTitulo(request.titulo());
        publicacion.setContenido(request.contenido());
        publicacion.setIdUsuario(request.idUsuario());
        publicacion.setIdCategoria(request.idCategoria());

        Publicacion actualizada = publicacionRepository.save(publicacion);

        return convertirAResponse(actualizada);
    }

    // PATCH: Banear publicacion
    @Transactional
    public PublicacionResponse banearPublicacion(Long id) {

        Publicacion publicacion = buscarPublicacion(id);

        publicacion.setEstado("BANEADO");

        Publicacion actualizada = publicacionRepository.save(publicacion);

        return convertirAResponse(actualizada);
    }

    // PATCH: Reactivar publicacion
    @Transactional
    public PublicacionResponse reactivarPublicacion(Long id) {

        Publicacion publicacion = buscarPublicacion(id);

        publicacion.setEstado("ACTIVO");

        Publicacion actualizada = publicacionRepository.save(publicacion);

        return convertirAResponse(actualizada);
    }

    // DELETE: Eliminar publicacion
    @Transactional
    public void eliminarPublicacion(Long id) {

        Publicacion publicacion = buscarPublicacion(id);

        try {
            publicacionRepository.delete(publicacion);
            publicacionRepository.flush();

        } catch (DataIntegrityViolationException ex) {
            throw new IllegalStateException(
                "No se puede eliminar la publicacion porque tiene comentarios asociados",
                ex
            );
        }
    }

    // Buscar publicacion existente
    private Publicacion buscarPublicacion(Long id) {

        return publicacionRepository.findById(id)
                .orElseThrow(() ->
                    new NoSuchElementException(
                        "No existe una publicacion con ID: " + id
                    )
                );
    }

    // Validar existencia de categoria
    private void validarCategoria(Long idCategoria) {

        if (!categoriaRepository.existsById(idCategoria)) {
            throw new IllegalArgumentException(
                "No existe la categoria con ID: " + idCategoria
            );
        }
    }

    // Convertir entidad a DTO
    private PublicacionResponse convertirAResponse(Publicacion publicacion) {

        return new PublicacionResponse(
            publicacion.getIdPublicacion(),
            publicacion.getTitulo(),
            publicacion.getContenido(),
            publicacion.getIdUsuario(),
            publicacion.getIdCategoria(),
            publicacion.getEstado(),
            publicacion.getFechaCreacion()
        );
    }
}
