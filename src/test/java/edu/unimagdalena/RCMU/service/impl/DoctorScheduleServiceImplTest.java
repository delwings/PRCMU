package edu.unimagdalena.RCMU.service.impl;

import edu.unimagdalena.RCMU.api.dto.DoctorScheduleDtos.*;
import edu.unimagdalena.RCMU.domine.entity.Doctor;
import edu.unimagdalena.RCMU.domine.entity.DoctorSchedule;
import edu.unimagdalena.RCMU.domine.enums.DayOfWeek;
import edu.unimagdalena.RCMU.domine.repository.DoctorRepository;
import edu.unimagdalena.RCMU.domine.repository.DoctorScheduleRepository;
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
        // GIVEN: Un request para lunes de 8am a 12pm
        Long doctorId = 1L;
        var req = new CreateDoctorScheduleRequest(
                doctorId,
                DayOfWeek.MONDAY,
                LocalTime.of(8, 0),
                LocalTime.of(12, 0)
        );

        var doctor = Doctor.builder().id(doctorId).lastName("House").build();
        when(doctorRepo.findById(doctorId)).thenReturn(Optional.of(doctor));

        // Mock del save: capturamos lo que se intenta guardar
        when(repo.save(any(DoctorSchedule.class))).thenAnswer(invocation -> {
            DoctorSchedule s = invocation.getArgument(0);
            s.setId(100L); // Simulamos ID generado
            return s;
        });

        // WHEN
        var res = service.create(req);

        // THEN
        assertThat(res).isNotNull();
        assertThat(res.id()).isEqualTo(100L);
        assertThat(res.dayOfWeek()).isEqualTo(DayOfWeek.MONDAY);
        verify(repo).save(any(DoctorSchedule.class));
    }

    @Test
    @DisplayName("Debe fallar si el doctor no existe al crear horario")
    void shouldThrowExceptionWhenDoctorNotFound() {
        // GIVEN
        when(doctorRepo.findById(anyLong())).thenReturn(Optional.empty());
        var req = new CreateDoctorScheduleRequest(99L, DayOfWeek.FRIDAY, LocalTime.MIN, LocalTime.MAX);

        // WHEN & THEN
        // Usamos org.assertj.core.api.Assertions.assertThatThrownBy
        org.assertj.core.api.Assertions.assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(edu.unimagdalena.RCMU.exception.NotFoundException.class);
    }
}