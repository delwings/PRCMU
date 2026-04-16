package edu.unimagdalena.RCMU.service.impl;

import edu.unimagdalena.RCMU.api.dto.AppointmentDtos.*;
import edu.unimagdalena.RCMU.domine.entity.*;
import edu.unimagdalena.RCMU.domine.enums.*;
import edu.unimagdalena.RCMU.domine.repository.*;
import edu.unimagdalena.RCMU.exception.NotFoundException;
import edu.unimagdalena.RCMU.service.AppointmentService;
import edu.unimagdalena.RCMU.service.mappers.AppointmentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service @RequiredArgsConstructor @Transactional
public class AppointmentServiceImpl implements AppointmentService {
    private final AppointmentRepository repo;
    private final PatientRepository patientRepo;
    private final DoctorRepository doctorRepo;
    private final OfficeRepository officeRepo;
    private final AppointmentTypeRepository typeRepo; // Necesaria para la duración
    private final DoctorScheduleRepository scheduleRepo; // Necesaria para validar horario laboral

    @Override
    public AppointmentResponse schedule(CreateAppointmentRequest req) {
        // 1. Validaciones de existencia y Estados (PDF 6.1)
        var patient = patientRepo.findById(req.patientId())
                .orElseThrow(() -> new NotFoundException("Patient %d not found".formatted(req.patientId())));
        if (patient.getStatus() != PatientStatus.ACTIVE) throw new IllegalStateException("Patient is not active");

        var doctor = doctorRepo.findById(req.doctorId())
                .orElseThrow(() -> new NotFoundException("Doctor %d not found".formatted(req.doctorId())));
        if (!doctor.getIsActive()) throw new IllegalStateException("Doctor is not active");

        var office = officeRepo.findById(req.officeId())
                .orElseThrow(() -> new NotFoundException("Office %d not found".formatted(req.officeId())));
        if (office.getStatus() != OfficeStatus.AVAILABLE) throw new IllegalStateException("Office is not available");

        // 2. Validación de tiempo pasado
        if (req.dateTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Cannot schedule an appointment in the past");
        }

        // 3. Cálculo de fin de cita (PDF 6.1)
        var type = typeRepo.findById(req.typeId())
                .orElseThrow(() -> new NotFoundException("Appointment Type not found"));
        LocalDateTime endAt = req.dateTime().plusMinutes(type.getDurationInMinutes());

        // 4. Validación de Traslapes usando los nuevos métodos de rango (PDF 6.1)
        if (repo.existsDoctorOverlap(req.doctorId(), req.dateTime(), endAt))
            throw new IllegalStateException("Doctor has an overlap");
        if (repo.existsOfficeOverlap(req.officeId(), req.dateTime(), endAt))
            throw new IllegalStateException("Office has an overlap");

        var appointment = Appointment.builder()
                .dateTime(req.dateTime())
                .endAt(endAt) // Asignamos el calculado
                .patient(patient)
                .doctor(doctor)
                .office(office)
                .appointmentType(type)
                .status(AppointmentStatus.SCHEDULED)
                .build();

        return AppointmentMapper.toResponse(repo.save(appointment));
    }

    @Override
    public void cancel(Long id, CancelAppointmentRequest req) {
        var appointment = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Appointment %d not found".formatted(id)));

        // Validación PDF 6.3: No cancelar completadas
        if (appointment.getStatus() == AppointmentStatus.COMPLETED)
            throw new IllegalStateException("Cannot cancel a completed appointment");

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setCancelReason(req.reason()); // Usamos el motivo obligatorio
    }

    // Métodos adicionales para cumplir con 6.2, 6.4 y 6.5
    public void confirm(Long id) {
        var a = repo.findById(id).orElseThrow(() -> new NotFoundException("Not found"));
        if (a.getStatus() != AppointmentStatus.SCHEDULED) throw new IllegalStateException("Invalid state");
        a.setStatus(AppointmentStatus.CONFIRMED);
    }

    public void complete(Long id, String observations) {
        var a = repo.findById(id).orElseThrow(() -> new NotFoundException("Not found"));
        if (a.getStatus() != AppointmentStatus.CONFIRMED) throw new IllegalStateException("Must be CONFIRMED");
        a.setStatus(AppointmentStatus.COMPLETED);
        a.setObservations(observations);
    }

    @Override @Transactional(readOnly = true)
    public List<AppointmentResponse> findByPatientId(Long patientId) {
        return repo.findByPatientIdOrderByDateTimeDesc(patientId).stream()
                .map(AppointmentMapper::toResponse).toList();
    }
}