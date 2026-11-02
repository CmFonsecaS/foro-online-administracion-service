package cl.duoc.administracion.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.duoc.administracion.model.Comentario;

public interface ComentarioRepository extends JpaRepository<Comentario, Long> {

    List<Comentario> findByIdPublicacion(Long idPublicacion);

    List<Comentario> findByIdUsuario(Long idUsuario);

    List<Comentario> findByEstado(String estado);
}