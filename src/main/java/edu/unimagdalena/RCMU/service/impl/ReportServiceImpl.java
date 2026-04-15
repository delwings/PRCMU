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
        // Usamos la query JPQL optimizada del repositorio
        return appointmentRepo.countCompletedAppointmentsByDoctor().stream()
                .map(obj -> {
                    edu.unimagdalena.RCMU.domine.entity.Doctor d = (edu.unimagdalena.RCMU.domine.entity.Doctor) obj[0];
                    Long count = (Long) obj[1];
                    return new DoctorProductivityResponse(
                            d.getFirstName() + " " + d.getLastName(),
                            count,
                            100.0 // Eficiencia base
                    );
                }).toList();
    }

    @Override
    public List<NoShowPatientResponse> getNoShowPatients() {
        // Usamos la query JPQL optimizada del repositorio para NO_SHOW (Punto 6.5)
        return appointmentRepo.countNoShowsByPatient().stream()
                .map(obj -> {
                    edu.unimagdalena.RCMU.domine.entity.Patient p = (edu.unimagdalena.RCMU.domine.entity.Patient) obj[0];
                    Long count = (Long) obj[1];
                    return new NoShowPatientResponse(
                            p.getFirstName() + " " + p.getLastName(),
                            p.getDocumentId(),
                            count
                    );
                }).toList();
    }

    @Override
    public List<OfficeOccupancyResponse> getOfficeOccupancyReport() {
        return officeRepo.findAll().stream()
                .map(o -> {
                    // Contamos citas no canceladas para este consultorio
                    long count = appointmentRepo.findAll().stream() // O crear un countByOffice en repo
                            .filter(a -> a.getOffice().getId().equals(o.getId()))
                            .filter(a -> a.getStatus() != AppointmentStatus.CANCELLED)
                            .count();
                    return new OfficeOccupancyResponse(o.getRoomNumber(), count, count * 5.0);
                }).toList();
    }
}