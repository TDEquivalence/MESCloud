package com.alcegory.mescloud.utility;

import lombok.extern.java.Log;

import java.sql.Timestamp;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Log
public class DateUtil {

    private static final int INCLUDE_LAST_DAY = 1;
    private static final int MILLISECONDS_PER_MINUTE = 60 * 1000;

    private DateUtil() {
        //Utility class, not meant for instantiation
    }

    public static int spanInDays(Timestamp startDate, Timestamp endDate) {
        LocalDate startLocalDate = startDate.toLocalDateTime().toLocalDate();
        LocalDate endLocalDate = endDate.toLocalDateTime().toLocalDate();
        long differenceInDays = ChronoUnit.DAYS.between(startLocalDate, endLocalDate);
        return (int) differenceInDays + INCLUDE_LAST_DAY;
    }

    public static int differenceInDays(Timestamp startDate, Timestamp endDate) {
        long differenceInMillis = endDate.getTime() - startDate.getTime();
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

    public static long calculateDifferenceInMinutes(Date completedAt, Date createdAt) {
        return (completedAt.getTime() - createdAt.getTime()) / MILLISECONDS_PER_MINUTE;
    }
}
