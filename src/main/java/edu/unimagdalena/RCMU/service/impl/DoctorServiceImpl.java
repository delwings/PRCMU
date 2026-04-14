package edu.unimagdalena.RCMU.services.impl;

import edu.unimagdalena.RCMU.api.dto.DoctorDtos.*;
import edu.unimagdalena.RCMU.domine.repositories.DoctorRepository;
import edu.unimagdalena.RCMU.domine.repositories.SpecialityRepository;
import edu.unimagdalena.RCMU.exception.NotFoundException;
import edu.unimagdalena.RCMU.services.DoctorService;
import edu.unimagdalena.RCMU.services.mapper.DoctorMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
                .orElseThrow(() -> new NotFoundException("Speciality %d not found".formatted(req.specialityId())));

        doctor.setSpeciality(speciality);
        return DoctorMapper.toResponse(repo.save(doctor));
    }

    @Override
    public DoctorResponse update(Long id, UpdateDoctorRequest req) {
        var doctor = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Doctor %d not found".formatted(id)));

        DoctorMapper.patch(doctor, req);
        return DoctorMapper.toResponse(doctor);
    }

    @Override
    @Transactional(readOnly = true)
    public DoctorResponse getById(Long id) {
        return repo.findById(id)
                .map(DoctorMapper::toResponse)
                .orElseThrow(() -> new NotFoundException("Doctor %d not found".formatted(id)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorResponse> findAll() {
        return repo.findAll().stream()
                .map(DoctorMapper::toResponse)
                .toList();
    }
}