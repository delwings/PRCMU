package edu.unimagdalena.RCMU.service.impl;

import edu.unimagdalena.RCMU.api.dto.DoctorScheduleDtos.*;
import edu.unimagdalena.RCMU.domine.entity.Doctor;
import edu.unimagdalena.RCMU.domine.entity.DoctorSchedule;
import edu.unimagdalena.RCMU.domine.enums.DayOfWeek;
import edu.unimagdalena.RCMU.domine.repository.DoctorRepository;
import edu.unimagdalena.RCMU.domine.repository.DoctorScheduleRepository;
import edu.unimagdalena.RCMU.api.error.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorScheduleServiceImplTest {

    @Mock DoctorScheduleRepository repo;
    @Mock DoctorRepository doctorRepo;

    @InjectMocks DoctorScheduleServiceImpl service;

    @Test
    @DisplayName("Debe crear un horario laboral para un doctor")
    void shouldCreateDoctorSchedule() {
        // GIVEN
        Long doctorId = 1L;
        var req = new CreateDoctorScheduleRequest(
                doctorId,
                DayOfWeek.MONDAY,
                LocalTime.of(8, 0),
                LocalTime.of(12, 0)
        );

        var doctor = Doctor.builder().id(doctorId).lastName("House").build();
        when(doctorRepo.findById(doctorId)).thenReturn(Optional.of(doctor));

        when(repo.save(any(DoctorSchedule.class))).thenAnswer(invocation -> {
            DoctorSchedule s = invocation.getArgument(0);
            s.setId(100L);
            return s;
        });

        // WHEN: Se añade el parámetro doctorId a la llamada
        var res = service.create(doctorId, req);

        // THEN
        assertThat(res).isNotNull();
        assertThat(res.id()).isEqualTo(100L);
        assertThat(res.dayOfWeek()).isEqualTo(DayOfWeek.MONDAY);
        verify(doctorRepo).findById(doctorId);
        verify(repo).save(any(DoctorSchedule.class));
    }

    @Test
    @DisplayName("Debe fallar si el doctor no existe al crear horario")
    void shouldThrowExceptionWhenDoctorNotFound() {
        // GIVEN
        Long invalidDoctorId = 99L;
        when(doctorRepo.findById(invalidDoctorId)).thenReturn(Optional.empty());
        var req = new CreateDoctorScheduleRequest(invalidDoctorId, DayOfWeek.FRIDAY, LocalTime.MIN, LocalTime.MAX);

        // WHEN & THEN: Se añade el parámetro invalidDoctorId a la llamada
        org.assertj.core.api.Assertions.assertThatThrownBy(() -> service.create(invalidDoctorId, req))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}