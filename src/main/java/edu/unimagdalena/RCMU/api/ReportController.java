package edu.unimagdalena.RCMU.api;

import edu.unimagdalena.RCMU.api.dto.AnalyticsDtos.DoctorProductivityResponse;
import edu.unimagdalena.RCMU.api.dto.AnalyticsDtos.NoShowPatientResponse;
import edu.unimagdalena.RCMU.api.dto.OfficeDtos.OfficeOccupancyResponse;
import edu.unimagdalena.RCMU.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService service;

    @GetMapping("/office-occupancy")
    public ResponseEntity<List<OfficeOccupancyResponse>> getOfficeOccupancy() {
        return ResponseEntity.ok(service.getOfficeOccupancyReport());
    }

    @GetMapping("/doctor-productivity")
    public ResponseEntity<List<DoctorProductivityResponse>> getDoctorProductivity() {
        return ResponseEntity.ok(service.getDoctorProductivity());
    }

    @GetMapping("/no-show-patients")
    public ResponseEntity<List<NoShowPatientResponse>> getNoShowPatients() {
        return ResponseEntity.ok(service.getNoShowPatients());
    }
}