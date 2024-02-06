package com.alcegory.mescloud.utility;

import lombok.extern.java.Log;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Log
public class DateUtil {

    private DateUtil() {
        //Utility class, not meant for instantiation
    }

    private static final int INCLUDE_LAST_DAY = 1;

    public static int spanInDays(Instant startDate, Instant endDate) {
        long differenceInDays = ChronoUnit.DAYS.between(startDate.truncatedTo(ChronoUnit.DAYS), endDate.truncatedTo(ChronoUnit.DAYS));
        return (int) differenceInDays + INCLUDE_LAST_DAY;
    }

    public static int differenceInDays(Instant startDate, Date endDate) {
        long differenceInMillis = endDate.getTime() - startDate.toEpochMilli();
        return (int) TimeUnit.MILLISECONDS.toDays(differenceInMillis);
    }

    public static int getCurrentYearLastTwoDigits() {
        return Calendar.getInstance().get(Calendar.YEAR) % 100;
    }

    public static Instant convertToInstant(String dateAsString) {
        return Instant.parse(dateAsString);
    }

    public static Date getCurrentUtcDate() {
        long currentTimeMillis = System.currentTimeMillis();
        return new Date(currentTimeMillis);
    }

    public static LocalDateTime getCurrentTime(String timeZoneId) {
        ZoneId zoneId = ZoneId.of(timeZoneId);
        return LocalDateTime.now(zoneId);
    }

    public static Instant determineCompletedAt(Date completedAtDate, Timestamp endDate) {
        Instant completedAtInstant;

        if (completedAtDate != null) {
            completedAtInstant = completedAtDate.toInstant();

            if (completedAtDate.toInstant().isAfter(endDate.toInstant())) {
                completedAtInstant = endDate.toInstant();
            }
        } else {
            completedAtInstant = endDate.toInstant();
        }

        return completedAtInstant;
    }

    public static Long calculateScheduledTimeInSeconds(Timestamp startDate, Timestamp endDate) {
        Duration productionScheduleTime = calculateScheduledTime(startDate, endDate);

        if (isInclusiveEnd(endDate)) {
            productionScheduleTime = productionScheduleTime.plusSeconds(1);
        }

        return productionScheduleTime.toSeconds();
    }

    private static boolean isInclusiveEnd(Timestamp endDate) {
        Instant endInstant = endDate.toInstant();
        return endInstant.getNano() > 0 || endInstant.getEpochSecond() % 60 > 0;
    }

    private static Duration calculateScheduledTime(Timestamp startDate, Timestamp endDate) {
        Instant startInstant = startDate.toInstant();
        Instant endInstant = endDate.toInstant();
        Duration duration = Duration.between(startInstant, endInstant);
        return duration.isNegative() ? Duration.ZERO : duration;
    }
}
