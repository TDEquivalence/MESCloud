package com.tde.mescloud.utility;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateUtil {

    private static final int INCLUDE_START_AND_END_DATE = 1;

    public static boolean isDayBefore(Date dateToCompare, Date referenceDate) {

        if (dateToCompare == null || referenceDate == null) {
            throw new IllegalArgumentException("Dates cannot be null.");
        }

        Calendar calendarToCompare = Calendar.getInstance();
        calendarToCompare.setTime(dateToCompare);
        truncateToDays(calendarToCompare);

        Calendar referenceCalendar = Calendar.getInstance();
        referenceCalendar.setTime(referenceDate);
        truncateToDays(referenceCalendar);

        return calendarToCompare.before(referenceCalendar) &&
                !isSameDay(calendarToCompare, referenceCalendar);
    }

    public static void truncateToDays(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    public static boolean isSameDay(Calendar firstCalendar, Calendar secondCalendar) {
        return firstCalendar.get(Calendar.YEAR) == secondCalendar.get(Calendar.YEAR) &&
                firstCalendar.get(Calendar.MONTH) == secondCalendar.get(Calendar.MONTH) &&
                firstCalendar.get(Calendar.DAY_OF_MONTH) == secondCalendar.get(Calendar.DAY_OF_MONTH);
    }

    public static long calculateDaysBetween(Date startDate, Date endDate) {

        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(startDate);
        truncateToDays(startCalendar);

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(endDate);
        truncateToDays(endCalendar);

        long differenceInMillis = endCalendar.getTimeInMillis() - startCalendar.getTimeInMillis();
        return TimeUnit.MILLISECONDS.toDays(differenceInMillis) + INCLUDE_START_AND_END_DATE;
    }
}
