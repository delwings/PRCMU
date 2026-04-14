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
        // 1. Especialidad
        Speciality speciality = specialityRepository.save(Speciality.builder()
                .name("General")
                .build());

        // 2. Tipo de Cita
        defaultType = appointmentTypeRepository.save(AppointmentType.builder()
                .name("Consulta")
                .durationInMinutes(20)
                .build());

        // 3. Paciente (Email único con tiempo actual)
        defaultPatient = patientRepository.save(Patient.builder()
                .documentId("DOC" + System.nanoTime())
                .firstName("Pedro")
                .lastName("G")
                .email("p" + System.nanoTime() + "@test.com")
                .status(PatientStatus.ACTIVE)
                .build());

        // 4. Doctor (Email único)
        defaultDoctor = doctorRepository.save(Doctor.builder()
                .firstName("Dr")
                .lastName("House")
                .email("h" + System.nanoTime() + "@test.com")
                .speciality(speciality)
                .isActive(true)
                .build());

        // 5. Oficina (para Appointment)
        defaultOffice = officeRepository.save(Office.builder()
                .roomNumber("OFF-" + System.nanoTime())
                .location("Piso 1")
                .status(OfficeStatus.AVAILABLE)
                .build());
    }

    @Test
    @DisplayName("JPQL: Obtener pacientes con historial de citas completadas")
    void shouldFindPatientsWithCompletedAppointments() {
        appointmentRepository.save(Appointment.builder()
                .patient(defaultPatient)
                .doctor(defaultDoctor)
                .appointmentType(defaultType)
                .office(defaultOffice)
                .status(AppointmentStatus.COMPLETED)
                .dateTime(LocalDateTime.now())
                .build());

        List<Patient> result = appointmentRepository.findPatientsWithCompletedAppointments();

        assertThat(result).isNotEmpty();
    }

    @Test
    @DisplayName("Validación de disponibilidad física de consultorios")
    void shouldCheckOfficeConflict() {
        LocalDateTime time = LocalDateTime.of(2026, 5, 10, 8, 0);

        appointmentRepository.save(Appointment.builder()
                .patient(defaultPatient)
                .doctor(defaultDoctor)
                .appointmentType(defaultType)
                .office(defaultOffice)
                .dateTime(time)
                .status(AppointmentStatus.SCHEDULED)
                .build());

        boolean exists = appointmentRepository.existsByOfficeIdAndDateTime(defaultOffice.getId(), time);
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Filtrado de citas por estado de gestión")
    void shouldFindByStatus() {
        appointmentRepository.save(Appointment.builder()
                .patient(defaultPatient)
                .doctor(defaultDoctor)
                .appointmentType(defaultType)
                .office(defaultOffice)
                .status(AppointmentStatus.CONFIRMED)
                .dateTime(LocalDateTime.now().plusDays(1))
                .build());

        List<Appointment> found = appointmentRepository.findByStatus(AppointmentStatus.CONFIRMED);

        assertThat(found).isNotEmpty();
        assertThat(found.getFirst().getStatus()).isEqualTo(AppointmentStatus.CONFIRMED);
    }
}