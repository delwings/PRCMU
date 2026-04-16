package edu.unimagdalena.RCMU.domine.repository;

import edu.unimagdalena.RCMU.domine.entity.*;
import edu.unimagdalena.RCMU.domine.enums.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AppointmentRepositoryTest extends AbstractRepositoryIT {

    @Autowired private AppointmentRepository appointmentRepository;
    @Autowired private AppointmentTypeRepository appointmentTypeRepository;
    @Autowired private PatientRepository patientRepository;
    @Autowired private OfficeRepository officeRepository;
    @Autowired private DoctorRepository doctorRepository;
    @Autowired private SpecialityRepository specialityRepository;

    private AppointmentType defaultType;
    private Patient defaultPatient;
    private Doctor defaultDoctor;
    private Office defaultOffice;

    @BeforeEach
    void setUp() {
        Speciality speciality = specialityRepository.save(Speciality.builder()
                .name("General").build());

        defaultType = appointmentTypeRepository.save(AppointmentType.builder()
                .name("Consulta")
                .durationInMinutes(20).build());

        defaultPatient = patientRepository.save(Patient.builder()
                .documentId("DOC" + System.nanoTime())
                .firstName("Pedro").lastName("G")
                .email("p" + System.nanoTime() + "@test.com")
                .status(PatientStatus.ACTIVE).build());

        defaultDoctor = doctorRepository.save(Doctor.builder()
                .firstName("Dr").lastName("House")
                .email("h" + System.nanoTime() + "@test.com")
                .speciality(speciality).isActive(true).build());

        defaultOffice = officeRepository.save(Office.builder()
                .roomNumber("OFF-" + System.nanoTime())
                .location("Piso 1").status(OfficeStatus.AVAILABLE).build());
    }

    @Test
    @DisplayName("JPQL: Ranking de doctores por citas completadas")
    void shouldCountCompletedAppointmentsByDoctor() {
        // GIVEN: Guardamos una cita completada
        LocalDateTime now = LocalDateTime.now();
        appointmentRepository.save(Appointment.builder()
                .patient(defaultPatient).doctor(defaultDoctor).appointmentType(defaultType)
                .office(defaultOffice).status(AppointmentStatus.COMPLETED)
                .dateTime(now).endAt(now.plusMinutes(20)).build());

        // WHEN
        List<Object[]> result = appointmentRepository.countCompletedAppointmentsByDoctor();

        // THEN
        assertThat(result).isNotEmpty();
        assertThat((Doctor) result.get(0)[0]).isEqualTo(defaultDoctor);
        assertThat((Long) result.get(0)[1]).isEqualTo(1L);
    }

    @Test
    @DisplayName("JPQL: Ranking de pacientes por inasistencias (No-Show)")
    void shouldCountNoShowsByPatient() {
        // GIVEN: Una cita marcada como NO_SHOW
        LocalDateTime now = LocalDateTime.now();
        appointmentRepository.save(Appointment.builder()
                .patient(defaultPatient).doctor(defaultDoctor).appointmentType(defaultType)
                .office(defaultOffice).status(AppointmentStatus.NO_SHOW)
                .dateTime(now).endAt(now.plusMinutes(20)).build());

        // WHEN
        List<Object[]> result = appointmentRepository.countNoShowsByPatient();

        // THEN
        assertThat(result).isNotEmpty();
        assertThat((Patient) result.get(0)[0]).isEqualTo(defaultPatient);
        assertThat((Long) result.get(0)[1]).isEqualTo(1L);
    }

    @Test
    @DisplayName("Validación de traslape: El doctor ya tiene cita en ese rango")
    void shouldDetectDoctorOverlap() {
        LocalDateTime start = LocalDateTime.of(2026, 6, 1, 10, 0);
        LocalDateTime end = start.plusMinutes(20);

        appointmentRepository.save(Appointment.builder()
                .patient(defaultPatient).doctor(defaultDoctor).appointmentType(defaultType)
                .office(defaultOffice).dateTime(start).endAt(end)
                .status(AppointmentStatus.SCHEDULED).build());

        // Caso de traslape parcial (empieza antes de que termine la anterior)
        boolean hasOverlap = appointmentRepository.existsDoctorOverlap(
                defaultDoctor.getId(), start.plusMinutes(10), start.plusMinutes(30));

        assertThat(hasOverlap).isTrue();
    }

    @Test
    @DisplayName("Búsqueda de citas por rango de fechas")
    void shouldFindByDateTimeRange() {
        LocalDateTime time = LocalDateTime.of(2026, 7, 1, 8, 0);
        appointmentRepository.save(Appointment.builder()
                .patient(defaultPatient).doctor(defaultDoctor).appointmentType(defaultType)
                .office(defaultOffice).dateTime(time).endAt(time.plusMinutes(20))
                .status(AppointmentStatus.SCHEDULED).build());

        List<Appointment> results = appointmentRepository.findByDateTimeBetween(
                time.minusHours(1), time.plusHours(1));

        assertThat(results).hasSize(1);
    }
}