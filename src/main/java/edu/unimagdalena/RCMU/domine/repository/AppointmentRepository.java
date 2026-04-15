package edu.unimagdalena.RCMU.domine.repository;

import edu.unimagdalena.RCMU.domine.entity.*;
import edu.unimagdalena.RCMU.domine.enums.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // 1. Consultas Básicas y Filtros
    List<Appointment> findByPatientIdOrderByDateTimeDesc(Long patientId);

    List<Appointment> findByStatus(AppointmentStatus status);

    List<Appointment> findByDateTimeBetween(LocalDateTime start, LocalDateTime end);

    // Mantenemos este para compatibilidad con los tests iniciales
    boolean existsByOfficeIdAndDateTime(Long officeId, LocalDateTime dateTime);

    // 2. Consultas JPQL de Negocio
    @Query("SELECT DISTINCT a.patient FROM Appointment a WHERE a.status = edu.unimagdalena.RCMU.domine.enums.AppointmentStatus.COMPLETED")
    List<Patient> findPatientsWithCompletedAppointments();

    // NUEVAS CONSULTAS PARA REPORT SERVICE (Analytics)

    // Ranking de Doctores por citas completadas (PDF 6.6)
    @Query("SELECT a.doctor, COUNT(a) FROM Appointment a " +
            "WHERE a.status = edu.unimagdalena.RCMU.domine.enums.AppointmentStatus.COMPLETED " +
            "GROUP BY a.doctor ORDER BY COUNT(a) DESC")
    List<Object[]> countCompletedAppointmentsByDoctor();

    // Ranking de Pacientes por No-Show (PDF 6.5)
    @Query("SELECT a.patient, COUNT(a) FROM Appointment a " +
            "WHERE a.status = edu.unimagdalena.RCMU.domine.enums.AppointmentStatus.NO_SHOW " +
            "GROUP BY a.patient ORDER BY COUNT(a) DESC")
    List<Object[]> countNoShowsByPatient();

    // 3. Validaciones de Traslape (Overlap)

    @Query("SELECT COUNT(a) > 0 FROM Appointment a WHERE a.doctor.id = :doctorId " +
            "AND a.status NOT IN (edu.unimagdalena.RCMU.domine.enums.AppointmentStatus.CANCELLED) " +
            "AND ((:start < a.endAt) AND (:end > a.dateTime))")
    boolean existsDoctorOverlap(@Param("doctorId") Long doctorId,
                                @Param("start") LocalDateTime start,
                                @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(a) > 0 FROM Appointment a WHERE a.office.id = :officeId " +
            "AND a.status NOT IN (edu.unimagdalena.RCMU.domine.enums.AppointmentStatus.CANCELLED) " +
            "AND ((:start < a.endAt) AND (:end > a.dateTime))")
    boolean existsOfficeOverlap(@Param("officeId") Long officeId,
                                @Param("start") LocalDateTime start,
                                @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(a) > 0 FROM Appointment a WHERE a.patient.id = :patientId " +
            "AND a.status NOT IN (edu.unimagdalena.RCMU.domine.enums.AppointmentStatus.CANCELLED) " +
            "AND ((:start < a.endAt) AND (:end > a.dateTime))")
    boolean existsPatientOverlap(@Param("patientId") Long patientId,
                                 @Param("start") LocalDateTime start,
                                 @Param("end") LocalDateTime end);
}