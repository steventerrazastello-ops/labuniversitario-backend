package pe.edu.upn.labuniversitario.service;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upn.labuniversitario.domain.EstadoReserva;
import pe.edu.upn.labuniversitario.domain.Laboratorio;
import pe.edu.upn.labuniversitario.domain.Reserva;
import pe.edu.upn.labuniversitario.domain.Usuario;
import pe.edu.upn.labuniversitario.dto.PageResponse;
import pe.edu.upn.labuniversitario.dto.ReservaDtos;
import pe.edu.upn.labuniversitario.exception.ApiException;
import pe.edu.upn.labuniversitario.repository.ReservaRepository;
import pe.edu.upn.labuniversitario.repository.UsuarioRepository;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;
    private final LaboratorioService laboratorioService;

    public ReservaService(
            ReservaRepository reservaRepository,
            UsuarioRepository usuarioRepository,
            LaboratorioService laboratorioService) {
        this.reservaRepository = reservaRepository;
        this.usuarioRepository = usuarioRepository;
        this.laboratorioService = laboratorioService;
    }

    @Transactional
    public ReservaDtos.Response crear(String email, ReservaDtos.CrearRequest request) {
        Usuario usuario = requireUser(email);
        Laboratorio laboratorio = laboratorioService.requireActive(request.laboratorioId());
        laboratorioService.validateInterval(request.fecha(), request.horaInicio(), request.horaFin());
        if (!laboratorio.isOperativo()) {
            throw ApiException.conflict("LAB_NOT_OPERATIONAL", "El laboratorio no está operativo.");
        }
        if (request.participantes() > laboratorio.getCapacidad()) {
            throw ApiException.badRequest("CAPACITY_EXCEEDED", "Los participantes superan la capacidad.");
        }
        long conflicts = reservaRepository.contarConflictos(
                laboratorio.getId(),
                request.fecha(),
                request.horaInicio(),
                request.horaFin(),
                EnumSet.of(EstadoReserva.PENDIENTE, EstadoReserva.APROBADA));
        if (conflicts > 0) {
            throw ApiException.conflict("RESERVA_HORARIO_OCUPADO", "El horario solicitado ya está ocupado.");
        }
        Reserva reserva = new Reserva(
                nextCode("RES"),
                usuario,
                laboratorio,
                request.fecha(),
                request.horaInicio(),
                request.horaFin(),
                request.participantes(),
                request.actividad());
        return toResponse(reservaRepository.save(reserva));
    }

    @Transactional(readOnly = true)
    public List<ReservaDtos.Response> listarMias(String email) {
        Usuario usuario = requireUser(email);
        return reservaRepository.findBySolicitanteIdOrderByCreadoEnDesc(usuario.getId()).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<ReservaDtos.Response> listar(int page, int size) {
        Page<ReservaDtos.Response> result = reservaRepository
                .findAllByOrderByCreadoEnDesc(PageRequest.of(page, Math.min(size, 100)))
                .map(this::toResponse);
        return PageResponse.from(result);
    }

    @Transactional
    public ReservaDtos.Response decidir(
            Long id,
            String actor,
            ReservaDtos.DecisionRequest request) {
        Reserva reserva = require(id);
        if (reserva.getEstado() != EstadoReserva.PENDIENTE) {
            throw ApiException.conflict(
                    "RESERVA_NOT_PENDING", "Solo se puede decidir una reserva pendiente.");
        }
        if (request.estado() != EstadoReserva.APROBADA && request.estado() != EstadoReserva.RECHAZADA) {
            throw ApiException.badRequest(
                    "INVALID_DECISION", "La decisión debe ser APROBADA o RECHAZADA.");
        }
        if (request.estado() == EstadoReserva.RECHAZADA
                && (request.motivo() == null || request.motivo().isBlank())) {
            throw ApiException.badRequest(
                    "REJECTION_REASON_REQUIRED", "El rechazo requiere un motivo.");
        }
        reserva.decidir(request.estado(), request.motivo(), actor);
        return toResponse(reserva);
    }

    @Transactional
    public ReservaDtos.Response cancelar(Long id, String email, boolean administrador) {
        Reserva reserva = require(id);
        if (!administrador && !reserva.getSolicitante().getEmail().equalsIgnoreCase(email)) {
            throw ApiException.forbidden("NOT_RESERVATION_OWNER", "Solo el propietario puede cancelar la reserva.");
        }
        if (reserva.getEstado() != EstadoReserva.PENDIENTE
                && reserva.getEstado() != EstadoReserva.APROBADA) {
            throw ApiException.conflict("RESERVA_NOT_CANCELLABLE", "La reserva ya no se puede cancelar.");
        }
        if (!reserva.getFecha().isAfter(LocalDate.now())) {
            throw ApiException.conflict("RESERVA_ALREADY_STARTED", "Solo se cancelan reservas futuras.");
        }
        reserva.cancelar(email);
        return toResponse(reserva);
    }

    private Reserva require(Long id) {
        return reservaRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("RESERVA_NOT_FOUND", "La reserva no existe."));
    }

    private Usuario requireUser(String email) {
        return usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> ApiException.notFound("USER_NOT_FOUND", "El usuario no existe."));
    }

    private String nextCode(String prefix) {
        return prefix + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT);
    }

    private ReservaDtos.Response toResponse(Reserva reserva) {
        return new ReservaDtos.Response(
                reserva.getId(),
                reserva.getCodigo(),
                reserva.getSolicitante().getId(),
                reserva.getSolicitante().getNombres(),
                reserva.getLaboratorio().getId(),
                reserva.getLaboratorio().getNombre(),
                reserva.getFecha(),
                reserva.getHoraInicio(),
                reserva.getHoraFin(),
                reserva.getParticipantes(),
                reserva.getActividad(),
                reserva.getEstado(),
                reserva.getMotivoDecision(),
                reserva.getDecididoPor(),
                reserva.getCreadoEn(),
                reserva.getDecididoEn());
    }
}
