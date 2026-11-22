package pe.edu.upn.labuniversitario.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upn.labuniversitario.domain.Laboratorio;

public interface LaboratorioRepository extends JpaRepository<Laboratorio, Long> {

    List<Laboratorio> findByActivoTrueOrderByCodigo();

    Optional<Laboratorio> findByIdAndActivoTrue(Long id);

    boolean existsByCodigoIgnoreCase(String codigo);
}
