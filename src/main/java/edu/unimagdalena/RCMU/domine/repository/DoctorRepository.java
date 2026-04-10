package edu.unimagdalena.RCMU.domine.repository;

import edu.unimagdalena.RCMU.domine.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    // Requisito: Listar doctores de una especialidad específica
    List<Doctor> findBySpecialityId(Long specialityId);

    // Buscar por apellido (útil para filtros de búsqueda)
    List<Doctor> findByLastNameContainingIgnoreCase(String lastName);
}