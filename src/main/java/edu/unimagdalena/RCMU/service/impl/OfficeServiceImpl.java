package edu.unimagdalena.RCMU.services.impl;

import edu.unimagdalena.RCMU.api.dto.OfficeDtos.*;
import edu.unimagdalena.RCMU.domine.repositories.OfficeRepository;
import edu.unimagdalena.RCMU.exception.NotFoundException;
import edu.unimagdalena.RCMU.services.OfficeService;
import edu.unimagdalena.RCMU.services.mapper.OfficeMapper;
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
                .orElseThrow(() -> new NotFoundException("Office %d not found".formatted(id)));
        OfficeMapper.patch(office, req);
        return OfficeMapper.toResponse(office);
    }

    @Override @Transactional(readOnly = true)
    public List<OfficeResponse> findAll() {
        return repo.findAll().stream().map(OfficeMapper::toResponse).toList();
    }
}