package com.tde.mescloud.utility;

import lombok.extern.java.Log;

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
        Date currentUtcDate = new Date(currentTimeMillis);

        return currentUtcDate;
    }

    public static Date getCurrentTime(String timeZoneId) {
        try {
            ZoneId zoneId = ZoneId.of(timeZoneId);
            LocalDateTime localDateTime = LocalDateTime.now(zoneId);
            return Date.from(localDateTime.atZone(zoneId).toInstant());
        } catch (Exception e) {
            log.severe("Unable to provide current time based on time zone - providing local time instead");
            return new Date();
        }
    }
}
