package pe.edu.upn.labuniversitario;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class LabUniversitarioApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void loginYConsultaDeLaboratoriosFuncionan() throws Exception {
        String token = login("solicitante@lab.edu.pe");

        mockMvc.perform(get("/api/v1/laboratorios")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigo").value("LAB-A101"))
                .andExpect(jsonPath("$[0].activo").value(true));
    }

    @Test
    void reservaValidaSeCreaYLaSuperposicionSeRechaza() throws Exception {
        String token = login("solicitante@lab.edu.pe");
        String fecha = LocalDate.now().plusDays(10).toString();
        String body = """
                {
                  "laboratorioId": 1,
                  "fecha": "%s",
                  "horaInicio": "10:00",
                  "horaFin": "11:30",
                  "participantes": 20,
                  "actividad": "Taller de API REST"
                }
                """.formatted(fecha);

        mockMvc.perform(post("/api/v1/reservas")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("PENDIENTE"))
                .andExpect(jsonPath("$.codigo").isNotEmpty());

        mockMvc.perform(post("/api/v1/reservas")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("RESERVA_HORARIO_OCUPADO"));
    }

    @Test
    void validacionYAutorizacionDevuelvenErroresEstructurados() throws Exception {
        String solicitante = login("solicitante@lab.edu.pe");
        String fecha = LocalDate.now().plusDays(12).toString();

        mockMvc.perform(post("/api/v1/reservas")
                        .header("Authorization", "Bearer " + solicitante)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "laboratorioId": 1,
                                  "fecha": "%s",
                                  "horaInicio": "12:00",
                                  "horaFin": "11:00",
                                  "participantes": 0,
                                  "actividad": ""
                                }
                                """.formatted(fecha)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.fieldErrors.actividad").exists())
                .andExpect(jsonPath("$.fieldErrors.participantes").exists());

        mockMvc.perform(post("/api/v1/laboratorios")
                        .header("Authorization", "Bearer " + solicitante)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "codigo": "LAB-X999",
                                  "nombre": "Laboratorio restringido",
                                  "ubicacion": "Pabellón X",
                                  "capacidad": 20
                                }
                                """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("FORBIDDEN"));
    }

    @Test
    void flujoDeIncidenciaPermiteCrearYResolverConRolTecnico() throws Exception {
        String solicitante = login("solicitante@lab.edu.pe");
        MvcResult created = mockMvc.perform(post("/api/v1/incidencias")
                        .header("Authorization", "Bearer " + solicitante)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "laboratorioId": 2,
                                  "tipo": "RED",
                                  "prioridad": "ALTA",
                                  "descripcion": "El switch principal no responde.",
                                  "bloqueaUso": true
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("ABIERTA"))
                .andReturn();
        long incidenciaId = objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asLong();

        String tecnico = login("tecnico@lab.edu.pe");
        mockMvc.perform(patch("/api/v1/incidencias/{id}/estado", incidenciaId)
                        .header("Authorization", "Bearer " + tecnico)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "estado": "RESUELTA",
                                  "prioridad": "ALTA",
                                  "diagnostico": "Fuente del switch desconectada.",
                                  "solucion": "Se reconectó y verificó la conectividad."
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("RESUELTA"))
                .andExpect(jsonPath("$.atendidoPor").value("tecnico@lab.edu.pe"));
    }

    @Test
    void endpointProtegidoSinTokenResponde401() throws Exception {
        mockMvc.perform(get("/api/v1/laboratorios"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    private String login(String email) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "%s",
                                  "password": "Clave123!"
                                }
                                """.formatted(email)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andReturn();
        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        return json.get("accessToken").asText();
    }
}
