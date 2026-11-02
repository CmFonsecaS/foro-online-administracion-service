package cl.duoc.administracion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ComentarioRequest(

        @NotBlank(message = "El contenido es obligatorio")
        String contenido,

        @NotNull(message = "El ID de la publicacion es obligatorio")
        Long idPublicacion,

        @NotNull(message = "El ID del usuario es obligatorio")
        Long idUsuario

) {
}