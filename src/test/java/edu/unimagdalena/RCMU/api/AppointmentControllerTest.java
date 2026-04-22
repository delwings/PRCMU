package edu.unimagdalena.RCMU.api;

// Import para JSON en Spring 4
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.unimagdalena.RCMU.api.dto.AppointmentDtos.*;
import edu.unimagdalena.RCMU.api.error.ConflictException;
import edu.unimagdalena.RCMU.api.error.ResourceNotFoundException;
import edu.unimagdalena.RCMU.domine.enums.AppointmentStatus;
import edu.unimagdalena.RCMU.service.AppointmentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

// ESTA ES LA RUTA CRÍTICA QUE SOLUCIONA EL ERROR DE "MockBean" EN SPRING 4
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AppointmentController.class)
class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean // Reemplazo oficial de @MockBean para Spring Boot 4
    private AppointmentService appointmentService;

    // --- ESCENARIOS DE ÉXITO ---

    @Test
    @DisplayName("POST /api/appointments - Debería crear una cita exitosamente")
    void shouldCreateAppointment() throws Exception {
        CreateAppointmentRequest request = new CreateAppointmentRequest(1L, 1L, 1L, LocalDateTime.now().plusDays(1), 1L);

        AppointmentResponse response = new AppointmentResponse(
                1L, request.dateTime(), request.dateTime().plusMinutes(20),
                "Juan Perez", "Dr. House", AppointmentStatus.SCHEDULED
        );

        given(appointmentService.schedule(any(CreateAppointmentRequest.class))).willReturn(response);

        mockMvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("SCHEDULED"));
    }

    @Test
    @DisplayName("GET /api/appointments - Debería listar todas las citas")
    void shouldListAppointments() throws Exception {
        AppointmentResponse response = new AppointmentResponse(1L, LocalDateTime.now(), LocalDateTime.now().plusMinutes(20), "Juan Perez", "Dr. House", AppointmentStatus.SCHEDULED);
        given(appointmentService.getAll()).willReturn(List.of(response));

        mockMvc.perform(get("/api/appointments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("PUT /api/appointments/{id}/confirm - Debería confirmar la cita")
    void shouldConfirmAppointment() throws Exception {
        AppointmentResponse response = new AppointmentResponse(1L, LocalDateTime.now(), LocalDateTime.now().plusMinutes(20), "Juan Perez", "Dr. House", AppointmentStatus.CONFIRMED);
        given(appointmentService.confirm(1L)).willReturn(response);

        mockMvc.perform(put("/api/appointments/1/confirm"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    @DisplayName("PUT /api/appointments/{id}/cancel - Debería cancelar la cita")
    void shouldCancelAppointment() throws Exception {
        CancelAppointmentRequest cancelReq = new CancelAppointmentRequest("Motivo médico");

        mockMvc.perform(put("/api/appointments/1/cancel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cancelReq)))
                .andExpect(status().isNoContent());

        verify(appointmentService).cancel(eq(1L), any(CancelAppointmentRequest.class));
    }

    // --- ESCENARIOS DE ERROR ---

    @Test
    @DisplayName("POST /api/appointments - Debería retornar 409 cuando hay conflicto")
    void create_shouldReturn409WhenConflict() throws Exception {
        CreateAppointmentRequest request = new CreateAppointmentRequest(1L, 1L, 1L, LocalDateTime.now().plusDays(1), 1L);
        given(appointmentService.schedule(any())).willThrow(new ConflictException("Conflicto de horario"));

        mockMvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("PUT /api/appointments/{id}/confirm - Debería retornar 404 si la cita no existe")
    void shouldReturn404WhenAppointmentNotFound() throws Exception {
        given(appointmentService.confirm(99L)).willThrow(new ResourceNotFoundException("Cita no encontrada"));

        mockMvc.perform(put("/api/appointments/99/confirm"))
                .andExpect(status().isNotFound());
    }
}