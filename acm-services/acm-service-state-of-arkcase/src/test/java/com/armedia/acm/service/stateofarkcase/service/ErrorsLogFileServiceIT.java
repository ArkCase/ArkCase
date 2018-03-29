package com.armedia.acm.service.stateofarkcase.service;

import static org.junit.Assert.assertNotNull;

import com.armedia.acm.service.stateofarkcase.exceptions.ErrorLogFileException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-library-object-converter.xml",
        "classpath:/spring/spring-library-state-of-arkcase-test.xml"
})
public class ErrorsLogFileServiceIT
{
    @Autowired
    ErrorsLogFileService errorsLogFileService;

    @Test
    public void testGetCurrentErrorsFile() throws ErrorLogFileException
    {
        assertNotNull(errorsLogFileService);
        assertNotNull(errorsLogFileService.getCurrentFile());
    }

    @Test
    public void testGetExistingInPastErrorsFile() throws ErrorLogFileException, IOException
    {
        assertNotNull(errorsLogFileService);
        int pastDays = 0;
        File file = new File(errorsLogFileService.getFileNameInPastDays(pastDays));
        file.deleteOnExit();

        // test file must be created, since logger needs to have messages from previous day.
        FileWriter writer = new FileWriter(file);
        writer.append("test");
        writer.close();
        System.out.println(errorsLogFileService.getFileNameInPastDays(pastDays));
        assertNotNull(errorsLogFileService.getFileInPastDays(pastDays));
    }
}