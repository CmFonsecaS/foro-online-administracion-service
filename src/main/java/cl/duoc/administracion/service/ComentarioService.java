package cl.duoc.administracion.service;

import java.util.List;

import cl.duoc.administracion.dto.ComentarioRequest;
import cl.duoc.administracion.dto.ComentarioResponse;

/**
 * Patron Service Layer: contrato de la logica de negocio.
 */
public interface ComentarioService {

    List<ComentarioResponse> listar();

    ComentarioResponse buscarPorId(Long id);

    ComentarioResponse crear(ComentarioRequest request);

    ComentarioResponse actualizar(Long id, ComentarioRequest request);

    void eliminar(Long id);

    ComentarioResponse banearComentario(Long id);

    ComentarioResponse reactivarComentario(Long id);
}
