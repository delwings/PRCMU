package edu.unimagdalena.RCMU.service.impl;

import edu.unimagdalena.RCMU.api.dto.AnalyticsDtos.AvailabilitySlotResponse;
import edu.unimagdalena.RCMU.domine.entity.*;
import edu.unimagdalena.RCMU.domine.enums.AppointmentStatus;
import edu.unimagdalena.RCMU.domine.enums.DayOfWeek;
import edu.unimagdalena.RCMU.domine.repository.AppointmentRepository;
import edu.unimagdalena.RCMU.domine.repository.DoctorScheduleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AvailabilityServiceImplTest {

    @Mock AppointmentRepository appointmentRepo;
    @Mock DoctorScheduleRepository scheduleRepo;

    @InjectMocks AvailabilityServiceImpl service;

    @Test
    @DisplayName("Debe generar slots libres excluyendo los ocupados")
    void shouldGenerateOnlyAvailableSlots() {
        // GIVEN: El doctor trabaja de 08:00 a 09:00 (60 min)
        // Con slots de 30 min, deberían salir 2 bloques: [08:00-08:30] y [08:30-09:00]
        Long doctorId = 1L;
        LocalDate date = LocalDate.of(2026, 5, 20);

        var schedule = DoctorSchedule.builder()
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(9, 0))
                .dayOfWeek(DayOfWeek.WEDNESDAY)
                .build();

        when(scheduleRepo.findByDoctorIdAndDayOfWeek(eq(doctorId), any())).thenReturn(List.of(schedule));

        // Simulamos una cita ya agendada en el primer bloque [08:00 - 08:30]
        var existingApp = Appointment.builder()
                .dateTime(date.atTime(8, 0))
                .endAt(date.atTime(8, 30))
                .doctor(Doctor.builder().id(doctorId).build())
                .status(AppointmentStatus.SCHEDULED)
                .build();

        when(appointmentRepo.findByDateTimeBetween(any(), any())).thenReturn(List.of(existingApp));

        // WHEN: Consultamos disponibilidad
        List<AvailabilitySlotResponse> slots = service.checkDoctorAvailability(doctorId, date);

        // THEN
        // Debería haber solo 1 slot disponible (el segundo bloque)
        assertThat(slots).hasSize(1);
        assertThat(slots.get(0).start()).isEqualTo(date.atTime(8, 30));
        assertThat(slots.get(0).end()).isEqualTo(date.atTime(9, 0));
        assertThat(slots.get(0).isAvailable()).isTrue();
    }

    @Test
    @DisplayName("Cita cancelada debe liberar el slot inmediatamente")
    void canceledAppointmentShouldNotBlockSlot() {
        // GIVEN: El doctor tiene una cita de 08:00 a 08:30 pero está CANCELADA
        Long doctorId = 1L;
        LocalDate date = LocalDate.of(2026, 5, 20);

        var schedule = DoctorSchedule.builder()
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(8, 30))
                .build();
        when(scheduleRepo.findByDoctorIdAndDayOfWeek(any(), any())).thenReturn(List.of(schedule));

        var canceledApp = Appointment.builder()
                .dateTime(date.atTime(8, 0))
                .endAt(date.atTime(8, 30))
                .doctor(Doctor.builder().id(doctorId).build())
                .status(AppointmentStatus.CANCELLED) // ESTADO CANCELADO
                .build();

        when(appointmentRepo.findByDateTimeBetween(any(), any())).thenReturn(List.of(canceledApp));

        // WHEN
        List<AvailabilitySlotResponse> slots = service.checkDoctorAvailability(doctorId, date);

        // THEN: El slot debe aparecer como disponible
        assertThat(slots).hasSize(1);
        assertThat(slots.get(0).isAvailable()).isTrue();
    }
}