package com.coronation.upload.domain.enums;

import java.time.DayOfWeek;

/**
 * Created by Toyin on 7/25/19.
 */
public enum JobPeriod {
    DAILY, END_OF_MONTH, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY;

    public static JobPeriod getPeriodFromWeekDay(DayOfWeek dayOfWeek) {
        switch (dayOfWeek) {
            case MONDAY: return JobPeriod.MONDAY;
            case TUESDAY: return JobPeriod.TUESDAY;
            case WEDNESDAY: return JobPeriod.WEDNESDAY;
            case THURSDAY: return JobPeriod.THURSDAY;
            case FRIDAY: return JobPeriod.FRIDAY;
            case SATURDAY: return JobPeriod.SATURDAY;
            case SUNDAY: return JobPeriod.SUNDAY;
            default: throw new IllegalArgumentException("Invalid enum");
        }
    }
}
