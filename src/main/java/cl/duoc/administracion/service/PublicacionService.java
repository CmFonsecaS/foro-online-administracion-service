package cl.duoc.administracion.service;

import java.util.List;

import cl.duoc.administracion.dto.PublicacionRequest;
import cl.duoc.administracion.dto.PublicacionResponse;

/**
 * Patron Service Layer: contrato de la logica de negocio.
 */
public interface PublicacionService {

    List<PublicacionResponse> listarPublicaciones();

    PublicacionResponse obtenerPublicacionPorId(Long id);

    PublicacionResponse crearPublicacion(PublicacionRequest request);

    PublicacionResponse actualizarPublicacion(Long id, PublicacionRequest request);

    PublicacionResponse banearPublicacion(Long id);

    PublicacionResponse reactivarPublicacion(Long id);

    void eliminarPublicacion(Long id);
}
