package pe.edu.upn.labuniversitario.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import pe.edu.upn.labuniversitario.domain.EstadoIncidencia;
import pe.edu.upn.labuniversitario.domain.PrioridadIncidencia;

public final class IncidenciaDtos {

    private IncidenciaDtos() {
    }

    public record CrearRequest(
            @NotNull Long laboratorioId,
            @NotBlank @Size(max = 80) String tipo,
            @NotNull PrioridadIncidencia prioridad,
            @NotBlank @Size(max = 1000) String descripcion,
            boolean bloqueaUso) {
    }

    public record ActualizarEstadoRequest(
            @NotNull EstadoIncidencia estado,
            @NotNull PrioridadIncidencia prioridad,
            @Size(max = 1000) String diagnostico,
            @Size(max = 1000) String solucion) {
    }

    public record Response(
            Long id,
            String codigo,
            Long laboratorioId,
            String laboratorio,
            Long reportanteId,
            String reportante,
            String tipo,
            PrioridadIncidencia prioridad,
            String descripcion,
            boolean bloqueaUso,
            EstadoIncidencia estado,
            String diagnostico,
            String solucion,
            String atendidoPor,
            Instant creadoEn,
            Instant actualizadoEn) {
    }
}
