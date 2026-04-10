package edu.unimagdalena.RCMU.domine.repository;

import edu.unimagdalena.RCMU.domine.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    // Requisito clave: Buscar por el número de documento
    Optional<Patient> findByDocumentId(String documentId);

    // Buscar pacientes activos
    List<Patient> findByStatus(Enum status); // Asegúrate de usar tu Enum de Status
}