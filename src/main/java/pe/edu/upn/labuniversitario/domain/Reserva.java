package pe.edu.upn.labuniversitario.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "reservas")
public class Reserva extends BaseEntity {

    @Column(nullable = false, unique = true, length = 20)
    private String codigo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "solicitante_id", nullable = false)
    private Usuario solicitante;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "laboratorio_id", nullable = false)
    private Laboratorio laboratorio;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private LocalTime horaInicio;

    @Column(nullable = false)
    private LocalTime horaFin;

    @Column(nullable = false)
    private int participantes;

    @Column(nullable = false, length = 240)
    private String actividad;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoReserva estado = EstadoReserva.PENDIENTE;

    @Column(length = 500)
    private String motivoDecision;

    @Column(length = 160)
    private String decididoPor;

    private Instant decididoEn;

    protected Reserva() {
    }

    public Reserva(
            String codigo,
            Usuario solicitante,
            Laboratorio laboratorio,
            LocalDate fecha,
            LocalTime horaInicio,
            LocalTime horaFin,
            int participantes,
            String actividad) {
        this.codigo = codigo;
        this.solicitante = solicitante;
        this.laboratorio = laboratorio;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.participantes = participantes;
        this.actividad = actividad;
    }

    public String getCodigo() {
        return codigo;
    }

    public Usuario getSolicitante() {
        return solicitante;
    }

    public Laboratorio getLaboratorio() {
        return laboratorio;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public int getParticipantes() {
        return participantes;
    }

    public String getActividad() {
        return actividad;
    }

    public EstadoReserva getEstado() {
        return estado;
    }

    public String getMotivoDecision() {
        return motivoDecision;
    }

    public String getDecididoPor() {
        return decididoPor;
    }

    public Instant getDecididoEn() {
        return decididoEn;
    }

    public void decidir(EstadoReserva nuevoEstado, String motivo, String actor) {
        this.estado = nuevoEstado;
        this.motivoDecision = motivo;
        this.decididoPor = actor;
        this.decididoEn = Instant.now();
    }

    public void cancelar(String actor) {
        this.estado = EstadoReserva.CANCELADA;
        this.motivoDecision = "Cancelada por el solicitante";
        this.decididoPor = actor;
        this.decididoEn = Instant.now();
    }
}
