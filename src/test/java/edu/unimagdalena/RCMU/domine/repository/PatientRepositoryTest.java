package edu.unimagdalena.RCMU.domine.repository;

import edu.unimagdalena.RCMU.domine.entity.Patient;
import edu.unimagdalena.RCMU.domine.enums.PatientStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class PatientRepositoryTest extends AbstractRepositoryIT {

    @Autowired
    private PatientRepository patientRepository;

    @Test
    @DisplayName("Buscar paciente por documento de identidad")
    void shouldFindByDocumentId() {
        // GIVEN
        Patient patient = Patient.builder()
                .documentId("123456")
                .firstName("Juan")
                .lastName("Perez")
                .email("juan" + System.nanoTime() + "@mail.com")
                .status(PatientStatus.ACTIVE)
                .build();

        patientRepository.save(patient);

        // WHEN
        Optional<Patient> found = patientRepository.findByDocumentId("123456");

        // THEN
        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("Juan");
    }

    @Test
    @DisplayName("Filtrar pacientes por estado")
    void shouldFindByStatus() {
        // GIVEN
        Patient patient = Patient.builder()
                .documentId("987654")
                .firstName("Maria")
                .lastName("Gomez")
                .email("maria" + System.nanoTime() + "@mail.com")
                .status(PatientStatus.ACTIVE)
                .build();
        patientRepository.save(patient);

        // WHEN
        List<Patient> actives = patientRepository.findByStatus(PatientStatus.ACTIVE);

        // THEN
        assertThat(actives).isNotEmpty();
        assertThat(actives.getFirst().getStatus()).isEqualTo(PatientStatus.ACTIVE);
    }
}