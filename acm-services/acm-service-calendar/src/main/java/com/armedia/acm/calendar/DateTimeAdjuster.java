package com.armedia.acm.calendar;

import microsoft.exchange.webservices.data.util.TimeZoneUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity May 15, 2017
 *
 */
public class DateTimeAdjuster
{
    public static Logger LOG = LoggerFactory.getLogger(DateTimeAdjuster.class);

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
        String timeZone = "Europe/Berlin";

        try
        {
            Map<String, String> microsoftTimeZones = TimeZoneUtils.createOlsonTimeZoneToMsMap();
            if (microsoftTimeZones != null)
            {
                timeZone = microsoftTimeZones.entrySet().stream().filter(entry -> entry.getValue().equals(msName)).map(Map.Entry::getKey).findFirst().orElse(null);
            }
        }
        catch (Exception e)
        {
            LOG.warn("Cannot take Java TimeZone name from Microsoft TimeZone name = [{}]. Default TimeZone [{}] will be used instead.", msName, timeZone);
        }

        return timeZone;
    }

}
