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

        Instant startDate = DateUtil.convertToInstant(startDateString);
        Instant endDate = DateUtil.convertToInstant(endDateString);

        int span = DateUtil.spanInDays(startDate, endDate);

        Assertions.assertEquals(2, span);
    }

    @Test
    public void testDifferenceInDays() {
        String startDateString = "2023-08-26T00:00:00.000Z";
        String endDateString = "2023-08-27T23:59:59.999Z";

        Instant startDate = DateUtil.convertToInstant(startDateString);
        Instant endDate = DateUtil.convertToInstant(endDateString);

        int difference = DateUtil.differenceInDays(startDate, Date.from(endDate));

        // Calculate expected difference manually
        long differenceInMillis = Date.from(endDate).getTime() - Date.from(startDate).getTime();
        int expectedDifference = (int) (differenceInMillis / (24 * 60 * 60 * 1000));

        Assertions.assertEquals(expectedDifference, difference);
    }
}
