package edu.unimagdalena.RCMU.service.impl;

import edu.unimagdalena.RCMU.api.dto.DoctorDtos.*;
import edu.unimagdalena.RCMU.domine.repository.DoctorRepository;
import edu.unimagdalena.RCMU.domine.repository.SpecialityRepository;
import edu.unimagdalena.RCMU.api.error.ResourceNotFoundException;
import edu.unimagdalena.RCMU.service.DoctorService;
import edu.unimagdalena.RCMU.service.mappers.DoctorMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
@Transactional
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository repo;
    private final SpecialityRepository specialityRepo;

    @Override
    public DoctorResponse create(CreateDoctorRequest req) {
        var doctor = DoctorMapper.toEntity(req);

        var speciality = specialityRepo.findById(req.specialityId())
                .orElseThrow(() -> new ResourceNotFoundException("Speciality %d not found".formatted(req.specialityId())));

        doctor.setSpeciality(speciality);
        return DoctorMapper.toResponse(repo.save(doctor));
    }

    @Override
    public DoctorResponse update(Long id, UpdateDoctorRequest req) {
        var doctor = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor %d not found".formatted(id)));

        DoctorMapper.patch(doctor, req);
        return DoctorMapper.toResponse(doctor);
    }

    @Override
    @Transactional(readOnly = true)
    public DoctorResponse getById(Long id) {
        return repo.findById(id)
                .map(DoctorMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor %d not found".formatted(id)));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DoctorResponse> findAll(Pageable pageable) {
        return repo.findAll(pageable)
                .map(DoctorMapper::toResponse);
    }
}