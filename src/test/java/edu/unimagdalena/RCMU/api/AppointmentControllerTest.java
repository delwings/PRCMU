package edu.unimagdalena.RCMU.api;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import edu.unimagdalena.RCMU.api.support.ControllerMvcSliceTest;
import edu.unimagdalena.RCMU.api.dto.AppointmentDtos.*;
import edu.unimagdalena.RCMU.api.error.ResourceNotFoundException;
import edu.unimagdalena.RCMU.domine.enums.AppointmentStatus;
import edu.unimagdalena.RCMU.service.AppointmentService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AppointmentController.class)
class AppointmentControllerTest extends ControllerMvcSliceTest {

    @MockitoBean
    private AppointmentService appointmentService;

    @Test
    @DisplayName("POST /api/appointments - Debería crear una cita")
    void create_returns201() throws Exception {
        LocalDateTime fechaCita = LocalDateTime.now().plusDays(1);
        CreateAppointmentRequest request = new CreateAppointmentRequest(
                fechaCita, // LocalDateTime
                1L,        // patientId
                1L,        // doctorId
                1L,        // officeId
                1L         // typeId
        );

        AppointmentResponse response = new AppointmentResponse(
                1L, request.dateTime(), request.dateTime().plusMinutes(20),
                "Juan Perez", "Dr. House", AppointmentStatus.SCHEDULED
        );

        when(appointmentService.schedule(any(CreateAppointmentRequest.class))).thenReturn(response);

        postJson("/api/appointments", request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));

        verify(appointmentService).schedule(any());
    }

    @Test
    @DisplayName("GET /api/appointments - Listar citas")
    void list_returns200() throws Exception {
        AppointmentResponse response = new AppointmentResponse(1L, LocalDateTime.now(), LocalDateTime.now().plusMinutes(20), "Juan Perez", "Dr. House", AppointmentStatus.SCHEDULED);
        when(appointmentService.getAll()).thenReturn(List.of(response));

        getJson("/api/appointments")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(appointmentService).getAll();
    }

    @Test
    @DisplayName("POST /api/appointments - 409 si hay conflicto de horario")
    void create_conflict_returns409() throws Exception {
        // Arrange
        LocalDateTime fecha = LocalDateTime.now().plusDays(1);
        CreateAppointmentRequest request = new CreateAppointmentRequest(fecha, 1L, 1L, 1L, 1L);

        // LANZAMOS EXCEPCIÓN DE CONFLICTO
        when(appointmentService.schedule(any(CreateAppointmentRequest.class)))
                .thenThrow(new edu.unimagdalena.RCMU.api.error.ConflictException("El doctor ya tiene una cita a esa hora"));

        // Act & Assert
        postJson("/api/appointments", request)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("El doctor ya tiene una cita a esa hora"));
    }

    @Test
    @DisplayName("PUT /api/appointments/{id}/confirm - 404 si la cita no existe")
    void confirm_notFound_returns404() throws Exception {
        // Arrange
        Long idInexistente = 99L;

        when(appointmentService.confirm(idInexistente))
                .thenThrow(new ResourceNotFoundException("Cita no encontrada"));

        // Act & Assert
        mockMvc.perform(put("/api/appointments/" + idInexistente + "/confirm"))
                .andExpect(status().isNotFound());
    }
}