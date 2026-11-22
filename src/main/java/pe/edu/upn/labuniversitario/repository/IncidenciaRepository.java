package pe.edu.upn.labuniversitario.repository;

import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upn.labuniversitario.domain.EstadoIncidencia;
import pe.edu.upn.labuniversitario.domain.Incidencia;

public interface IncidenciaRepository extends JpaRepository<Incidencia, Long> {

    boolean existsByLaboratorioIdAndBloqueaUsoTrueAndEstadoIn(
            Long laboratorioId,
            Collection<EstadoIncidencia> estados);

    List<Incidencia> findByReportanteIdOrderByCreadoEnDesc(Long reportanteId);

    Page<Incidencia> findAllByOrderByCreadoEnDesc(Pageable pageable);
}
