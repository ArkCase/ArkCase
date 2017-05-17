package com.armedia.acm.calendar;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity May 15, 2017
 *
 */
public class DateTimeAdjuster
{

    /**
     * @param text
     * @return
     */
    public static String adjustDateTimeString(String text)
    {
        StringBuilder dateText = new StringBuilder(text);
        if (dateText.charAt(10) == ' ')
        {
            dateText.replace(10, 11, "T");
        }
        int columnIndex = dateText.indexOf("[");
        if (columnIndex < 0)
        {
            columnIndex = dateText.length();
        }
        if (dateText.charAt(columnIndex - 3) != ':')
        {
            dateText.insert(columnIndex - 2, ":");
        }
        if (dateText.indexOf("[") < 0)
        {
            ZonedDateTime zdt = ZonedDateTime.parse(dateText, DateTimeFormatter.ISO_DATE_TIME);
            Date date = Date.from(zdt.toInstant());
            TimeZone timeZone = TimeZone.getTimeZone(zdt.getZone());
            for (String zone : TimeZone.getAvailableIDs(timeZone.getOffset(date.getTime())))
            {
                if (zone.startsWith("Etc/"))
                {
                    dateText.append('[').append(zone).append(']');
                }
            }
        }
        return dateText.toString();
    }

    private static final String UTC = "(UTC";

    /**
     * @param msName
     * @return
     */
    public static String guessTimeZone(String msName)
    {
        // (UTC+02:00) Harare, Pretoria
        if (msName.contains(UTC) && msName.contains(")") && (msName.contains("+") || msName.contains("-")))
        {
            StringBuilder tzBuilder = new StringBuilder("GMT");
            final int signPosition = msName.lastIndexOf(UTC) + UTC.length();
            char charAt = msName.charAt(signPosition);
            String offset = msName.substring(signPosition + 1, msName.indexOf(')'));
            if (charAt == '+')
            {
                return tzBuilder.append('-').append(offset).toString();
            } else if (charAt == '-')
            {
                return tzBuilder.append('+').append(offset).toString();
            }
        }
        // was not possible to guess
        return "GMT+00:00";
    }

}
