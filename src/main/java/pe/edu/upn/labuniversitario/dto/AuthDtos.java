package pe.edu.upn.labuniversitario.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import pe.edu.upn.labuniversitario.domain.Rol;

public final class AuthDtos {

    private AuthDtos() {
    }

    public record LoginRequest(
            @NotBlank @Email String email,
            @NotBlank String password) {
    }

    public record LoginResponse(
            String accessToken,
            String tokenType,
            Instant expiresAt,
            Long usuarioId,
            String nombres,
            String email,
            Rol rol) {
    }
}
