package com.armedia.acm.services.holiday.service;

/*-
 * #%L
 * ACM Default Plugin: admin
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

import com.armedia.acm.objectonverter.json.JSONUnmarshaller;
import com.armedia.acm.services.holiday.model.HolidayConfigurationHolder;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.io.FileUtils;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author ana.serafimoska
 */
public class HolidayConfigurationServiceTest extends EasyMockSupport {

    HolidayConfigurationService holidayConfigurationService = new HolidayConfigurationService();


    @Before
    public void setUp(){

        JSONUnmarshaller jsonUnmarshaller = new JSONUnmarshaller();
        jsonUnmarshaller.setMapper(new ObjectMapper());

        HolidayConfigurationHolder holidayConfigurationHolder = new HolidayConfigurationHolder();
        holidayConfigurationHolder.setJsonUnmarshaller(jsonUnmarshaller);

        holidayConfigurationService.setHolidayConfigurationHolder(holidayConfigurationHolder);
    }

    private void setHolidayFile(String relativePath) throws IOException
    {
        String holidayConfigurationFilePath = getClass().getClassLoader().getResource(relativePath).getPath();
        String holidayJsonConfiguration = FileUtils.readFileToString(new File(holidayConfigurationFilePath));
        holidayConfigurationService.getHolidayConfigurationHolder().setHolidayJsonConfiguration(holidayJsonConfiguration);
        holidayConfigurationService.getHolidayConfigurationHolder().afterPropertiesSet();
    }

    @Test
    public void testAddWorkingDaysToDateWithoutWeekendsAndHolidaysIncluded() throws IOException {

        setHolidayFile("test/holidayFile.json");

        LocalDate actualResult = holidayConfigurationService.addWorkingDaysToDate(LocalDate.parse("20181010", DateTimeFormatter.BASIC_ISO_DATE),
                6);

        LocalDate expectedResult = LocalDate.parse("20181018", DateTimeFormatter.BASIC_ISO_DATE);
        assertEquals(actualResult, expectedResult);

    }

    @Test
    public void testAddWorkingDaysToDateWithoutWeekendsIncluded() throws IOException {

        //included "Thanksgiving Day": "2018-11-22"
        setHolidayFile("test/holidayFile.json");

        LocalDate actualResult = holidayConfigurationService.addWorkingDaysToDate(LocalDate.parse("20181122", DateTimeFormatter.BASIC_ISO_DATE),
                6);

        LocalDate expectedResult = LocalDate.parse("20181130", DateTimeFormatter.BASIC_ISO_DATE);
        assertEquals(actualResult, expectedResult);

    }

    @Test
    public void testAddWorkingDaysToDateWithWeekendsIncluded() throws IOException
    {

        setHolidayFile("test/holidayFileIncludeWeekends.json");

        LocalDate actualResult = holidayConfigurationService.addWorkingDaysToDate(LocalDate.parse("20181009", DateTimeFormatter.BASIC_ISO_DATE),
                6);

        LocalDate expectedResult = LocalDate.parse("20181015", DateTimeFormatter.BASIC_ISO_DATE);
        assertEquals(actualResult, expectedResult);

    }

    @Test
    public void testAddWorkingDaysToDateWithWeekendsAndHolidayIncluded() throws IOException
    {

        //included "Christmas Day": "2018-12-25"
        setHolidayFile("test/holidayFileIncludeWeekends.json");

        LocalDate actualResult = holidayConfigurationService.addWorkingDaysToDate(LocalDate.parse("20181224", DateTimeFormatter.BASIC_ISO_DATE),
                6);

        LocalDate expectedResult = LocalDate.parse("20181231", DateTimeFormatter.BASIC_ISO_DATE);
        assertEquals(actualResult, expectedResult);

    }

    @Test
    public void testFindNextWorkingDayWhenCurrentDayIsHoliday() throws IOException
    {
        setHolidayFile("test/holidayFile.json");

        // memorials day 2018-05-28
        LocalDate testDate = LocalDate.parse("20180528", DateTimeFormatter.BASIC_ISO_DATE);
        LocalDate actualResult = holidayConfigurationService.getFirstWorkingDay(testDate);

        LocalDate expectedResult = LocalDate.parse("20180529", DateTimeFormatter.BASIC_ISO_DATE);
        assertEquals(actualResult, expectedResult);
    }

    @Test
    public void testFindNextWorkingDayWhenCurrentDayIsNonWorkingWeekend() throws IOException
    {
        setHolidayFile("test/holidayFile.json");

        LocalDate currentDate = LocalDate.parse("20181222", DateTimeFormatter.BASIC_ISO_DATE);
        LocalDate actualResult = holidayConfigurationService.getFirstWorkingDay(currentDate);

        LocalDate expectedResult = LocalDate.parse("20181224", DateTimeFormatter.BASIC_ISO_DATE);

        assertEquals(actualResult, expectedResult);
    }

    @Test
    public void testFindNextWorkingDayWhenCurrentDayIsWorkingWeekend() throws IOException
    {
        setHolidayFile("test/holidayFileIncludeWeekends.json");

        LocalDate currentDate = LocalDate.parse("20181222", DateTimeFormatter.BASIC_ISO_DATE);
        LocalDate actualResult = holidayConfigurationService.getFirstWorkingDay(currentDate);

        LocalDate expectedResult = LocalDate.parse("20181222", DateTimeFormatter.BASIC_ISO_DATE);

        assertEquals(actualResult, expectedResult);
    }

    @Test
    public void testCalculateDueDateNoWorkingWeekend() throws IOException
    {
        setHolidayFile("test/holidayFile.json");

        LocalDate currentDate = LocalDate.parse("20200425", DateTimeFormatter.BASIC_ISO_DATE);
        LocalDate actualResult = holidayConfigurationService.getFirstWorkingDay(currentDate);

        LocalDate dueDate = holidayConfigurationService.addWorkingDaysToDate(actualResult, 20);

        LocalDate expectedResult = LocalDate.parse("20200427", DateTimeFormatter.BASIC_ISO_DATE);
        LocalDate expectedDueDate = LocalDate.parse("20200525", DateTimeFormatter.BASIC_ISO_DATE);

        assertEquals(actualResult, expectedResult);
        assertEquals(dueDate, expectedDueDate);
    }

    @Test
    public void calculateDateSubtractBy() throws IOException
    {
        setHolidayFile("test/holidayFile.json");

        LocalDate currentDate = LocalDate.parse("20200525", DateTimeFormatter.BASIC_ISO_DATE);

        LocalDate result = holidayConfigurationService.subtractWorkingDaysFromDate(currentDate, 20);

        LocalDate expectedDueDate = LocalDate.parse("20200427", DateTimeFormatter.BASIC_ISO_DATE);

        assertEquals(expectedDueDate, result);
    }

}
