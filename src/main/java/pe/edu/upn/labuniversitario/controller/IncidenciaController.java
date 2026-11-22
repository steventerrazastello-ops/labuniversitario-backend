package pe.edu.upn.labuniversitario.controller;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.upn.labuniversitario.dto.IncidenciaDtos;
import pe.edu.upn.labuniversitario.dto.PageResponse;
import pe.edu.upn.labuniversitario.service.IncidenciaService;

@RestController
@RequestMapping("/api/v1/incidencias")
public class IncidenciaController {

    private final IncidenciaService incidenciaService;

    public IncidenciaController(IncidenciaService incidenciaService) {
        this.incidenciaService = incidenciaService;
    }

    @PostMapping
    public ResponseEntity<IncidenciaDtos.Response> crear(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody IncidenciaDtos.CrearRequest request) {
        IncidenciaDtos.Response created = incidenciaService.crear(jwt.getSubject(), request);
        return ResponseEntity.created(URI.create("/api/v1/incidencias/" + created.id())).body(created);
    }

    @GetMapping("/mias")
    public List<IncidenciaDtos.Response> listarMias(@AuthenticationPrincipal Jwt jwt) {
        return incidenciaService.listarMias(jwt.getSubject());
    }

    @GetMapping
    public PageResponse<IncidenciaDtos.Response> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return incidenciaService.listar(page, size);
    }

    @PatchMapping("/{id}/estado")
    public IncidenciaDtos.Response actualizarEstado(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody IncidenciaDtos.ActualizarEstadoRequest request) {
        return incidenciaService.actualizarEstado(id, jwt.getSubject(), request);
    }
}
