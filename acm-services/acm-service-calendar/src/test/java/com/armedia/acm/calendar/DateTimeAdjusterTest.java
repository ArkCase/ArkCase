package com.armedia.acm.calendar;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by riste.tutureski on 5/17/2017.
 */
public class DateTimeAdjusterTest
{
    @Test
    public void testGuessTimeZone() throws Exception
    {
        String expectedTimeZone = "Europe/Warsaw";
        String timezone = DateTimeAdjuster.guessTimeZone("Central European Standard Time");

        assertEquals(expectedTimeZone, timezone);
    }

    @Test
    public void testGuessTimeZoneNotFound() throws Exception
    {
        String defaultTimeZone = "Europe/Berlin";
        String timezone = DateTimeAdjuster.guessTimeZone("Wrong Microsoft TimeZone");

        assertEquals(defaultTimeZone, timezone);
    }
}
