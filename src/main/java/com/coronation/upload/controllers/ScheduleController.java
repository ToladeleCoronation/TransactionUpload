package com.coronation.upload.controllers;

import com.coronation.upload.domain.Schedule;
import com.coronation.upload.services.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Created by Toyin on 8/1/19.
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/schedules")
public class ScheduleController {
    private ScheduleService scheduleService;

    @Autowired
    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('INITIATOR')")
    public ResponseEntity<Schedule> createJob(@RequestBody @Valid Schedule schedule, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }
        if (scheduleService.findByName(schedule.getName()) != null ||
            !scheduleService.findByCronParams(schedule.getMinute(), schedule.getHour(), schedule.getPeriod()).isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return ResponseEntity.ok(scheduleService.save(schedule));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('INITIATOR')")
    public ResponseEntity<Schedule> editJob(@PathVariable("id") Long id,
                                            @RequestBody @Valid Schedule schedule, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }
        Schedule previous = scheduleService.findById(id);
        if (previous == null) {
            return ResponseEntity.notFound().build();
        }
        Schedule duplicateName = scheduleService.findByName(schedule.getName());
        List<Schedule> duplicatePeriod =
            scheduleService.findByCronParamsExcludeId(schedule.getMinute(), schedule.getHour(), schedule.getPeriod(), id);

        if ((duplicateName != null && !duplicateName.getId().equals(id)) ||
                !duplicatePeriod.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return ResponseEntity.ok(scheduleService.edit(previous, schedule));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Schedule> findById(@PathVariable("id") Long id) {
        Schedule schedule = scheduleService.findById(id);
        if (schedule == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(schedule);
    }

    @GetMapping
    public ResponseEntity<List<Schedule>> findAll() {
        return ResponseEntity.ok(scheduleService.findAll());
    }
}
