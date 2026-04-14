package edu.unimagdalena.RCMU.services.impl;

import edu.unimagdalena.RCMU.api.dto.AnalyticsDtos.*;
import edu.unimagdalena.RCMU.api.dto.OfficeDtos.OfficeOccupancyResponse;
import edu.unimagdalena.RCMU.domine.enums.AppointmentStatus;
import edu.unimagdalena.RCMU.domine.repositories.AppointmentRepository;
import edu.unimagdalena.RCMU.domine.repositories.OfficeRepository;
import edu.unimagdalena.RCMU.services.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private final AppointmentRepository appointmentRepo;
    private final OfficeRepository officeRepo;

    @Override
    public List<DoctorProductivityResponse> getDoctorProductivity() {
        return appointmentRepo.findAll().stream()
                .filter(a -> a.getStatus() == AppointmentStatus.COMPLETED)
                .collect(java.util.stream.Collectors.groupingBy(a -> a.getDoctor().getLastName(),
                        java.util.stream.Collectors.counting()))
                .entrySet().stream()
                .map(entry -> new DoctorProductivityResponse(entry.getKey(), entry.getValue(), 100.0))
                .toList();
    }

    @Override
    public List<NoShowPatientResponse> getNoShowPatients() {
        return appointmentRepo.findAll().stream()
                .filter(a -> a.getStatus() == AppointmentStatus.CANCELLED)
                .map(a -> new NoShowPatientResponse(
                        a.getPatient().getFirstName() + " " + a.getPatient().getLastName(),
                        a.getPatient().getDocumentId(),
                        1L))
                .toList();
    }

    @Override
    public List<OfficeOccupancyResponse> getOfficeOccupancyReport() {
        return officeRepo.findAll().stream()
                .map(o -> new OfficeOccupancyResponse(o.getRoomNumber(), 0L, 0.0))
                .toList();
    }
}