package pe.edu.upn.labuniversitario.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "laboratorios")
public class Laboratorio extends BaseEntity {

    @Column(nullable = false, unique = true, length = 30)
    private String codigo;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(nullable = false, length = 160)
    private String ubicacion;

    @Column(nullable = false)
    private int capacidad;

    @Column(nullable = false)
    private boolean activo = true;

    @Column(nullable = false)
    private boolean operativo = true;

    protected Laboratorio() {
    }

    public Laboratorio(String codigo, String nombre, String ubicacion, int capacidad) {
        this.codigo = codigo.toUpperCase();
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.capacidad = capacidad;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public boolean isActivo() {
        return activo;
    }

    public boolean isOperativo() {
        return operativo;
    }

    public void actualizar(String nombre, String ubicacion, int capacidad, boolean operativo) {
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.capacidad = capacidad;
        this.operativo = operativo;
    }

    public void desactivar() {
        this.activo = false;
    }
}
