package cl.duoc.administracion.dto;

import java.time.LocalDateTime;

public record ComentarioResponse(

        Long idComentario,
        String contenido,
        Long idPublicacion,
        Long idUsuario,
        String estado,
        LocalDateTime fechaCreacion

) {
}
