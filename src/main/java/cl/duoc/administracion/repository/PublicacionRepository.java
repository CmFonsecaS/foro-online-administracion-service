package cl.duoc.administracion.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.duoc.administracion.model.Publicacion;

public interface PublicacionRepository extends JpaRepository<Publicacion, Long> {

    List<Publicacion> findByIdCategoria(Long idCategoria);

    List<Publicacion> findByIdUsuario(Long idUsuario);

    List<Publicacion> findByEstado(String estado);
}