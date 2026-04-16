package edu.unimagdalena.RCMU.service;

import edu.unimagdalena.RCMU.api.dto.AnalyticsDtos.*;
import edu.unimagdalena.RCMU.api.dto.OfficeDtos.OfficeOccupancyResponse;
import java.util.List;

public interface ReportService {
    List<DoctorProductivityResponse> getDoctorProductivity();
    List<NoShowPatientResponse> getNoShowPatients();
    List<OfficeOccupancyResponse> getOfficeOccupancyReport();
}