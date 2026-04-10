package edu.unimagdalena.RCMU.domine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Para los horarios de los doctores
@Repository
public interface DoctorScheduleRepository extends JpaRepository<edu.unimagdalena.RCMU.domine.entity.DoctorSchedule, Long> {
    java.util.List<edu.unimagdalena.RCMU.domine.entity.DoctorSchedule> findByDoctorId(Long doctorId);
}