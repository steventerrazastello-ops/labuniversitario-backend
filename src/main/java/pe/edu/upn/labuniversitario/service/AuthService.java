package pe.edu.upn.labuniversitario.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upn.labuniversitario.domain.Usuario;
import pe.edu.upn.labuniversitario.dto.AuthDtos;
import pe.edu.upn.labuniversitario.exception.ApiException;
import pe.edu.upn.labuniversitario.repository.UsuarioRepository;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final TokenService tokenService;

    public AuthService(
            AuthenticationManager authenticationManager,
            UsuarioRepository usuarioRepository,
            TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
        this.tokenService = tokenService;
    }

    @Transactional(readOnly = true)
    public AuthDtos.LoginResponse login(AuthDtos.LoginRequest request) {
        authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(request.email(), request.password()));
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> ApiException.notFound("USER_NOT_FOUND", "El usuario no existe."));
        TokenService.GeneratedToken token = tokenService.generate(usuario);
        return new AuthDtos.LoginResponse(
                token.value(),
                "Bearer",
                token.expiresAt(),
                usuario.getId(),
                usuario.getNombres(),
                usuario.getEmail(),
                usuario.getRol());
    }
}
