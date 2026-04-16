package edu.unimagdalena.RCMU.service;

import edu.unimagdalena.RCMU.api.dto.DoctorScheduleDtos.*;
import java.util.List;

public interface DoctorScheduleService {
    DoctorScheduleResponse create(CreateDoctorScheduleRequest req);
    List<DoctorScheduleResponse> findByDoctorId(Long doctorId);
}