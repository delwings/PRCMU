package edu.unimagdalena.RCMU.services.impl;

import edu.unimagdalena.RCMU.api.dto.SpecialityDtos.*;
import edu.unimagdalena.RCMU.domine.repository.SpecialityRepository;
import edu.unimagdalena.RCMU.services.SpecialityService;
import edu.unimagdalena.RCMU.services.mapper.SpecialityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SpecialityServiceImpl implements SpecialityService {

    private final SpecialityRepository repo;

    @Override
    public SpecialityResponse create(CreateSpecialityRequest req) {
        var entity = SpecialityMapper.toEntity(req);
        return SpecialityMapper.toResponse(repo.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SpecialityResponse> findAll() {
        return repo.findAll().stream()
                .map(SpecialityMapper::toResponse)
                .toList();
    }
}