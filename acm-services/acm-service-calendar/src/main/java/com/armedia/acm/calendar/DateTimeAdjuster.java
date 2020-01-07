package com.armedia.acm.calendar;

/*-
 * #%L
 * ACM Service: Calendar Service
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import microsoft.exchange.webservices.data.util.TimeZoneUtils;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity May 15, 2017
 *
 */
public class DateTimeAdjuster
{
    private static final String UTC = "(UTC";
    public static Logger LOG = LogManager.getLogger(DateTimeAdjuster.class);

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
                timeZone = microsoftTimeZones.entrySet().stream().filter(entry -> entry.getValue().equals(msName)).map(Map.Entry::getKey)
                        .findFirst().orElse(timeZone);
            }
        }
        catch (Exception e)
        {
            LOG.warn("Cannot take Java TimeZone name from Microsoft TimeZone name = [{}]. Default TimeZone [{}] will be used instead.",
                    msName, timeZone);
        }

        return timeZone;
    }

}
