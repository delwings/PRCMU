package edu.unimagdalena.RCMU.service.impl;

import edu.unimagdalena.RCMU.api.dto.OfficeDtos.*;
import edu.unimagdalena.RCMU.domine.repository.OfficeRepository;
import edu.unimagdalena.RCMU.api.error.ResourceNotFoundException;
import edu.unimagdalena.RCMU.service.OfficeService;
import edu.unimagdalena.RCMU.service.mappers.OfficeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service @RequiredArgsConstructor @Transactional
public class OfficeServiceImpl implements OfficeService {
    private final OfficeRepository repo;

    @Override
    public OfficeResponse create(CreateOfficeRequest req) {
        return OfficeMapper.toResponse(repo.save(OfficeMapper.toEntity(req)));
    }

    @Override
    public OfficeResponse update(Long id, UpdateOfficeRequest req) {
        var office = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Office %d not found".formatted(id)));
        OfficeMapper.patch(office, req);
        return OfficeMapper.toResponse(office);
    }

    @Override @Transactional(readOnly = true)
    public List<OfficeResponse> findAll() {
        return repo.findAll().stream().map(OfficeMapper::toResponse).toList();
    }
}