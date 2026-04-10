package edu.unimagdalena.RCMU.domine.repository;

import edu.unimagdalena.RCMU.domine.entity.*;
import edu.unimagdalena.RCMU.domine.enums.DayOfWeek;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DoctorScheduleRepositoryTest extends AbstractRepositoryIT {

    @Autowired
    private DoctorScheduleRepository scheduleRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private SpecialityRepository specialityRepository;

    private Doctor defaultDoctor;

    @BeforeEach
    void setUp() {
        Speciality speciality = specialityRepository.save(Speciality.builder()
                .name("Cardiología")
                .build());

        defaultDoctor = doctorRepository.save(Doctor.builder()
                .firstName("Gregory")
                .lastName("House")
                .email("house." + System.nanoTime() + "@hospital.com")
                .speciality(speciality)
                .isActive(true)
                .build());
    }

    @Test
    @DisplayName("Debería encontrar horarios por doctor y día de la semana")
    void shouldFindByDoctorAndDayOfWeek() {
        // GIVEN
        DoctorSchedule schedule = DoctorSchedule.builder()
                .doctor(defaultDoctor)
                .dayOfWeek(DayOfWeek.MONDAY)
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(12, 0))
                .build();
        scheduleRepository.save(schedule);

        // WHEN
        List<DoctorSchedule> found = scheduleRepository.findByDoctorIdAndDayOfWeek(defaultDoctor.getId(), DayOfWeek.MONDAY);

        // THEN
        assertThat(found).isNotEmpty();
        assertThat(found.getFirst().getDoctor().getLastName()).isEqualTo("House");
    }

    @Test
    @DisplayName("Debería listar todos los horarios de un doctor específico")
    void shouldFindAllSchedulesByDoctorId() {
        // GIVEN
        scheduleRepository.save(DoctorSchedule.builder()
                .doctor(defaultDoctor)
                .dayOfWeek(DayOfWeek.TUESDAY)
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(12, 0))
                .build());

        // WHEN
        List<DoctorSchedule> schedules = scheduleRepository.findByDoctorId(defaultDoctor.getId());

        // THEN
        assertThat(schedules).hasSize(1);
        assertThat(schedules.getFirst().getDayOfWeek()).isEqualTo(DayOfWeek.TUESDAY);
    }
}