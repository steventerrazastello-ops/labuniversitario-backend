package pe.edu.upn.labuniversitario.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import pe.edu.upn.labuniversitario.domain.Laboratorio;
import pe.edu.upn.labuniversitario.domain.Rol;
import pe.edu.upn.labuniversitario.domain.Usuario;
import pe.edu.upn.labuniversitario.repository.LaboratorioRepository;
import pe.edu.upn.labuniversitario.repository.UsuarioRepository;

@Configuration
@Profile({"local", "test"})
public class DataSeeder {

    @Bean
    CommandLineRunner seedDemoData(
            UsuarioRepository usuarioRepository,
            LaboratorioRepository laboratorioRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            if (usuarioRepository.count() == 0) {
                String password = passwordEncoder.encode("Clave123!");
                usuarioRepository.save(new Usuario(
                        "Ana Solicitante", "solicitante@lab.edu.pe", password, Rol.SOLICITANTE));
                usuarioRepository.save(new Usuario(
                        "Carlos Coordinador", "coordinador@lab.edu.pe", password, Rol.COORDINADOR));
                usuarioRepository.save(new Usuario(
                        "Teresa Técnica", "tecnico@lab.edu.pe", password, Rol.TECNICO));
                usuarioRepository.save(new Usuario(
                        "Adriana Administradora", "admin@lab.edu.pe", password, Rol.ADMINISTRADOR));
            }
            if (laboratorioRepository.count() == 0) {
                laboratorioRepository.save(new Laboratorio(
                        "LAB-A101", "Laboratorio de Desarrollo", "Pabellón A, piso 1", 30));
                laboratorioRepository.save(new Laboratorio(
                        "LAB-B202", "Laboratorio de Redes", "Pabellón B, piso 2", 24));
                laboratorioRepository.save(new Laboratorio(
                        "LAB-C303", "Laboratorio de Datos", "Pabellón C, piso 3", 36));
            }
        };
    }
}
