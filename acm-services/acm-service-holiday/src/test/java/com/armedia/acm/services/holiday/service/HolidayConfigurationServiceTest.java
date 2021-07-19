package com.armedia.acm.services.holiday.service;

/*-
 * #%L
 * ACM Service: Holiday
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
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

import com.armedia.acm.services.holiday.model.HolidayConfigurationProps;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * @author ana.serafimoska
 */
public class HolidayConfigurationServiceTest extends EasyMockSupport {

    HolidayConfigurationService holidayConfigurationService = new HolidayConfigurationService();

    Yaml yaml = new Yaml();

    @Before
    public void setUp()
    {
    }

    private void setHolidayFile(String relativePath)
    {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(relativePath);
        Map holidayPropsMap = (Map) yaml.load(inputStream);
        HolidayConfigurationProps props = new HolidayConfigurationProps();
        props.setIncludeWeekends((Boolean) ((Map) holidayPropsMap.get("holidayConfiguration")).get("includeWeekends"));
        props.setHolidays((Map<String, String>) ((Map) holidayPropsMap.get("holidayConfiguration")).get("holidays"));
        holidayConfigurationService.setHolidayConfigurationProps(props);
    }

    @Test
    public void testAddWorkingDaysToDateWithoutWeekendsAndHolidaysIncluded()
    {

        setHolidayFile("test/holidayFile.yaml");

        LocalDate actualResult = holidayConfigurationService.addWorkingDaysToDate(LocalDate.parse("20181010", DateTimeFormatter.BASIC_ISO_DATE),
                6);

        LocalDate expectedResult = LocalDate.parse("20181018", DateTimeFormatter.BASIC_ISO_DATE);
        assertEquals(actualResult, expectedResult);

    }

    @Test
    public void testAddWorkingDaysToDateWithoutWeekendsIncluded()
    {

        //included "Thanksgiving Day": "2018-11-22"
        setHolidayFile("test/holidayFile.yaml");

        LocalDate actualResult = holidayConfigurationService.addWorkingDaysToDate(LocalDate.parse("20181122", DateTimeFormatter.BASIC_ISO_DATE),
                6);

        LocalDate expectedResult = LocalDate.parse("20181130", DateTimeFormatter.BASIC_ISO_DATE);
        assertEquals(actualResult, expectedResult);

    }

    @Test
    public void testAddWorkingDaysToDateWithWeekendsIncluded()
    {

        setHolidayFile("test/holidayFileIncludeWeekends.yaml");

        LocalDate actualResult = holidayConfigurationService.addWorkingDaysToDate(LocalDate.parse("20181009", DateTimeFormatter.BASIC_ISO_DATE),
                6);

        LocalDate expectedResult = LocalDate.parse("20181015", DateTimeFormatter.BASIC_ISO_DATE);
        assertEquals(actualResult, expectedResult);

    }

    @Test
    public void testAddWorkingDaysToDateWithWeekendsAndHolidayIncluded()
    {

        //included "Christmas Day": "2018-12-25"
        setHolidayFile("test/holidayFileIncludeWeekends.yaml");

        LocalDate actualResult = holidayConfigurationService.addWorkingDaysToDate(LocalDate.parse("20181224", DateTimeFormatter.BASIC_ISO_DATE),
                6);

        LocalDate expectedResult = LocalDate.parse("20181231", DateTimeFormatter.BASIC_ISO_DATE);
        assertEquals(actualResult, expectedResult);

    }

    @Test
    public void testFindNextWorkingDayWhenCurrentDayIsHoliday()
    {
        setHolidayFile("test/holidayFile.yaml");

        // memorials day 2018-05-28
        LocalDate testDate = LocalDate.parse("20180528", DateTimeFormatter.BASIC_ISO_DATE);
        LocalDate actualResult = holidayConfigurationService.getFirstWorkingDay(testDate);

        LocalDate expectedResult = LocalDate.parse("20180529", DateTimeFormatter.BASIC_ISO_DATE);
        assertEquals(actualResult, expectedResult);
    }

    @Test
    public void testFindNextWorkingDayWhenCurrentDayIsNonWorkingWeekend()
    {
        setHolidayFile("test/holidayFile.yaml");

        LocalDate currentDate = LocalDate.parse("20181222", DateTimeFormatter.BASIC_ISO_DATE);
        LocalDate actualResult = holidayConfigurationService.getFirstWorkingDay(currentDate);

        LocalDate expectedResult = LocalDate.parse("20181224", DateTimeFormatter.BASIC_ISO_DATE);

        assertEquals(actualResult, expectedResult);
    }

    @Test
    public void testFindNextWorkingDayWhenCurrentDayIsWorkingWeekend()
    {
        setHolidayFile("test/holidayFileIncludeWeekends.yaml");

        LocalDate currentDate = LocalDate.parse("20181222", DateTimeFormatter.BASIC_ISO_DATE);
        LocalDate actualResult = holidayConfigurationService.getFirstWorkingDay(currentDate);

        LocalDate expectedResult = LocalDate.parse("20181222", DateTimeFormatter.BASIC_ISO_DATE);

        assertEquals(actualResult, expectedResult);
    }

    @Test
    public void testCalculateDueDateNoWorkingWeekend()
    {
        setHolidayFile("test/holidayFile.yaml");

        LocalDate currentDate = LocalDate.parse("20200425", DateTimeFormatter.BASIC_ISO_DATE);
        LocalDate actualResult = holidayConfigurationService.getFirstWorkingDay(currentDate);

        LocalDate dueDate = holidayConfigurationService.addWorkingDaysToDate(actualResult, 20);

        LocalDate expectedResult = LocalDate.parse("20200427", DateTimeFormatter.BASIC_ISO_DATE);
        LocalDate expectedDueDate = LocalDate.parse("20200525", DateTimeFormatter.BASIC_ISO_DATE);

        assertEquals(actualResult, expectedResult);
        assertEquals(dueDate, expectedDueDate);
    }

    @Test
    public void calculateDateSubtractBy()
    {
        setHolidayFile("test/holidayFile.yaml");

        LocalDate currentDate = LocalDate.parse("20200525", DateTimeFormatter.BASIC_ISO_DATE);

        LocalDate result = holidayConfigurationService.subtractWorkingDaysFromDate(currentDate, 20);

        LocalDate expectedDueDate = LocalDate.parse("20200427", DateTimeFormatter.BASIC_ISO_DATE);

        assertEquals(expectedDueDate, result);
    }

    @Test
    public void testCalculateAmountOfWorkingDaysWithoutWeekendsAndHolidaysIncluded()
    {
        setHolidayFile("test/holidayFile.yaml");

        int actualResult = holidayConfigurationService.calculateAmountOfWorkingDays(LocalDate.parse("20210706", DateTimeFormatter.BASIC_ISO_DATE),
                LocalDate.parse("20210709", DateTimeFormatter.BASIC_ISO_DATE));

        assertEquals(actualResult, 3);
    }

    @Test
    public void testCalculateAmountOfWorkingDaysWithoutHolidaysWithWeekendsIncluded()
    {
        setHolidayFile("test/holidayFile.yaml");

        int actualResult = holidayConfigurationService.calculateAmountOfWorkingDays(LocalDate.parse("20210707", DateTimeFormatter.BASIC_ISO_DATE),
                LocalDate.parse("20210713", DateTimeFormatter.BASIC_ISO_DATE));

        assertEquals(actualResult, 4);
    }

    @Test
    public void testCalculateAmountOfWorkingDaysWithHolidaysAndWeekendsIncluded()
    {
        setHolidayFile("test/holidayFile.yaml");

        int actualResult = holidayConfigurationService.calculateAmountOfWorkingDays(LocalDate.parse("20210701", DateTimeFormatter.BASIC_ISO_DATE),
                LocalDate.parse("20210709", DateTimeFormatter.BASIC_ISO_DATE));

        assertEquals(actualResult, 5);
    }

}
