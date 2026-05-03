package edu.unimagdalena.RCMU.api;

import edu.unimagdalena.RCMU.api.support.ControllerMvcSliceTest;
import edu.unimagdalena.RCMU.service.PatientService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PatientController.class)
class PatientControllerTest extends ControllerMvcSliceTest {

    @MockitoBean
    private PatientService patientService;

    @Test
    @DisplayName("POST /api/patients - Debería retornar 400 si el email es inválido")
    void create_invalidEmail_returns400() throws Exception {
        // Enviamos un JSON con formato de email incorrecto o campos nulos
        String invalidJson = "{\"name\":\"Juan\", \"email\":\"correo-no-valido\"}";

        postRawJson("/api/patients", invalidJson)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Error de validación en los campos"));
    }
}