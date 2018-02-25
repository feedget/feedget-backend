package kr.co.mashup.feedgetapi.common.util;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by ethan.kim on 2018. 2. 26..
 */
public class ZonedDateTimeUtils {

    /**
     * 해당 zone의 현재시간을 주어진 format으로 반환한다
     *
     * @param format the dateTime format
     * @param zoneId the zone ID
     * @return
     */
    public static String getCurrentZonedDateTimeWithFormat(String format, String zoneId) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format);
        ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of(zoneId));
        return dateTimeFormatter.format(zonedDateTime);
    }
}
