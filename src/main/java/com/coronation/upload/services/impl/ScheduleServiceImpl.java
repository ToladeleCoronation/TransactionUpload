package com.coronation.upload.services.impl;

import com.coronation.upload.domain.Schedule;
import com.coronation.upload.domain.enums.JobPeriod;
import com.coronation.upload.repo.ScheduleRepository;
import com.coronation.upload.services.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Toyin on 7/26/19.
 */
@Service
public class ScheduleServiceImpl implements ScheduleService {
    private ScheduleRepository scheduleRepository;

    @Autowired
    public ScheduleServiceImpl(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    @Override
    public Schedule findById(Long id) {
        return scheduleRepository.findById(id).orElse(null);
    }

    @Override
    public Schedule findByName(String name) {
        return scheduleRepository.findByNameEquals(name);
    }

    @Override
    public List<Schedule> findAll() {
        return scheduleRepository.findAll();
    }

    @Override
    public Schedule save(Schedule schedule) {
        return scheduleRepository.saveAndFlush(schedule);
    }

    @Override
    public Schedule edit(Schedule previous, Schedule newSchedule) {
        previous.setHour(newSchedule.getHour());
        previous.setMinute(newSchedule.getMinute());
        previous.setPeriod(newSchedule.getPeriod());
        previous.setModifiedAt(LocalDateTime.now());
        previous.setName(newSchedule.getName());
        return scheduleRepository.saveAndFlush(previous);
    }

    @Override
    public List<Schedule> findByCronParams(Integer minute, Integer hour, JobPeriod period) {
        return scheduleRepository.findByCronParams(minute, hour, period);
    }

    @Override
    public List<Schedule> findByCronParamsExcludeId(Integer minute, Integer hour, JobPeriod period, Long jobId) {
        return scheduleRepository.findByCronParamsExcludeId(minute, hour, period, jobId);
    }
}
