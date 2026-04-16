package edu.unimagdalena.RCMU.services;

import edu.unimagdalena.RCMU.api.dto.SpecialityDtos.*;
import java.util.List;

public interface SpecialityService {
    SpecialityResponse create(CreateSpecialityRequest req);
    List<SpecialityResponse> findAll();
}