package edu.unimagdalena.RCMU.domine.repository;

import edu.unimagdalena.RCMU.domine.entity.*;
import edu.unimagdalena.RCMU.domine.enums.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    // Busca el historial de citas de un paciente ordenado por fecha
    List<Appointment> findByPatientIdOrderByDateTimeDesc(Long patientId);

    // Valida si existe una cita en un consultorio y hora específica para evitar traslapes
    boolean existsByOfficeIdAndDateTime(Long officeId, LocalDateTime dateTime);

    // Filtra citas por estado de la cita
    List<Appointment> findByStatus(AppointmentStatus status);

    // JPQL: Obtiene pacientes que tienen al menos una cita completada
    @Query("SELECT DISTINCT a.patient FROM Appointment a WHERE a.status = edu.unimagdalena.RCMU.domine.enums.AppointmentStatus.COMPLETED")
    List<Patient> findPatientsWithCompletedAppointments();
}