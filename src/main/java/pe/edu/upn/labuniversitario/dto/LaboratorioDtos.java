package pe.edu.upn.labuniversitario.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public final class LaboratorioDtos {

    private LaboratorioDtos() {
    }

    public record CrearRequest(
            @NotBlank @Size(max = 30) String codigo,
            @NotBlank @Size(max = 120) String nombre,
            @NotBlank @Size(max = 160) String ubicacion,
            @Min(1) @Max(200) int capacidad) {
    }

    public record ActualizarRequest(
            @NotBlank @Size(max = 120) String nombre,
            @NotBlank @Size(max = 160) String ubicacion,
            @Min(1) @Max(200) int capacidad,
            boolean operativo) {
    }

    public record Response(
            Long id,
            String codigo,
            String nombre,
            String ubicacion,
            int capacidad,
            boolean activo,
            boolean operativo) {
    }

    public record DisponibilidadRequest(
            @NotNull LocalDate fecha,
            @NotNull LocalTime horaInicio,
            @NotNull LocalTime horaFin,
            @Min(1) @Max(200) int participantes) {
    }

    public record DisponibilidadResponse(
            Long laboratorioId,
            LocalDate fecha,
            LocalTime horaInicio,
            LocalTime horaFin,
            boolean disponible,
            List<String> motivos) {
    }
}
