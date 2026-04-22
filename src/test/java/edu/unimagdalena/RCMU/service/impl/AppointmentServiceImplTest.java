package edu.unimagdalena.RCMU.service.impl;

import edu.unimagdalena.RCMU.api.dto.AppointmentDtos.*;
import edu.unimagdalena.RCMU.api.error.ConflictException; // Importado
import edu.unimagdalena.RCMU.api.error.ResourceNotFoundException;
import edu.unimagdalena.RCMU.domine.entity.*;
import edu.unimagdalena.RCMU.domine.enums.*;
import edu.unimagdalena.RCMU.domine.repository.*;
import edu.unimagdalena.RCMU.service.mappers.AppointmentMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
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
    @DisplayName("6.6: Fallar si el doctor no atiende en el horario seleccionado")
    void shouldThrowExceptionWhenDoctorDoesNotWorkAtThatTime() {
        // GIVEN
        LocalDateTime start = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0);
        var req = new CreateAppointmentRequest(1L, 1L, 1L, start, 1L); // Corregido orden de argumentos

        var type = AppointmentType.builder().durationInMinutes(30).build();
        setupMocks(type);

        var schedule = DoctorSchedule.builder()
                .dayOfWeek(DayOfWeek.valueOf(start.getDayOfWeek().name()))
                .startTime(LocalTime.of(14, 0))
                .endTime(LocalTime.of(18, 0))
                .build();
        when(scheduleRepo.findByDoctorId(1L)).thenReturn(List.of(schedule));

        // WHEN & THEN - Cambiado a ConflictException
        assertThatThrownBy(() -> service.schedule(req))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("El doctor no atiende");
    }

    @Test
    @DisplayName("6.1: Calcular endAt correctamente y guardar cita cuando el horario es válido")
    void shouldCalculateEndAtAndSaveAppointment() {
        // GIVEN
        LocalDateTime start = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0);
        var req = new CreateAppointmentRequest(1L, 1L, 1L, start, 1L);

        var type = AppointmentType.builder().id(1L).durationInMinutes(30).build();
        setupMocks(type);

        var schedule = DoctorSchedule.builder()
                .dayOfWeek(DayOfWeek.valueOf(start.getDayOfWeek().name()))
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(12, 0))
                .build();
        when(scheduleRepo.findByDoctorId(1L)).thenReturn(List.of(schedule));
        when(repo.save(any(Appointment.class))).thenAnswer(i -> i.getArgument(0));

        // WHEN
        var res = service.schedule(req);

        // THEN
        assertThat(res.endAt()).isEqualTo(start.plusMinutes(30));
        verify(repo).save(any(Appointment.class));
    }

    @Test
    @DisplayName("6.2: Confirmar cita programada correctamente")
    void shouldConfirmScheduledAppointment() {
        // GIVEN
        var appointment = Appointment.builder().id(1L).status(AppointmentStatus.SCHEDULED).build();
        when(repo.findById(1L)).thenReturn(Optional.of(appointment));
        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));

        // WHEN
        var res = service.confirm(1L);

        // THEN
        assertThat(res.status()).isEqualTo(AppointmentStatus.CONFIRMED);
        verify(repo).save(appointment);
    }

    @Test
    @DisplayName("6.3: Cancelar cita y registrar motivo")
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
        verify(repo).save(appointment);
    }

    @Test
    @DisplayName("6.4: Completar cita con observaciones")
    void shouldCompleteAppointment() {
        // GIVEN
        var appointment = Appointment.builder().id(5L).status(AppointmentStatus.CONFIRMED).build();
        when(repo.findById(5L)).thenReturn(Optional.of(appointment));
        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));

        // WHEN
        var res = service.complete(5L, "Paciente saludable");

        // THEN
        assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.COMPLETED);
        assertThat(appointment.getObservations()).isEqualTo("Paciente saludable");
    }

    @Test
    @DisplayName("6.5: Marcar como No Show")
    void shouldMarkAsNoShow() {
        // GIVEN
        var appointment = Appointment.builder().id(1L).status(AppointmentStatus.CONFIRMED).build();
        when(repo.findById(1L)).thenReturn(Optional.of(appointment));
        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));

        // WHEN
        var res = service.markAsNoShow(1L);

        // THEN
        assertThat(res.status()).isEqualTo(AppointmentStatus.NO_SHOW);
    }

    // --- HELPER METHODS ---

    private void setupMocks(AppointmentType type) {
        var p = Patient.builder().id(1L).status(PatientStatus.ACTIVE).build();
        var d = Doctor.builder().id(1L).isActive(true).build();
        var o = Office.builder().id(1L).status(OfficeStatus.AVAILABLE).build();

        lenient().when(patientRepo.findById(1L)).thenReturn(Optional.of(p));
        lenient().when(doctorRepo.findById(1L)).thenReturn(Optional.of(d));
        lenient().when(officeRepo.findById(1L)).thenReturn(Optional.of(o));
        lenient().when(typeRepo.findById(1L)).thenReturn(Optional.of(type));
    }
}