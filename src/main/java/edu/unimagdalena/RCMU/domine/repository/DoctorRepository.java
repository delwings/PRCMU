package edu.unimagdalena.RCMU.domine.repository;

import edu.unimagdalena.RCMU.domine.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    // JPQL: Listar doctores filtrando por el nombre de la especialidad (Case Insensitive)
    @Query("SELECT d FROM Doctor d JOIN d.speciality s WHERE LOWER(s.name) = LOWER(:specialityName) AND d.isActive = true")
    List<Doctor> findActiveBySpecialityName(@Param("specialityName") String specialityName);

    // Buscar doctores por coincidencia parcial en el apellido
    List<Doctor> findByLastNameContainingIgnoreCase(String lastName);
}