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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

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
