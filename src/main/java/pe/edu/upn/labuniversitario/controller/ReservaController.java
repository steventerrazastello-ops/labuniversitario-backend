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
import pe.edu.upn.labuniversitario.dto.PageResponse;
import pe.edu.upn.labuniversitario.dto.ReservaDtos;
import pe.edu.upn.labuniversitario.service.ReservaService;

@RestController
@RequestMapping("/api/v1/reservas")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @PostMapping
    public ResponseEntity<ReservaDtos.Response> crear(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody ReservaDtos.CrearRequest request) {
        ReservaDtos.Response created = reservaService.crear(jwt.getSubject(), request);
        return ResponseEntity.created(URI.create("/api/v1/reservas/" + created.id())).body(created);
    }

    @GetMapping("/mias")
    public List<ReservaDtos.Response> listarMias(@AuthenticationPrincipal Jwt jwt) {
        return reservaService.listarMias(jwt.getSubject());
    }

    @GetMapping
    public PageResponse<ReservaDtos.Response> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return reservaService.listar(page, size);
    }

    @PatchMapping("/{id}/decision")
    public ReservaDtos.Response decidir(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody ReservaDtos.DecisionRequest request) {
        return reservaService.decidir(id, jwt.getSubject(), request);
    }

    @PatchMapping("/{id}/cancelacion")
    public ReservaDtos.Response cancelar(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {
        List<String> roles = jwt.getClaimAsStringList("roles");
        boolean admin = roles != null && roles.contains("ADMINISTRADOR");
        return reservaService.cancelar(id, jwt.getSubject(), admin);
    }
}
