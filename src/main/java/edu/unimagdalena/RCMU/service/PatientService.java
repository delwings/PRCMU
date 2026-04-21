package edu.unimagdalena.RCMU.service;

import edu.unimagdalena.RCMU.api.dto.PatientDtos.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PatientService {
    PatientResponse create(CreatePatientRequest req);
    PatientResponse update(Long id, UpdatePatientRequest req);
    PatientResponse getById(Long id);
    Page<PatientResponse> findAll(Pageable pageable); // Firma actualizada
    void delete(Long id);
}