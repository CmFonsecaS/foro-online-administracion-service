package cl.duoc.administracion.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.duoc.administracion.dto.ComentarioRequest;
import cl.duoc.administracion.dto.ComentarioResponse;
import cl.duoc.administracion.model.Comentario;
import cl.duoc.administracion.repository.ComentarioRepository;
import cl.duoc.administracion.repository.PublicacionRepository;

@Service
public class ComentarioService {

    private final ComentarioRepository comentarioRepository;
    private final PublicacionRepository publicacionRepository;

    public ComentarioService(
            ComentarioRepository comentarioRepository,
            PublicacionRepository publicacionRepository) {

        this.comentarioRepository = comentarioRepository;
        this.publicacionRepository = publicacionRepository;
    }

    // Listar comentarios
    @Transactional(readOnly = true)
    public List<ComentarioResponse> listar() {

        return comentarioRepository.findAll()
                .stream()
                .map(this::convertirRespuesta)
                .toList();
    }

    // Buscar comentario por ID
    @Transactional(readOnly = true)
    public ComentarioResponse buscarPorId(Long id) {

        Comentario comentario = obtenerComentario(id);

        return convertirRespuesta(comentario);
    }

    // Crear comentario
    @Transactional
    public ComentarioResponse crear(ComentarioRequest request) {

        validarPublicacion(request.idPublicacion());

        Comentario comentario = new Comentario(
                request.contenido(),
                request.idPublicacion(),
                request.idUsuario()
        );

        Comentario guardado = comentarioRepository.save(comentario);

        return convertirRespuesta(guardado);
    }

    // Actualizar comentario
    @Transactional
    public ComentarioResponse actualizar(
            Long id,
            ComentarioRequest request) {

        Comentario comentario = obtenerComentario(id);

        validarPublicacion(request.idPublicacion());

        comentario.setContenido(request.contenido());
        comentario.setIdPublicacion(request.idPublicacion());
        comentario.setIdUsuario(request.idUsuario());

        Comentario guardado = comentarioRepository.save(comentario);

        return convertirRespuesta(guardado);
    }

    // Eliminar comentario
    @Transactional
    public void eliminar(Long id) {

        Comentario comentario = obtenerComentario(id);

        comentarioRepository.delete(comentario);
    }

    // Banear comentario
    @Transactional
    public ComentarioResponse banearComentario(Long id) {

        Comentario comentario = obtenerComentario(id);

        comentario.setEstado("BANEADO");

        Comentario guardado = comentarioRepository.save(comentario);

        return convertirRespuesta(guardado);
    }

    // Reactivar comentario
    @Transactional
    public ComentarioResponse reactivarComentario(Long id) {

        Comentario comentario = obtenerComentario(id);

        comentario.setEstado("ACTIVO");

        Comentario guardado = comentarioRepository.save(comentario);

        return convertirRespuesta(guardado);
    }

    // Obtener comentario o lanzar excepcion
    private Comentario obtenerComentario(Long id) {

        return comentarioRepository.findById(id)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Comentario no encontrado: " + id
                        )
                );
    }

    // Validar existencia de publicacion
    private void validarPublicacion(Long idPublicacion) {

        if (!publicacionRepository.existsById(idPublicacion)) {
            throw new NoSuchElementException(
                    "La publicacion no existe: " + idPublicacion
            );
        }
    }

    // Convertir entidad a DTO
    private ComentarioResponse convertirRespuesta(Comentario comentario) {

        return new ComentarioResponse(
                comentario.getIdComentario(),
                comentario.getContenido(),
                comentario.getIdPublicacion(),
                comentario.getIdUsuario(),
                comentario.getEstado(),
                comentario.getFechaCreacion()
        );
    }
}
