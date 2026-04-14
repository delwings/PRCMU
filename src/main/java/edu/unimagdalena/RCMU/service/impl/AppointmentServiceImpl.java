package edu.unimagdalena.RCMU.services.impl;

import edu.unimagdalena.RCMU.api.dto.AppointmentDtos.*;
import edu.unimagdalena.RCMU.domine.entity.*;
import edu.unimagdalena.RCMU.domine.enums.AppointmentStatus;
import edu.unimagdalena.RCMU.domine.repositories.*;
import edu.unimagdalena.RCMU.exception.NotFoundException;
import edu.unimagdalena.RCMU.services.AppointmentService;
import edu.unimagdalena.RCMU.services.mapper.AppointmentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service @RequiredArgsConstructor @Transactional
public class AppointmentServiceImpl implements AppointmentService {
    private final AppointmentRepository repo;
    private final PatientRepository patientRepo;
    private final DoctorRepository doctorRepo;
    private final OfficeRepository officeRepo;

    @Override
    public AppointmentResponse schedule(CreateAppointmentRequest req) {
        // Validación de traslape en el consultorio
        boolean conflict = repo.existsByOfficeIdAndDateTime(req.officeId(), req.dateTime());
        if (conflict) throw new IllegalStateException("Office %d is already occupied at this time".formatted(req.officeId()));

        var patient = patientRepo.findById(req.patientId())
                .orElseThrow(() -> new NotFoundException("Patient %d not found".formatted(req.patientId())));
        var doctor = doctorRepo.findById(req.doctorId())
                .orElseThrow(() -> new NotFoundException("Doctor %d not found".formatted(req.doctorId())));
        var office = officeRepo.findById(req.officeId())
                .orElseThrow(() -> new NotFoundException("Office %d not found".formatted(req.officeId())));

        var appointment = Appointment.builder()
                .dateTime(req.dateTime())
                .patient(patient)
                .doctor(doctor)
                .office(office)
                .status(AppointmentStatus.SCHEDULED)
                .build();

        return AppointmentMapper.toResponse(repo.save(appointment));
    }

    @Override
    public void cancel(Long id, CancelAppointmentRequest req) {
        var appointment = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Appointment %d not found".formatted(id)));
        appointment.setStatus(AppointmentStatus.CANCELLED);
    }

    @Override @Transactional(readOnly = true)
    public List<AppointmentResponse> findByPatientId(Long patientId) {
        return repo.findByPatientId(patientId).stream()
                .map(AppointmentMapper::toResponse).toList();
    }
}