package edu.unimagdalena.RCMU.service;

import edu.unimagdalena.RCMU.api.dto.DoctorScheduleDtos.*;
import java.util.List;

public interface DoctorScheduleService {
    // Firma actualizada para coincidir con el Controller
    DoctorScheduleResponse create(Long doctorId, CreateDoctorScheduleRequest req);
    List<DoctorScheduleResponse> findByDoctorId(Long doctorId);
}