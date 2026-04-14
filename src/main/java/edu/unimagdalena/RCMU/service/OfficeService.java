package edu.unimagdalena.RCMU.services;

import edu.unimagdalena.RCMU.api.dto.OfficeDtos.*;
import java.util.List;

public interface OfficeService {
    OfficeResponse create(CreateOfficeRequest req);
    OfficeResponse update(Long id, UpdateOfficeRequest req);
    List<OfficeResponse> findAll();
}