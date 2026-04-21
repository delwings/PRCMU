package edu.unimagdalena.RCMU.service.impl;

import edu.unimagdalena.RCMU.api.dto.PatientDtos.*;
import edu.unimagdalena.RCMU.domine.entity.Patient;
import edu.unimagdalena.RCMU.domine.repository.PatientRepository;
import edu.unimagdalena.RCMU.api.error.ResourceNotFoundException;
import edu.unimagdalena.RCMU.service.PatientService;
import edu.unimagdalena.RCMU.service.mappers.PatientMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PatientServiceImpl implements PatientService {

    private final PatientRepository repo;

    @Override
    public PatientResponse create(CreatePatientRequest req) {
        // Mapeo manual a entidad, persistencia y retorno de respuesta
        Patient patientSaved = repo.save(PatientMapper.toEntity(req));
        return PatientMapper.toResponse(patientSaved);
    }

    @Override
    public PatientResponse update(Long id, UpdatePatientRequest req) {
        // Busca la entidad, aplica los cambios parciales (patch) y guarda
        var patient = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient with ID %d not found".formatted(id)));

        PatientMapper.patch(patient, req);
        return PatientMapper.toResponse(patient);
    }

    @Override
    @Transactional(readOnly = true)
    public PatientResponse getById(Long id) {
        return repo.findById(id)
                .map(PatientMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Patient %d not found".formatted(id)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientResponse> findAll() {
        return repo.findAll().stream()
                .map(PatientMapper::toResponse)
                .toList();
    }

    @Override
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete: Patient %d not found".formatted(id));
        }
        repo.deleteById(id);
    }
}