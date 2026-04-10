package edu.unimagdalena.RCMU.domine.repository;

import edu.unimagdalena.RCMU.domine.entity.DoctorSchedule;
import edu.unimagdalena.RCMU.domine.enums.DayOfWeek;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface DoctorScheduleRepository extends JpaRepository<DoctorSchedule, Long> {
    // Busca los horarios asignados a un doctor específico
    List<DoctorSchedule> findByDoctorId(Long doctorId);

    // Filtra horarios por doctor y día de la semana usando el Enum DayOfWeek
    List<DoctorSchedule> findByDoctorIdAndDayOfWeek(Long doctorId, DayOfWeek dayOfWeek);
}