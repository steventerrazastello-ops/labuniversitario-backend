# Contrato REST de LabUniversitario

Base URL local: `http://localhost:8080/api/v1`

Salvo `POST /auth/login`, todos los endpoints requieren `Authorization: Bearer <token>`.

## Autenticación

| Método | Ruta | Rol | Entrada | Respuesta exitosa |
|---|---|---|---|---|
| POST | `/auth/login` | Público | `email`, `password` | 200 con JWT, vencimiento y perfil |

## Laboratorios

| Método | Ruta | Rol | Entrada | Respuesta exitosa |
|---|---|---|---|---|
| GET | `/laboratorios` | Autenticado | — | 200, lista de laboratorios activos |
| GET | `/laboratorios/{id}` | Autenticado | `id` de ruta | 200, detalle |
| GET | `/laboratorios/{id}/disponibilidad` | Autenticado | `fecha`, `horaInicio`, `horaFin`, `participantes` | 200, disponibilidad y motivos |
| POST | `/laboratorios` | Administrador | `codigo`, `nombre`, `ubicacion`, `capacidad` | 201 y cabecera `Location` |
| PUT | `/laboratorios/{id}` | Administrador | `nombre`, `ubicacion`, `capacidad`, `operativo` | 200, laboratorio actualizado |
| DELETE | `/laboratorios/{id}` | Administrador | `id` de ruta | 204; baja lógica |

## Reservas

| Método | Ruta | Rol | Entrada | Respuesta exitosa |
|---|---|---|---|---|
| POST | `/reservas` | Autenticado | laboratorio, fecha, horario, participantes y actividad | 201, reserva pendiente |
| GET | `/reservas/mias` | Autenticado | — | 200, reservas del usuario |
| GET | `/reservas?page=0&size=20` | Coordinador/Administrador | paginación | 200, `contenido` y metadatos |
| PATCH | `/reservas/{id}/decision` | Coordinador/Administrador | `estado` APROBADA/RECHAZADA y `motivo` | 200, reserva decidida |
| PATCH | `/reservas/{id}/cancelacion` | Propietario/Administrador | `id` de ruta | 200, reserva cancelada |

Reglas principales: fecha presente o futura, hora final posterior a la inicial, capacidad suficiente, laboratorio activo y operativo, y ausencia de superposición con reservas pendientes o aprobadas.

## Incidencias

| Método | Ruta | Rol | Entrada | Respuesta exitosa |
|---|---|---|---|---|
| POST | `/incidencias` | Autenticado | laboratorio, tipo, prioridad, descripción y bloqueo | 201, incidencia abierta |
| GET | `/incidencias/mias` | Autenticado | — | 200, incidencias reportadas |
| GET | `/incidencias?page=0&size=20` | Coordinador/Técnico/Administrador | paginación | 200, `contenido` y metadatos |
| PATCH | `/incidencias/{id}/estado` | Técnico/Administrador | estado, prioridad, diagnóstico y solución | 200, incidencia actualizada |

El diagnóstico es obligatorio al pasar a `EN_ATENCION` o `RESUELTA`. La solución es obligatoria en `RESUELTA` y `CERRADA`.

## Errores

Formato uniforme:

```json
{
  "timestamp": "2026-11-18T06:00:00Z",
  "status": 400,
  "code": "VALIDATION_ERROR",
  "message": "La solicitud contiene datos inválidos",
  "path": "/api/v1/reservas",
  "fieldErrors": {
    "participantes": "debe ser mayor que o igual a 1"
  }
}
```

| HTTP | Uso |
|---:|---|
| 400 | Validación sintáctica o regla de transición inválida |
| 401 | Falta token o token inválido |
| 403 | El rol no tiene permiso |
| 404 | Recurso inexistente |
| 409 | Código duplicado o reserva superpuesta |
| 500 | Error no controlado, sin exponer detalles internos |
