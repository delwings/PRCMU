package edu.unimagdalena.RCMU.domine.repository;

import edu.unimagdalena.RCMU.domine.entity.Patient;
import edu.unimagdalena.RCMU.domine.enums.PatientStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    // Buscar paciente por su documento de identidad único
    Optional<Patient> findByDocumentId(String documentId);

    // Filtrar pacientes por su estado actual (ACTIVE/INACTIVE)
    List<Patient> findByStatus(PatientStatus status);
}