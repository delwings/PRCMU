package edu.unimagdalena.RCMU.domine.repository;

import edu.unimagdalena.RCMU.domine.entity.Doctor;
import edu.unimagdalena.RCMU.domine.entity.Speciality;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DoctorRepositoryTest extends AbstractRepositoryIT {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private SpecialityRepository specialityRepository;

    @Test
    @DisplayName("1. JPQL: Buscar doctores activos por nombre de especialidad")
    void shouldFindActiveDoctorsBySpecialityName() {
        // GIVEN: Una especialidad persistida y un doctor activo asociado
        Speciality spec = specialityRepository.save(Speciality.builder()
                .name("Cardiología")
                .build());

        doctorRepository.save(Doctor.builder()
                .firstName("Juan")
                .lastName("Pérez")
                .email("juan.perez@rcmu.edu.co")
                .speciality(spec)
                .isActive(true)
                .build());

        // WHEN: Buscamos por el nombre de la especialidad en minúsculas (Case Insensitive)
        List<Doctor> result = doctorRepository.findActiveBySpecialityName("cardiología");

        // THEN: Debería retornar el doctor Pérez
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getLastName()).isEqualTo("Pérez");
        assertThat(result.getFirst().getIsActive()).isTrue();
    }

    @Test
    @DisplayName("2. Query Method: Buscar doctores por coincidencia parcial en apellido")
    void shouldFindByLastNameContaining() {
        // GIVEN: Dos doctores con apellidos similares
        doctorRepository.save(Doctor.builder()
                .firstName("Ana")
                .lastName("García")
                .email("ana.garcia@rcmu.edu.co")
                .speciality(specialityRepository.save(new Speciality(null, "Pediatría", null)))
                .isActive(true)
                .build());

        doctorRepository.save(Doctor.builder()
                .firstName("Luis")
                .lastName("Garcés")
                .email("luis.garces@rcmu.edu.co")
                .speciality(specialityRepository.save(new Speciality(null, "Neurología", null)))
                .isActive(true)
                .build());

        // WHEN: Buscamos por la raíz del apellido "Garc"
        List<Doctor> result = doctorRepository.findByLastNameContainingIgnoreCase("garc");

        // THEN: Debería encontrar a ambos doctores (García y Garcés)
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Doctor::getLastName)
                .containsExactlyInAnyOrder("García", "Garcés");
    }
}