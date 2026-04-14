package edu.unimagdalena.RCMU.services.impl;

import edu.unimagdalena.RCMU.api.dto.DoctorScheduleDtos.*;
import edu.unimagdalena.RCMU.domine.repositories.DoctorRepository;
import edu.unimagdalena.RCMU.domine.repositories.DoctorScheduleRepository;
import edu.unimagdalena.RCMU.exception.NotFoundException;
import edu.unimagdalena.RCMU.services.DoctorScheduleService;
import edu.unimagdalena.RCMU.services.mapper.DoctorScheduleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service @RequiredArgsConstructor @Transactional
public class DoctorScheduleServiceImpl implements DoctorScheduleService {
    private final DoctorScheduleRepository repo;
    private final DoctorRepository doctorRepo;

    @Override
    public DoctorScheduleResponse create(CreateDoctorScheduleRequest req) {
        var doctor = doctorRepo.findById(req.doctorId())
                .orElseThrow(() -> new NotFoundException("Doctor %d not found".formatted(req.doctorId())));

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