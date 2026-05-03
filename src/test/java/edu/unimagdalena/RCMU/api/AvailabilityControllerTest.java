package edu.unimagdalena.RCMU.api;

import edu.unimagdalena.RCMU.api.support.ControllerMvcSliceTest;
import edu.unimagdalena.RCMU.api.dto.AnalyticsDtos.AvailabilitySlotResponse;
import edu.unimagdalena.RCMU.service.AvailabilityService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AvailabilityController.class)
class AvailabilityControllerTest extends ControllerMvcSliceTest {

    @MockitoBean
    private AvailabilityService availabilityService;

    @Test
    @DisplayName("GET /api/availability/doctors/{id} - Consulta de disponibilidad exitosa")
    void checkAvailability_returns200() throws Exception {
        // Arrange
        LocalDate date = LocalDate.of(2026, 6, 15);
        LocalDateTime start = date.atTime(9, 0);
        LocalDateTime end = date.atTime(9, 20);

        AvailabilitySlotResponse slot = new AvailabilitySlotResponse(start, end, true);

        when(availabilityService.checkDoctorAvailability(eq(1L), any(LocalDate.class)))
                .thenReturn(List.of(slot));

        // ACT & ASSERT
        getJson("/api/availability/doctors/1?date=2026-06-15")
                .andExpect(status().isOk());
    }
}