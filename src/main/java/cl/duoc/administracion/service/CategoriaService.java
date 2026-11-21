package cl.duoc.administracion.service;

import java.util.List;

import cl.duoc.administracion.dto.CategoriaRequest;
import cl.duoc.administracion.dto.CategoriaResponse;

/**
 * Patron Service Layer: contrato de la logica de negocio.
 */
public interface CategoriaService {

    List<CategoriaResponse> listarCategorias();

    CategoriaResponse obtenerCategoriaPorId(Long id);

    CategoriaResponse crearCategoria(CategoriaRequest request);

    CategoriaResponse actualizarCategoria(Long id, CategoriaRequest request);

    void eliminarCategoria(Long id);
}
