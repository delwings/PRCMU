package edu.unimagdalena.RCMU.services.impl;

import edu.unimagdalena.RCMU.api.dto.AppointmentTypeDtos.*;
import edu.unimagdalena.RCMU.domine.repositories.AppointmentTypeRepository;
import edu.unimagdalena.RCMU.services.AppointmentTypeService;
import edu.unimagdalena.RCMU.services.mapper.AppointmentTypeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AppointmentTypeServiceImpl implements AppointmentTypeService {

    private final AppointmentTypeRepository repo;

    @Override
    public AppointmentTypeResponse create(CreateAppointmentTypeRequest req) {
        var entity = AppointmentTypeMapper.toEntity(req);
        return AppointmentTypeMapper.toResponse(repo.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentTypeResponse> findAll() {
        return repo.findAll().stream()
                .map(AppointmentTypeMapper::toResponse)
                .toList();
    }
}