package edu.unimagdalena.RCMU.api;

import edu.unimagdalena.RCMU.api.support.ControllerMvcSliceTest;
import edu.unimagdalena.RCMU.api.dto.DoctorDtos.DoctorResponse;
import edu.unimagdalena.RCMU.service.DoctorService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = DoctorController.class)
class DoctorControllerTest extends ControllerMvcSliceTest {

    @MockitoBean
    private DoctorService doctorService;

    @Test
    @DisplayName("POST /api/doctors - Creación válida")
    void create_returns201() throws Exception {
        DoctorResponse response = new DoctorResponse(
                1L,
                "Gregory",
                "House",
                "house@diagnostico.com",
                "Nefrología",
                true
        );

        when(doctorService.create(any())).thenReturn(response);

        // Enviamos el JSON (asegúrate que coincida con tu CreateDoctorRequest)
        String jsonBody = "{\"firstName\":\"Gregory\", \"lastName\":\"House\", \"email\":\"house@diagnostico.com\", \"specialityId\":1}";

        postRawJson("/api/doctors", jsonBody)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstName").value("Gregory"))
                .andExpect(jsonPath("$.specialityName").value("Nefrología"));
    }
}