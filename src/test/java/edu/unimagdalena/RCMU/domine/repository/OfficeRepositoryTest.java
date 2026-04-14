package edu.unimagdalena.RCMU.domine.repository;

import edu.unimagdalena.RCMU.domine.entity.Office;
import edu.unimagdalena.RCMU.domine.enums.OfficeStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

class OfficeRepositoryTest extends AbstractRepositoryIT {

    @Autowired
    private OfficeRepository officeRepository;

    @Test
    @DisplayName("Buscar consultorio por número de habitación")
    void shouldFindByRoomNumber() {
        // GIVEN
        Office office = Office.builder()
                .roomNumber("101")
                .location("Piso 1")
                .status(OfficeStatus.AVAILABLE)
                .build();
        officeRepository.save(office);

        // WHEN
        Optional<Office> found = officeRepository.findByRoomNumber("101");

        // THEN
        assertThat(found).isPresent();
        assertThat(found.get().getRoomNumber()).isEqualTo("101");
        assertThat(found.get().getLocation()).isEqualTo("Piso 1");
    }
}