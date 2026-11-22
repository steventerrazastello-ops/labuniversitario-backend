package pe.edu.upn.labuniversitario.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "incidencias")
public class Incidencia extends BaseEntity {

    @Column(nullable = false, unique = true, length = 20)
    private String codigo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "laboratorio_id", nullable = false)
    private Laboratorio laboratorio;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reportante_id", nullable = false)
    private Usuario reportante;

    @Column(nullable = false, length = 80)
    private String tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private PrioridadIncidencia prioridad;

    @Column(nullable = false, length = 1000)
    private String descripcion;

    @Column(nullable = false)
    private boolean bloqueaUso;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoIncidencia estado = EstadoIncidencia.ABIERTA;

    @Column(length = 1000)
    private String diagnostico;

    @Column(length = 1000)
    private String solucion;

    @Column(length = 160)
    private String atendidoPor;

    protected Incidencia() {
    }

    public Incidencia(
            String codigo,
            Laboratorio laboratorio,
            Usuario reportante,
            String tipo,
            PrioridadIncidencia prioridad,
            String descripcion,
            boolean bloqueaUso) {
        this.codigo = codigo;
        this.laboratorio = laboratorio;
        this.reportante = reportante;
        this.tipo = tipo;
        this.prioridad = prioridad;
        this.descripcion = descripcion;
        this.bloqueaUso = bloqueaUso;
    }

    public String getCodigo() {
        return codigo;
    }

    public Laboratorio getLaboratorio() {
        return laboratorio;
    }

    public Usuario getReportante() {
        return reportante;
    }

    public String getTipo() {
        return tipo;
    }

    public PrioridadIncidencia getPrioridad() {
        return prioridad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public boolean isBloqueaUso() {
        return bloqueaUso;
    }

    public EstadoIncidencia getEstado() {
        return estado;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public String getSolucion() {
        return solucion;
    }

    public String getAtendidoPor() {
        return atendidoPor;
    }

    public void actualizar(
            EstadoIncidencia estado,
            PrioridadIncidencia prioridad,
            String diagnostico,
            String solucion,
            String atendidoPor) {
        this.estado = estado;
        this.prioridad = prioridad;
        this.diagnostico = diagnostico;
        this.solucion = solucion;
        this.atendidoPor = atendidoPor;
    }
}
