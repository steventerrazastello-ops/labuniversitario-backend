package pe.edu.upn.labuniversitario.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upn.labuniversitario.domain.EstadoIncidencia;
import pe.edu.upn.labuniversitario.domain.EstadoReserva;
import pe.edu.upn.labuniversitario.domain.Laboratorio;
import pe.edu.upn.labuniversitario.dto.LaboratorioDtos;
import pe.edu.upn.labuniversitario.exception.ApiException;
import pe.edu.upn.labuniversitario.repository.IncidenciaRepository;
import pe.edu.upn.labuniversitario.repository.LaboratorioRepository;
import pe.edu.upn.labuniversitario.repository.ReservaRepository;

@Service
public class LaboratorioService {

    private final LaboratorioRepository laboratorioRepository;
    private final ReservaRepository reservaRepository;
    private final IncidenciaRepository incidenciaRepository;

    public LaboratorioService(
            LaboratorioRepository laboratorioRepository,
            ReservaRepository reservaRepository,
            IncidenciaRepository incidenciaRepository) {
        this.laboratorioRepository = laboratorioRepository;
        this.reservaRepository = reservaRepository;
        this.incidenciaRepository = incidenciaRepository;
    }

    @Transactional(readOnly = true)
    public List<LaboratorioDtos.Response> listarActivos() {
        return laboratorioRepository.findByActivoTrueOrderByCodigo().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public LaboratorioDtos.Response obtener(Long id) {
        return toResponse(requireActive(id));
    }

    @Transactional
    public LaboratorioDtos.Response crear(LaboratorioDtos.CrearRequest request) {
        if (laboratorioRepository.existsByCodigoIgnoreCase(request.codigo())) {
            throw ApiException.conflict("LAB_CODE_EXISTS", "Ya existe un laboratorio con ese código.");
        }
        Laboratorio laboratorio = new Laboratorio(
                request.codigo(), request.nombre(), request.ubicacion(), request.capacidad());
        return toResponse(laboratorioRepository.save(laboratorio));
    }

    @Transactional
    public LaboratorioDtos.Response actualizar(Long id, LaboratorioDtos.ActualizarRequest request) {
        Laboratorio laboratorio = requireActive(id);
        laboratorio.actualizar(
                request.nombre(), request.ubicacion(), request.capacidad(), request.operativo());
        return toResponse(laboratorio);
    }

    @Transactional
    public void desactivar(Long id) {
        requireActive(id).desactivar();
    }

    @Transactional(readOnly = true)
    public LaboratorioDtos.DisponibilidadResponse consultarDisponibilidad(
            Long id,
            LocalDate fecha,
            LocalTime horaInicio,
            LocalTime horaFin,
            int participantes) {
        Laboratorio laboratorio = requireActive(id);
        validateInterval(fecha, horaInicio, horaFin);
        List<String> motivos = new ArrayList<>();
        if (!laboratorio.isOperativo()) {
            motivos.add("El laboratorio no se encuentra operativo.");
        }
        if (participantes > laboratorio.getCapacidad()) {
            motivos.add("La cantidad de participantes supera la capacidad.");
        }
        long conflictos = reservaRepository.contarConflictos(
                id,
                fecha,
                horaInicio,
                horaFin,
                EnumSet.of(EstadoReserva.PENDIENTE, EstadoReserva.APROBADA));
        if (conflictos > 0) {
            motivos.add("Existe una reserva superpuesta.");
        }
        boolean incidenciaBloqueante = incidenciaRepository
                .existsByLaboratorioIdAndBloqueaUsoTrueAndEstadoIn(
                        id,
                        EnumSet.of(EstadoIncidencia.ABIERTA, EstadoIncidencia.EN_ATENCION));
        if (incidenciaBloqueante) {
            motivos.add("Existe una incidencia que bloquea el uso.");
        }
        return new LaboratorioDtos.DisponibilidadResponse(
                id, fecha, horaInicio, horaFin, motivos.isEmpty(), List.copyOf(motivos));
    }

    Laboratorio requireActive(Long id) {
        return laboratorioRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> ApiException.notFound(
                        "LAB_NOT_FOUND", "El laboratorio no existe o está desactivado."));
    }

    void validateInterval(LocalDate fecha, LocalTime horaInicio, LocalTime horaFin) {
        if (fecha.isBefore(LocalDate.now())) {
            throw ApiException.badRequest("PAST_DATE", "La fecha no puede estar en el pasado.");
        }
        if (!horaFin.isAfter(horaInicio)) {
            throw ApiException.badRequest("INVALID_TIME_RANGE", "La hora final debe ser posterior a la inicial.");
        }
    }

    private LaboratorioDtos.Response toResponse(Laboratorio laboratorio) {
        return new LaboratorioDtos.Response(
                laboratorio.getId(),
                laboratorio.getCodigo(),
                laboratorio.getNombre(),
                laboratorio.getUbicacion(),
                laboratorio.getCapacidad(),
                laboratorio.isActivo(),
                laboratorio.isOperativo());
    }
}
