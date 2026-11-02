package cl.duoc.administracion.dto;

import java.time.LocalDateTime;

public record PublicacionResponse(

    Long idPublicacion,
    String titulo,
    String contenido,
    Long idUsuario,
    Long idCategoria,
    String estado,
    LocalDateTime fechaCreacion

) {
}