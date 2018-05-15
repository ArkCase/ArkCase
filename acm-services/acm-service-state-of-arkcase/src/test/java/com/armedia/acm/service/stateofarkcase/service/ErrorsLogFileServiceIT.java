package com.armedia.acm.service.stateofarkcase.service;

/*-
 * #%L
 * ACM Service: State of Arkcase
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
