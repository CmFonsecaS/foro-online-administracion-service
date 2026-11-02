package cl.duoc.administracion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record PublicacionRequest(

    @NotBlank(message = "El titulo es obligatorio")
    @Size(max = 200, message = "El titulo no puede superar los 200 caracteres")
    String titulo,

    @NotBlank(message = "El contenido es obligatorio")
    String contenido,

    @NotNull(message = "El usuario es obligatorio")
    @Positive(message = "El ID del usuario debe ser positivo")
    Long idUsuario,

    @NotNull(message = "La categoria es obligatoria")
    @Positive(message = "El ID de la categoria debe ser positivo")
    Long idCategoria

) {
}