# Evidencia de ejecución Postman/Newman

**Fecha:** 18 de noviembre de 2026  
**Colección:** LabUniversitario API REST  
**Entorno:** perfil local H2 en memoria

## Resultado

- Solicitudes ejecutadas: **22**
- Solicitudes fallidas: **0**
- Aserciones ejecutadas: **22**
- Aserciones fallidas: **0**
- Tiempo medio de respuesta: **108 ms**
- Rango observado: **7–979 ms**

## Detalle

| # | Caso | HTTP | Estado | Tiempo |
|---:|---|---:|---|---:|
| 1 | 01 - Login solicitante | 200 | OK | 979 ms |
| 2 | 02 - Listar laboratorios | 200 | OK | 57 ms |
| 3 | 03 - Obtener laboratorio | 200 | OK | 17 ms |
| 4 | 04 - Consultar disponibilidad | 200 | OK | 48 ms |
| 5 | 05 - Crear reserva | 201 | Created | 52 ms |
| 6 | 06 - Rechazar reserva superpuesta | 409 | Conflict | 23 ms |
| 7 | 07 - Listar mis reservas | 200 | OK | 32 ms |
| 8 | 08 - Login coordinador | 200 | OK | 308 ms |
| 9 | 09 - Listar todas las reservas | 200 | OK | 19 ms |
| 10 | 10 - Aprobar reserva | 200 | OK | 31 ms |
| 11 | 11 - Cancelar reserva propia | 200 | OK | 18 ms |
| 12 | 12 - Crear incidencia | 201 | Created | 25 ms |
| 13 | 13 - Listar mis incidencias | 200 | OK | 14 ms |
| 14 | 14 - Login técnico | 200 | OK | 309 ms |
| 15 | 15 - Listar todas las incidencias | 200 | OK | 17 ms |
| 16 | 16 - Resolver incidencia | 200 | OK | 18 ms |
| 17 | 17 - Login administrador | 200 | OK | 323 ms |
| 18 | 18 - Crear laboratorio | 201 | Created | 17 ms |
| 19 | 19 - Actualizar laboratorio | 200 | OK | 17 ms |
| 20 | 20 - Desactivar laboratorio | 204 | No Content | 7 ms |
| 21 | 21 - Validación de solicitud inválida | 400 | Bad Request | 28 ms |
| 22 | 22 - Acceso sin token | 401 | Unauthorized | 7 ms |

## Archivos verificables

- `postman/LabUniversitario.postman_collection.json` — colección importable.
- `postman/LabUniversitario.local.postman_environment.json` — variables del entorno local.
- `docs/evidencias/newman-report.json` — reporte generado por Newman y sanitizado para retirar los JWT de la ejecución.

> La ejecución cubrió respuestas 200, 201, 204, 400, 401 y 409, además de autenticación JWT, autorización por rol, validaciones y reglas de negocio.
