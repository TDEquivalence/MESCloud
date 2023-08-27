package com.tde.mescloud.util;

import com.tde.mescloud.utility.DateUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.TimeZone;

public class DateUtilTest {

    private static final SimpleDateFormat ISO_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    private static final TimeZone UTC_TIME_ZONE = TimeZone.getTimeZone("UTC");

    static {
        ISO_DATE_FORMAT.setTimeZone(UTC_TIME_ZONE);
    }

    @Test
    public void testSpanInDays() {
        String startDateString = "2023-08-26T00:00:00.000Z";
        String endDateString = "2023-08-27T23:59:59.999Z";

        Date startDate = DateUtil.convertToDate(startDateString);
        Date endDate = DateUtil.convertToDate(endDateString);

        int span = DateUtil.spanInDays(startDate, endDate);

        long differenceInMillis = endDate.getTime() - startDate.getTime();
//        int expectedSpan = (int) (differenceInMillis / (24 * 60 * 60 * 1000)) + 1;

        Assertions.assertEquals(2, span);
    }

    @Test
    public void testDifferenceInDays() {
        String startDateString = "2023-08-26T00:00:00.000Z";
        String endDateString = "2023-08-27T23:59:59.999Z";

        Date startDate = DateUtil.convertToDate(startDateString);
        Date endDate = DateUtil.convertToDate(endDateString);

        int difference = DateUtil.differenceInDays(startDate, endDate);

        // Calculate expected difference manually
        long differenceInMillis = endDate.getTime() - startDate.getTime();
        int expectedDifference = (int) (differenceInMillis / (24 * 60 * 60 * 1000));

        Assertions.assertEquals(expectedDifference, difference);
    }


    @Test
    public void testConvertToDate() {
        String dateAsString = "2023-08-26T00:00:00.000Z";
        Date convertedDate = DateUtil.convertToDate(dateAsString);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        String convertedDateAsString = dateFormat.format(convertedDate);

        Assertions.assertEquals(dateAsString, convertedDateAsString);
    }
}
