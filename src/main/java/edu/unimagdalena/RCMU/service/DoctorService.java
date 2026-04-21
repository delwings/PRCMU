package edu.unimagdalena.RCMU.service;

import edu.unimagdalena.RCMU.api.dto.DoctorDtos.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface DoctorService {
    DoctorResponse create(CreateDoctorRequest req);
    DoctorResponse update(Long id, UpdateDoctorRequest req);
    DoctorResponse getById(Long id);
    Page<DoctorResponse> findAll(Pageable pageable);
}