package edu.unimagdalena.RCMU.domine.repository;

import edu.unimagdalena.RCMU.domine.entity.AppointmentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas para el repositorio de tipos de cita.
 * Verifica la persistencia y búsqueda por nombre.
 **/

class AppointmentTypeRepositoryTest extends AbstractRepositoryIT {

    @Autowired
    private AppointmentTypeRepository typeRepository;

    @Test
    @DisplayName("Evitar duplicados buscando por nombre de tipo")
    void shouldFindByName() {
        // GIVEN: Guardamos un tipo de cita con todos los campos obligatorios
        AppointmentType type = AppointmentType.builder()
                .name("Consulta General")
                .durationInMinutes(30)
                .build();

        typeRepository.save(type);

        // WHEN: Intentamos buscar ese mismo nombre
        Optional<AppointmentType> found = typeRepository.findByName("Consulta General");

        // THEN: Confirmamos que el repositorio lo encuentra y los datos coinciden
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Consulta General");
        assertThat(found.get().getDurationInMinutes()).isEqualTo(30);
    }
}