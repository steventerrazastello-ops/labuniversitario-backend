package pe.edu.upn.labuniversitario.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import pe.edu.upn.labuniversitario.domain.EstadoReserva;

public final class ReservaDtos {

    private ReservaDtos() {
    }

    public record CrearRequest(
            @NotNull Long laboratorioId,
            @NotNull @FutureOrPresent LocalDate fecha,
            @NotNull LocalTime horaInicio,
            @NotNull LocalTime horaFin,
            @Min(1) @Max(200) int participantes,
            @NotBlank @Size(max = 240) String actividad) {
    }

    public record DecisionRequest(
            @NotNull EstadoReserva estado,
            @Size(max = 500) String motivo) {
    }

    public record Response(
            Long id,
            String codigo,
            Long solicitanteId,
            String solicitante,
            Long laboratorioId,
            String laboratorio,
            LocalDate fecha,
            LocalTime horaInicio,
            LocalTime horaFin,
            int participantes,
            String actividad,
            EstadoReserva estado,
            String motivoDecision,
            String decididoPor,
            Instant creadoEn,
            Instant decididoEn) {
    }
}
