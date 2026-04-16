package edu.unimagdalena.RCMU.service;

import edu.unimagdalena.RCMU.api.dto.DoctorDtos.*;
import java.util.List;

public interface DoctorService {
    DoctorResponse create(CreateDoctorRequest req);
    DoctorResponse update(Long id, UpdateDoctorRequest req);
    DoctorResponse getById(Long id);
    List<DoctorResponse> findAll();
}