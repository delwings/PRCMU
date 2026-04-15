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

    boolean existsByOfficeIdAndDateTime(Long officeId, LocalDateTime dateTime);

    List<Appointment> findByStatus(AppointmentStatus status);

    @Query("SELECT DISTINCT a.patient FROM Appointment a WHERE a.status = edu.unimagdalena.RCMU.domine.enums.AppointmentStatus.COMPLETED")
    List<Patient> findPatientsWithCompletedAppointments();

    // Buscar citas por rango de fecha (Reportes)
    List<Appointment> findByDateTimeBetween(LocalDateTime start, LocalDateTime end);

    // Validación de traslape para Doctor
    @Query("SELECT COUNT(a) > 0 FROM Appointment a WHERE a.doctor.id = :doctorId " +
            "AND a.status NOT IN (edu.unimagdalena.RCMU.domine.enums.AppointmentStatus.CANCELLED) " +
            "AND ((:start < a.endAt) AND (:end > a.dateTime))")
    boolean existsDoctorOverlap(@Param("doctorId") Long doctorId,
                                @Param("start") LocalDateTime start,
                                @Param("end") LocalDateTime end);

    // Validación de traslape para Consultorio
    @Query("SELECT COUNT(a) > 0 FROM Appointment a WHERE a.office.id = :officeId " +
            "AND a.status NOT IN (edu.unimagdalena.RCMU.domine.enums.AppointmentStatus.CANCELLED) " +
            "AND ((:start < a.endAt) AND (:end > a.dateTime))")
    boolean existsOfficeOverlap(@Param("officeId") Long officeId,
                                @Param("start") LocalDateTime start,
                                @Param("end") LocalDateTime end);

    // Validación de traslape para Paciente (Un paciente no puede cruzarse citas)
    @Query("SELECT COUNT(a) > 0 FROM Appointment a WHERE a.patient.id = :patientId " +
            "AND a.status NOT IN (edu.unimagdalena.RCMU.domine.enums.AppointmentStatus.CANCELLED) " +
            "AND ((:start < a.endAt) AND (:end > a.dateTime))")
    boolean existsPatientOverlap(@Param("patientId") Long patientId,
                                 @Param("start") LocalDateTime start,
                                 @Param("end") LocalDateTime end);
}