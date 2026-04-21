package edu.unimagdalena.RCMU.service.impl;

import edu.unimagdalena.RCMU.api.dto.AppointmentDtos.*;
import edu.unimagdalena.RCMU.domine.entity.*;
import edu.unimagdalena.RCMU.domine.enums.*;
import edu.unimagdalena.RCMU.domine.repository.*;
import edu.unimagdalena.RCMU.api.error.ResourceNotFoundException;
import edu.unimagdalena.RCMU.service.AppointmentService;
import edu.unimagdalena.RCMU.service.mappers.AppointmentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AppointmentServiceImpl implements AppointmentService {
    private final AppointmentRepository repo;
    private final PatientRepository patientRepo;
    private final DoctorRepository doctorRepo;
    private final OfficeRepository officeRepo;
    private final AppointmentTypeRepository typeRepo;
    private final DoctorScheduleRepository scheduleRepo;

    @Override
    public AppointmentResponse schedule(CreateAppointmentRequest req) {
        var patient = patientRepo.findById(req.patientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
        var doctor = doctorRepo.findById(req.doctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
        var office = officeRepo.findById(req.officeId())
                .orElseThrow(() -> new ResourceNotFoundException("Office not found"));
        var type = typeRepo.findById(req.typeId())
                .orElseThrow(() -> new ResourceNotFoundException("Type not found"));

        // Regla 6.6: Validación de Horario Laboral
        var dayOfWeek = edu.unimagdalena.RCMU.domine.enums.DayOfWeek.valueOf(req.dateTime().getDayOfWeek().name());
        var schedules = scheduleRepo.findByDoctorId(doctor.getId());

        boolean worksThatTime = schedules.stream()
                .filter(s -> s.getDayOfWeek() == dayOfWeek)
                .anyMatch(s -> {
                    var time = req.dateTime().toLocalTime();
                    return !time.isBefore(s.getStartTime()) && !time.isAfter(s.getEndTime());
                });

        if (!worksThatTime) {
            throw new IllegalStateException("El doctor no atiende en el horario o día seleccionado.");
        }

        var entity = AppointmentMapper.toEntity(req);
        entity.setPatient(patient);
        entity.setDoctor(doctor);
        entity.setOffice(office);
        entity.setAppointmentType(type);
        entity.setEndAt(req.dateTime().plusMinutes(type.getDurationInMinutes()));
        entity.setStatus(AppointmentStatus.SCHEDULED);

        return AppointmentMapper.toResponse(repo.save(entity));
    }

    @Override
    public AppointmentResponse confirm(Long id) {
        var a = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada"));
        if (a.getStatus() != AppointmentStatus.SCHEDULED)
            throw new IllegalStateException("Solo se pueden confirmar citas programadas");
        a.setStatus(AppointmentStatus.CONFIRMED);
        return AppointmentMapper.toResponse(repo.save(a));
    }

    @Override
    public AppointmentResponse complete(Long id, String observations) {
        var a = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada"));
        if (a.getStatus() != AppointmentStatus.CONFIRMED)
            throw new IllegalStateException("La cita debe estar confirmada para completarse");
        if (observations == null || observations.isBlank())
            throw new IllegalArgumentException("Las observaciones son obligatorias para completar la cita");

        a.setStatus(AppointmentStatus.COMPLETED);
        a.setObservations(observations);
        return AppointmentMapper.toResponse(repo.save(a));
    }

    @Override
    public AppointmentResponse markAsNoShow(Long id) {
        var a = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada"));
        a.setStatus(AppointmentStatus.NO_SHOW);
        return AppointmentMapper.toResponse(repo.save(a));
    }

    @Override @Transactional(readOnly = true)
    public List<AppointmentResponse> getAll() {
        return repo.findAll().stream().map(AppointmentMapper::toResponse).toList();
    }

    @Override
    public void cancel(Long id, CancelAppointmentRequest req) {
        var a = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada"));
        if (a.getStatus() == AppointmentStatus.COMPLETED)
            throw new IllegalStateException("No se puede cancelar una cita completada");
        a.setStatus(AppointmentStatus.CANCELLED);
        a.setCancelReason(req.reason());
        repo.save(a);
    }

    @Override @Transactional(readOnly = true)
    public List<AppointmentResponse> findByPatientId(Long patientId) {
        return repo.findByPatientIdOrderByDateTimeDesc(patientId).stream()
                .map(AppointmentMapper::toResponse).toList();
    }
}