package edu.unimagdalena.RCMU.service.impl;

import edu.unimagdalena.RCMU.api.dto.AppointmentDtos.*;
import edu.unimagdalena.RCMU.domine.entity.*;
import edu.unimagdalena.RCMU.domine.enums.*;
import edu.unimagdalena.RCMU.domine.repository.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceImplTest {

    @Mock AppointmentRepository repo;
    @Mock PatientRepository patientRepo;
    @Mock DoctorRepository doctorRepo;
    @Mock OfficeRepository officeRepo;
    @Mock AppointmentTypeRepository typeRepo;
    @Mock DoctorScheduleRepository scheduleRepo;

    @InjectMocks AppointmentServiceImpl service;

    @Test
    @DisplayName("6.1: No permitir cita en el pasado")
    void shouldThrowExceptionWhenDateIsPast() {
        // GIVEN
        var pastDate = LocalDateTime.now().minusDays(1);
        var req = new CreateAppointmentRequest(pastDate, 1L, 1L, 1L, 1L);

        setupMocks(AppointmentType.builder().durationInMinutes(30).build());

        // WHEN & THEN
        assertThatThrownBy(() -> service.schedule(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("past");
    }

    @Test
    @DisplayName("6.1: Calcular endAt correctamente y guardar cita")
    void shouldCalculateEndAtAndSaveAppointment() {
        // GIVEN
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        var req = new CreateAppointmentRequest(start, 1L, 1L, 1L, 1L);

        var type = AppointmentType.builder().id(1L).durationInMinutes(30).build(); // 30 min
        setupMocks(type); // Helper para mocks repetitivos

        when(repo.save(any(Appointment.class))).thenAnswer(i -> i.getArgument(0));

        // WHEN
        var res = service.schedule(req);

        // THEN
        assertThat(res.endAt()).isEqualTo(start.plusMinutes(30));
        verify(repo).save(any(Appointment.class));
    }

    @Test
    @DisplayName("6.1: No permitir traslape de doctor")
    void shouldThrowExceptionWhenDoctorIsBusy() {
        // GIVEN
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        var req = new CreateAppointmentRequest(start, 1L, 1L, 1L, 1L);

        lenient().when(patientRepo.findById(any())).thenReturn(Optional.of(Patient.builder().id(1L).status(PatientStatus.ACTIVE).build()));
        lenient().when(doctorRepo.findById(any())).thenReturn(Optional.of(Doctor.builder().id(1L).isActive(true).build()));
        lenient().when(typeRepo.findById(any())).thenReturn(Optional.of(AppointmentType.builder().durationInMinutes(20).build()));
        lenient().when(officeRepo.findById(any())).thenReturn(Optional.of(Office.builder().id(1L).status(OfficeStatus.AVAILABLE).build()));

        // Simulamos que el repositorio detecta traslape
        when(repo.existsDoctorOverlap(eq(1L), any(), any())).thenReturn(true);

        // WHEN & THEN
        assertThatThrownBy(() -> service.schedule(req))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Doctor has an overlap");
    }

    @Test
    @DisplayName("6.3: Cancelar cita correctamente y registrar motivo")
    void shouldCancelAndSetReason() {
        // GIVEN
        var appointment = Appointment.builder().id(10L).status(AppointmentStatus.SCHEDULED).build();
        when(repo.findById(10L)).thenReturn(Optional.of(appointment));
        var cancelReq = new CancelAppointmentRequest("Paciente viajó");

        // WHEN
        service.cancel(10L, cancelReq);

        // THEN
        assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.CANCELLED);
        assertThat(appointment.getCancelReason()).isEqualTo("Paciente viajó");
    }

    @Test
    @DisplayName("6.4: Completar cita correctamente con observaciones")
    void shouldCompleteAppointment() {
        // GIVEN: Una cita confirmada (según flujo PDF)
        var appointment = Appointment.builder().id(5L).status(AppointmentStatus.CONFIRMED).build();
        when(repo.findById(5L)).thenReturn(Optional.of(appointment));

        // WHEN
        service.complete(5L, "Paciente saludable");

        // THEN
        assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.COMPLETED);
        assertThat(appointment.getObservations()).isEqualTo("Paciente saludable");
    }

    // --- HELPER METHODS ---

    private void setupMocks(AppointmentType type) {
        var p = Patient.builder().id(1L).status(PatientStatus.ACTIVE).build();
        var d = Doctor.builder().id(1L).isActive(true).build();
        var o = Office.builder().id(1L).status(OfficeStatus.AVAILABLE).build();

        // Agregamos lenient() a todos para evitar el error de Mockito
        lenient().when(patientRepo.findById(any())).thenReturn(Optional.of(p));
        lenient().when(doctorRepo.findById(any())).thenReturn(Optional.of(d));
        lenient().when(officeRepo.findById(any())).thenReturn(Optional.of(o));
        lenient().when(typeRepo.findById(any())).thenReturn(Optional.of(type));

        // Por defecto no hay traslapes
        lenient().when(repo.existsDoctorOverlap(any(), any(), any())).thenReturn(false);
        lenient().when(repo.existsOfficeOverlap(any(), any(), any())).thenReturn(false);
    }
}