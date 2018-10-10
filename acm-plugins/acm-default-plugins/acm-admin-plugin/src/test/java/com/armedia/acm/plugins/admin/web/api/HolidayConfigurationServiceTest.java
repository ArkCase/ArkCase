package com.armedia.acm.plugins.admin.web.api;

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


        LocalDate actualResult = holidayConfigurationService.addWorkingDaysToDate(LocalDate.parse("20181119", DateTimeFormatter.BASIC_ISO_DATE),
                6);

        LocalDate expectedResult = LocalDate.parse("20181128", DateTimeFormatter.BASIC_ISO_DATE);
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

        LocalDate expectedResult = LocalDate.parse("20181230", DateTimeFormatter.BASIC_ISO_DATE);
        assertEquals(actualResult, expectedResult);

    }

}
