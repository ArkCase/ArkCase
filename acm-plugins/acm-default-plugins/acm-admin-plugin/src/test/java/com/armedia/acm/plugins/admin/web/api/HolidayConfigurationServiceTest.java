package com.armedia.acm.plugins.admin.web.api;

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

import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.objectonverter.json.JSONUnmarshaller;
import com.armedia.acm.plugins.admin.service.HolidayConfigurationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.FileSystemResource;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.assertEquals;

/**
 * @author ana.serafimoska
 */
public class HolidayConfigurationServiceTest extends EasyMockSupport {

    HolidayConfigurationService holidayConfigurationService = new HolidayConfigurationService();


    @Before
    public void setUp(){

        ObjectConverter objectConverter = new ObjectConverter();
        JSONUnmarshaller jsonUnmarshaller = new JSONUnmarshaller();

        jsonUnmarshaller.setMapper(new ObjectMapper());
        objectConverter.setJsonUnmarshaller(jsonUnmarshaller);

        holidayConfigurationService.setObjectConverter(objectConverter);
    }

    @Test
    public void testAddWorkingDaysToDateWithoutWeekendsAndHolidaysIncluded(){

        String holidayConfigurationFilePath = getClass().getClassLoader().getResource("test/holidayFile.json").getPath();
        holidayConfigurationService.setHolidayFile(new FileSystemResource(holidayConfigurationFilePath));


        LocalDate actualResult = holidayConfigurationService.addWorkingDaysToDate(LocalDate.parse("20181010", DateTimeFormatter.BASIC_ISO_DATE),
                6);

        LocalDate expectedResult = LocalDate.parse("20181018", DateTimeFormatter.BASIC_ISO_DATE);
        assertEquals(actualResult, expectedResult);

    }

    @Test
    public void testAddWorkingDaysToDateWithoutWeekendsIncluded(){

        //included "Thanksgiving Day": "2018-11-22"
        String holidayConfigurationFilePath = getClass().getClassLoader().getResource("test/holidayFile.json").getPath();
        holidayConfigurationService.setHolidayFile(new FileSystemResource(holidayConfigurationFilePath));


        LocalDate actualResult = holidayConfigurationService.addWorkingDaysToDate(LocalDate.parse("20181122", DateTimeFormatter.BASIC_ISO_DATE),
                6);

        LocalDate expectedResult = LocalDate.parse("20181130", DateTimeFormatter.BASIC_ISO_DATE);
        assertEquals(actualResult, expectedResult);

    }

    @Test
    public void testAddWorkingDaysToDateWithWeekendsIncluded(){

        String holidayConfigurationFilePath = getClass().getClassLoader().getResource("test/holidayFileIncludeWeekends.json").getPath();
        holidayConfigurationService.setHolidayFile(new FileSystemResource(holidayConfigurationFilePath));


        LocalDate actualResult = holidayConfigurationService.addWorkingDaysToDate(LocalDate.parse("20181009", DateTimeFormatter.BASIC_ISO_DATE),
                6);

        LocalDate expectedResult = LocalDate.parse("20181015", DateTimeFormatter.BASIC_ISO_DATE);
        assertEquals(actualResult, expectedResult);

    }

    @Test
    public void testAddWorkingDaysToDateWithWeekendsAndHolidayIncluded(){

        //included "Christmas Day": "2018-12-25"
        String holidayConfigurationFilePath = getClass().getClassLoader().getResource("test/holidayFileIncludeWeekends.json").getPath();
        holidayConfigurationService.setHolidayFile(new FileSystemResource(holidayConfigurationFilePath));


        LocalDate actualResult = holidayConfigurationService.addWorkingDaysToDate(LocalDate.parse("20181224", DateTimeFormatter.BASIC_ISO_DATE),
                6);

        LocalDate expectedResult = LocalDate.parse("20181231", DateTimeFormatter.BASIC_ISO_DATE);
        assertEquals(actualResult, expectedResult);

    }

}
