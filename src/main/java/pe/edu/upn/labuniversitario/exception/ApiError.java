package pe.edu.upn.labuniversitario.exception;

import java.time.Instant;
import java.util.Map;

public record ApiError(
        Instant timestamp,
        int status,
        String code,
        String message,
        String path,
        Map<String, String> fieldErrors) {

    public static ApiError simple(int status, String code, String message, String path) {
        return new ApiError(Instant.now(), status, code, message, path, Map.of());
    }
}
