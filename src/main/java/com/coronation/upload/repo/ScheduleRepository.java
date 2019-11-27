package com.coronation.upload.repo;

import com.coronation.upload.domain.Schedule;
import com.coronation.upload.domain.enums.JobPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by Toyin on 7/26/19.
 */
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    @Query(value="Select s from Schedule s where "
            + "s.minute = ?1 and s.hour = ?2 and s.period = ?3")
    List<Schedule> findByCronParams(Integer minute, Integer hour, JobPeriod period);
    Schedule findByNameEquals(String name);
    @Query(value="Select s from Schedule s where "
            + "s.minute = ?1 and s.hour = ?2 and s.period = ?3 and s.id <> ?4")
    List<Schedule> findByCronParamsExcludeId(Integer minute, Integer hour, JobPeriod period, Long scheduleId);
}
