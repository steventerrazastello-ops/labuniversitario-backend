package pe.edu.upn.labuniversitario.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.upn.labuniversitario.dto.LaboratorioDtos;
import pe.edu.upn.labuniversitario.service.LaboratorioService;

@Validated
@RestController
@RequestMapping("/api/v1/laboratorios")
public class LaboratorioController {

    private final LaboratorioService laboratorioService;

    public LaboratorioController(LaboratorioService laboratorioService) {
        this.laboratorioService = laboratorioService;
    }

    @GetMapping
    public List<LaboratorioDtos.Response> listar() {
        return laboratorioService.listarActivos();
    }

    @GetMapping("/{id}")
    public LaboratorioDtos.Response obtener(@PathVariable Long id) {
        return laboratorioService.obtener(id);
    }

    @PostMapping
    public ResponseEntity<LaboratorioDtos.Response> crear(
            @Valid @RequestBody LaboratorioDtos.CrearRequest request) {
        LaboratorioDtos.Response created = laboratorioService.crear(request);
        return ResponseEntity.created(URI.create("/api/v1/laboratorios/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    public LaboratorioDtos.Response actualizar(
            @PathVariable Long id,
            @Valid @RequestBody LaboratorioDtos.ActualizarRequest request) {
        return laboratorioService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        laboratorioService.desactivar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/disponibilidad")
    public LaboratorioDtos.DisponibilidadResponse disponibilidad(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime horaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime horaFin,
            @RequestParam(defaultValue = "1") @Min(1) @Max(200) int participantes) {
        return laboratorioService.consultarDisponibilidad(
                id, fecha, horaInicio, horaFin, participantes);
    }
}
