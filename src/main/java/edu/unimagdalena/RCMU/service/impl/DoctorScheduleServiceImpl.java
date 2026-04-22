package edu.unimagdalena.RCMU.service.impl;

import edu.unimagdalena.RCMU.api.dto.DoctorScheduleDtos.*;
import edu.unimagdalena.RCMU.domine.repository.DoctorRepository;
import edu.unimagdalena.RCMU.domine.repository.DoctorScheduleRepository;
import edu.unimagdalena.RCMU.api.error.ResourceNotFoundException;
import edu.unimagdalena.RCMU.service.DoctorScheduleService;
import edu.unimagdalena.RCMU.service.mappers.DoctorScheduleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DoctorScheduleServiceImpl implements DoctorScheduleService {
    private final DoctorScheduleRepository repo;
    private final DoctorRepository doctorRepo;

    @Override
    public DoctorScheduleResponse create(Long doctorId, CreateDoctorScheduleRequest req) {
        var doctor = doctorRepo.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor %d not found".formatted(doctorId)));

        var schedule = DoctorScheduleMapper.toEntity(req);
        schedule.setDoctor(doctor);

        return DoctorScheduleMapper.toResponse(repo.save(schedule));
    }

    @Override @Transactional(readOnly = true)
    public List<DoctorScheduleResponse> findByDoctorId(Long doctorId) {
        return repo.findByDoctorId(doctorId).stream()
                .map(DoctorScheduleMapper::toResponse).toList();
    }
}