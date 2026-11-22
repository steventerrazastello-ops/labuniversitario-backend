package pe.edu.upn.labuniversitario.service;

import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upn.labuniversitario.domain.EstadoIncidencia;
import pe.edu.upn.labuniversitario.domain.Incidencia;
import pe.edu.upn.labuniversitario.domain.Laboratorio;
import pe.edu.upn.labuniversitario.domain.Usuario;
import pe.edu.upn.labuniversitario.dto.IncidenciaDtos;
import pe.edu.upn.labuniversitario.dto.PageResponse;
import pe.edu.upn.labuniversitario.exception.ApiException;
import pe.edu.upn.labuniversitario.repository.IncidenciaRepository;
import pe.edu.upn.labuniversitario.repository.UsuarioRepository;

@Service
public class IncidenciaService {

    private final IncidenciaRepository incidenciaRepository;
    private final UsuarioRepository usuarioRepository;
    private final LaboratorioService laboratorioService;

    public IncidenciaService(
            IncidenciaRepository incidenciaRepository,
            UsuarioRepository usuarioRepository,
            LaboratorioService laboratorioService) {
        this.incidenciaRepository = incidenciaRepository;
        this.usuarioRepository = usuarioRepository;
        this.laboratorioService = laboratorioService;
    }

    @Transactional
    public IncidenciaDtos.Response crear(String email, IncidenciaDtos.CrearRequest request) {
        Usuario reportante = requireUser(email);
        Laboratorio laboratorio = laboratorioService.requireActive(request.laboratorioId());
        Incidencia incidencia = new Incidencia(
                nextCode(),
                laboratorio,
                reportante,
                request.tipo(),
                request.prioridad(),
                request.descripcion(),
                request.bloqueaUso());
        return toResponse(incidenciaRepository.save(incidencia));
    }

    @Transactional(readOnly = true)
    public List<IncidenciaDtos.Response> listarMias(String email) {
        Usuario usuario = requireUser(email);
        return incidenciaRepository.findByReportanteIdOrderByCreadoEnDesc(usuario.getId()).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<IncidenciaDtos.Response> listar(int page, int size) {
        Page<IncidenciaDtos.Response> result = incidenciaRepository
                .findAllByOrderByCreadoEnDesc(PageRequest.of(page, Math.min(size, 100)))
                .map(this::toResponse);
        return PageResponse.from(result);
    }

    @Transactional
    public IncidenciaDtos.Response actualizarEstado(
            Long id,
            String actor,
            IncidenciaDtos.ActualizarEstadoRequest request) {
        Incidencia incidencia = incidenciaRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound(
                        "INCIDENCIA_NOT_FOUND", "La incidencia no existe."));
        validateTransition(incidencia.getEstado(), request.estado(), request.diagnostico(), request.solucion());
        incidencia.actualizar(
                request.estado(),
                request.prioridad(),
                request.diagnostico(),
                request.solucion(),
                actor);
        return toResponse(incidencia);
    }

    private void validateTransition(
            EstadoIncidencia current,
            EstadoIncidencia target,
            String diagnostico,
            String solucion) {
        if (current == EstadoIncidencia.CERRADA) {
            throw ApiException.conflict("INCIDENCIA_CLOSED", "Una incidencia cerrada no puede modificarse.");
        }
        if ((target == EstadoIncidencia.EN_ATENCION || target == EstadoIncidencia.RESUELTA)
                && (diagnostico == null || diagnostico.isBlank())) {
            throw ApiException.badRequest(
                    "DIAGNOSIS_REQUIRED", "El diagnóstico es obligatorio al atender o resolver.");
        }
        if ((target == EstadoIncidencia.RESUELTA || target == EstadoIncidencia.CERRADA)
                && (solucion == null || solucion.isBlank())) {
            throw ApiException.badRequest(
                    "SOLUTION_REQUIRED", "La solución es obligatoria al resolver o cerrar.");
        }
    }

    private Usuario requireUser(String email) {
        return usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> ApiException.notFound("USER_NOT_FOUND", "El usuario no existe."));
    }

    private String nextCode() {
        return "INC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT);
    }

    private IncidenciaDtos.Response toResponse(Incidencia incidencia) {
        return new IncidenciaDtos.Response(
                incidencia.getId(),
                incidencia.getCodigo(),
                incidencia.getLaboratorio().getId(),
                incidencia.getLaboratorio().getNombre(),
                incidencia.getReportante().getId(),
                incidencia.getReportante().getNombres(),
                incidencia.getTipo(),
                incidencia.getPrioridad(),
                incidencia.getDescripcion(),
                incidencia.isBloqueaUso(),
                incidencia.getEstado(),
                incidencia.getDiagnostico(),
                incidencia.getSolucion(),
                incidencia.getAtendidoPor(),
                incidencia.getCreadoEn(),
                incidencia.getActualizadoEn());
    }
}
