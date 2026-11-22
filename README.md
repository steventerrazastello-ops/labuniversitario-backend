# Backend de LabUniversitario

API REST para gestionar laboratorios, reservas e incidencias. Implementa autenticación JWT, autorización por roles, persistencia JPA, validaciones y errores uniformes.

## Inicio rápido

1. Usar **Java 21** y **Maven 3.10+**.
2. Ejecutar `mvn spring-boot:run` desde esta carpeta.
3. Importar en Postman los archivos de `postman/`.
4. Consultar `http://localhost:8080/api/v1/laboratorios` con un token JWT.

El perfil predeterminado es `local`: usa H2 en memoria y carga datos de demostración.

## Usuarios locales

| Rol | Correo | Contraseña |
|---|---|---|
| Solicitante | `solicitante@lab.edu.pe` | `Clave123!` |
| Coordinador | `coordinador@lab.edu.pe` | `Clave123!` |
| Técnico | `tecnico@lab.edu.pe` | `Clave123!` |
| Administrador | `admin@lab.edu.pe` | `Clave123!` |

Estas credenciales son exclusivamente académicas y no deben usarse en producción.

## Arquitectura implementada

| Capa | Responsabilidad | Paquetes principales |
|---|---|---|
| Controller | Recibe HTTP, valida DTO y devuelve códigos REST | `controller` |
| DTO | Define contratos de entrada y salida | `dto` |
| Service | Aplica reglas de negocio y transacciones | `service` |
| Repository | Ejecuta consultas JPA/JPQL parametrizadas | `repository` |
| Entity | Modela tablas y relaciones persistentes | `domain` |
| Security | JWT, BCrypt, CORS y autorización por rol | `config`, `exception` |

Los controladores no exponen entidades JPA: toda comunicación se realiza mediante DTO.

## Persistencia

### Perfil local

```properties
spring.profiles.active=local
spring.datasource.url=jdbc:h2:mem:labuniversitario;MODE=MSSQLServer
```

JPA/Hibernate crea el esquema en memoria al iniciar. La consola H2 está disponible en `/h2-console` durante el desarrollo local.

### Perfil SQL Server

El perfil `sqlserver` está preparado, pero requiere una instancia accesible y credenciales reales:

```powershell
$env:SPRING_PROFILES_ACTIVE = "sqlserver"
$env:DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=LabUniversitario;encrypt=true;trustServerCertificate=true"
$env:DB_USERNAME = "usuario"
$env:DB_PASSWORD = "secreto"
$env:JWT_SECRET = "secreto-base64-de-32-bytes-o-mas"
mvn spring-boot:run
```

En este entorno académico la evidencia se ejecutó con H2. No se afirma una conexión SQL Server que no haya sido verificada.

## Pruebas

### Integración automatizada

```powershell
mvn test
```

Resultado verificado: **5 pruebas, 0 fallos, 0 errores y 0 omitidas**.

### Postman/Newman

```powershell
npx --yes newman@6.2.1 run postman/LabUniversitario.postman_collection.json `
  -e postman/LabUniversitario.local.postman_environment.json
```

Resultado verificado: **22 solicitudes y 22 aserciones, sin fallos**. El reporte sanitizado está en `docs/evidencias/newman-report.json` y el resumen legible en `docs/evidencias/newman-resumen.md`. Los JWT generados durante la ejecución fueron reemplazados antes de publicar la evidencia.

## Documentación

- `docs/ENDPOINTS.md` — métodos, rutas, permisos, entradas y respuestas.
- `postman/LabUniversitario.postman_collection.json` — colección ejecutable.
- `docs/evidencias/` — resultados de pruebas.

## Repositorio remoto

[https://github.com/steventerrazastello-ops/labuniversitario-backend](https://github.com/steventerrazastello-ops/labuniversitario-backend)

Ruta local del proyecto: `LabUniversitario/backend`.
