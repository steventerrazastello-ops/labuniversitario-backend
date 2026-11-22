package pe.edu.upn.labuniversitario.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.upn.labuniversitario.domain.EstadoReserva;
import pe.edu.upn.labuniversitario.domain.Reserva;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    @Query("""
            select count(r)
            from Reserva r
            where r.laboratorio.id = :laboratorioId
              and r.fecha = :fecha
              and r.estado in :estados
              and :horaInicio < r.horaFin
              and :horaFin > r.horaInicio
            """)
    long contarConflictos(
            @Param("laboratorioId") Long laboratorioId,
            @Param("fecha") LocalDate fecha,
            @Param("horaInicio") LocalTime horaInicio,
            @Param("horaFin") LocalTime horaFin,
            @Param("estados") Collection<EstadoReserva> estados);

    List<Reserva> findBySolicitanteIdOrderByCreadoEnDesc(Long solicitanteId);

    Page<Reserva> findAllByOrderByCreadoEnDesc(Pageable pageable);
}
