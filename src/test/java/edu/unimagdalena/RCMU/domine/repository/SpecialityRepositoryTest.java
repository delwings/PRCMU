package edu.unimagdalena.RCMU.domine.repository;

import edu.unimagdalena.RCMU.domine.entity.Speciality;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Prueba de repositorio para la entidad Speciality.
 * Se asegura de que la persistencia y búsqueda por nombre funcionen correctamente.
 **/

class SpecialityRepositoryTest extends AbstractRepositoryIT {

    @Autowired
    private SpecialityRepository specialityRepository;

    @Test
    @DisplayName("Buscar especialidad por nombre exacto")
    void shouldFindByName() {
        // GIVEN
        Speciality speciality = Speciality.builder()
                .name("Cardiología")
                .description("Especialidad dedicada al corazón") // Evitamos null si el campo es obligatorio
                .build();

        specialityRepository.save(speciality);

        // WHEN
        Optional<Speciality> found = specialityRepository.findByName("Cardiología");

        // THEN
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Cardiología");
        assertThat(found.get().getDescription()).isEqualTo("Especialidad dedicada al corazón");
    }
}