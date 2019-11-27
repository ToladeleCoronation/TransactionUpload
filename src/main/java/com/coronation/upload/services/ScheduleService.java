package com.coronation.upload.services;

import com.coronation.upload.domain.Schedule;
import com.coronation.upload.domain.enums.JobPeriod;

import java.util.List;

/**
 * Created by Toyin on 7/26/19.
 */
public interface ScheduleService {
    Schedule findById(Long id);
    Schedule findByName(String name);
    List<Schedule> findAll();
    Schedule save(Schedule schedule);
    Schedule edit(Schedule previous, Schedule newSchedule);

    List<Schedule> findByCronParams(Integer minute, Integer hour, JobPeriod period);

    List<Schedule> findByCronParamsExcludeId(Integer minute, Integer hour, JobPeriod period, Long jobId);
}
