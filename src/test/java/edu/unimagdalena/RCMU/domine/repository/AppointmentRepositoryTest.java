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

    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private AppointmentTypeRepository appointmentTypeRepository;
    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private OfficeRepository officeRepository;
    @Autowired
    private DoctorRepository doctorRepository;
    @Autowired
    private SpecialityRepository specialityRepository;

    private AppointmentType defaultType;
    private Patient defaultPatient;
    private Doctor defaultDoctor;
    private Office defaultOffice;

    @BeforeEach
    void setUp() {

        Speciality speciality = specialityRepository.save(Speciality.builder()
                .name("General")
                .build());

        defaultType = appointmentTypeRepository.save(AppointmentType.builder()
                .name("Consulta")
                .durationInMinutes(20)
                .build());

        defaultPatient = patientRepository.save(Patient.builder()
                .documentId("DOC" + System.nanoTime())
                .firstName("Pedro")
                .lastName("G")
                .email("p" + System.nanoTime() + "@test.com")
                .status(PatientStatus.ACTIVE)
                .build());

        defaultDoctor = doctorRepository.save(Doctor.builder()
                .firstName("Dr")
                .lastName("House")
                .email("h" + System.nanoTime() + "@test.com")
                .speciality(speciality)
                .isActive(true)
                .build());

        defaultOffice = officeRepository.save(Office.builder()
                .roomNumber("OFF-" + System.nanoTime())
                .location("Piso 1")
                .status(OfficeStatus.AVAILABLE)
                .build());
    }

    @Test
    @DisplayName("JPQL: Obtener pacientes con historial de citas completadas")
    void shouldFindPatientsWithCompletedAppointments() {
        LocalDateTime now = LocalDateTime.now();
        appointmentRepository.save(Appointment.builder()
                .patient(defaultPatient)
                .doctor(defaultDoctor)
                .appointmentType(defaultType)
                .office(defaultOffice)
                .status(AppointmentStatus.COMPLETED)
                .dateTime(now)
                .endAt(now.plusMinutes(20)) // endAt es obligatorio ahora
                .build());

        List<Patient> result = appointmentRepository.findPatientsWithCompletedAppointments();

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getDocumentId()).isEqualTo(defaultPatient.getDocumentId());
    }

    @Test
    @DisplayName("Validación de traslape de rango para Doctor")
    void shouldDetectDoctorOverlapRange() {
        LocalDateTime start = LocalDateTime.of(2026, 6, 1, 10, 0);
        LocalDateTime end = start.plusMinutes(20);

        // Guardamos una cita existente
        appointmentRepository.save(Appointment.builder()
                .patient(defaultPatient)
                .doctor(defaultDoctor)
                .appointmentType(defaultType)
                .office(defaultOffice)
                .dateTime(start)
                .endAt(end)
                .status(AppointmentStatus.SCHEDULED)
                .build());

        // Caso 1: Nuevo intento de cita que inicia justo en medio de la anterior
        boolean hasOverlap = appointmentRepository.existsDoctorOverlap(
                defaultDoctor.getId(),
                start.plusMinutes(10),
                start.plusMinutes(30)
        );

        assertThat(hasOverlap).isTrue();
    }

    @Test
    @DisplayName("Búsqueda de citas por rango de fechas")
    void shouldFindByDateTimeRange() {
        LocalDateTime day1 = LocalDateTime.of(2026, 7, 1, 8, 0);
        LocalDateTime day2 = LocalDateTime.of(2026, 7, 2, 8, 0);

        appointmentRepository.save(Appointment.builder()
                .patient(defaultPatient)
                .doctor(defaultDoctor)
                .appointmentType(defaultType)
                .office(defaultOffice)
                .dateTime(day1)
                .endAt(day1.plusMinutes(20))
                .status(AppointmentStatus.SCHEDULED)
                .build());

        // Buscamos solo en el rango del día 1
        List<Appointment> results = appointmentRepository.findByDateTimeBetween(
                day1.minusHours(1),
                day1.plusHours(1)
        );

        assertThat(results).hasSize(1);
    }

    @Test
    @DisplayName("Filtrado de citas por estado de gestión")
    void shouldFindByStatus() {
        LocalDateTime time = LocalDateTime.now().plusDays(1);
        appointmentRepository.save(Appointment.builder()
                .patient(defaultPatient)
                .doctor(defaultDoctor)
                .appointmentType(defaultType)
                .office(defaultOffice)
                .status(AppointmentStatus.CONFIRMED)
                .dateTime(time)
                .endAt(time.plusMinutes(20))
                .build());

        List<Appointment> found = appointmentRepository.findByStatus(AppointmentStatus.CONFIRMED);

        assertThat(found).isNotEmpty();
        assertThat(found.getFirst().getStatus()).isEqualTo(AppointmentStatus.CONFIRMED);
    }
}