package edu.unimagdalena.RCMU.service;

import edu.unimagdalena.RCMU.api.dto.PatientDtos.*;
import java.util.List;

public interface PatientService {
    PatientResponse create(CreatePatientRequest req);
    PatientResponse update(Long id, UpdatePatientRequest req);
    PatientResponse getById(Long id);
    List<PatientResponse> findAll();
    void delete(Long id);
}