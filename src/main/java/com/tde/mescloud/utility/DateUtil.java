package com.tde.mescloud.utility;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateUtil {

    private DateUtil() {
    }

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

    public static boolean isSameDay(Date firstDate, Date secondDate) {
        Calendar firstCalendar = toCalendar(firstDate);
        Calendar secondCalendar = toCalendar(secondDate);
        return isSameDay(firstCalendar, secondCalendar);
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

    public static int spanInDays(Date startDate, Date endDate) {

        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(startDate);
        truncateToDays(startCalendar);

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(endDate);
        truncateToDays(endCalendar);

        long differenceInMillis = endCalendar.getTimeInMillis() - startCalendar.getTimeInMillis();
        return (int) TimeUnit.MILLISECONDS.toDays(differenceInMillis);
    }

    public static int differenceInDays(Date startDate, Date endDate) {

        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(startDate);
        truncateToDays(startCalendar);

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(endDate);
        truncateToDays(endCalendar);

        long differenceInMillis = endCalendar.getTimeInMillis() - startCalendar.getTimeInMillis();
        return (int) TimeUnit.MILLISECONDS.toDays(differenceInMillis);
    }

    public static Calendar toCalendar(Date date) {
        Calendar calendarToCompare = Calendar.getInstance();
        calendarToCompare.setTime(date);
        return calendarToCompare;
    }

    public static Date getPreviousDay(Date inputDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(inputDate);

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        calendar.add(Calendar.DAY_OF_MONTH, -1);

        return calendar.getTime();
    }

    public static Date convertToDate(String dateAsString) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;
        Instant instant = Instant.from(formatter.parse(dateAsString));
        return Date.from(instant);
    }

    public static int getCurrentYearLastTwoDigits() {
        return Calendar.getInstance().get(Calendar.YEAR) % 100;
    }

    public static Instant convertToInstant(String dateAsString) {
        return Instant.parse(dateAsString);
    }
}
