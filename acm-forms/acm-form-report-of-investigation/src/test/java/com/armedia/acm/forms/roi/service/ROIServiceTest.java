/**
 * 
 */
package com.armedia.acm.forms.roi.service;

/*-
 * #%L
 * ACM Forms: Report of Investigation
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

import com.armedia.acm.forms.roi.model.ROIForm;
import com.armedia.acm.objectonverter.DateFormats;
import com.armedia.acm.services.users.dao.UserDao;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.io.IOUtils;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

/**
 * @author riste.tutureski
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-roi-service-test.xml"
})
public class ROIServiceTest extends EasyMockSupport
{

    @Autowired
    private ROIService roiServiceTest;

    private UserDao mockUserDao;

    private Gson gson;

    @Before
    public void setUp() throws Exception
    {
        gson = new GsonBuilder().setDateFormat(DateFormats.FREVVO_DATE_FORMAT).create();

        mockUserDao = createMock(UserDao.class);
        roiServiceTest.setUserDao(mockUserDao);
    }

    @Test
    public void testInitFormData() throws Exception
    {
        String expected = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("txt/InitFormData.txt"));

        ROIForm form = gson.fromJson(expected, ROIForm.class);
        form.getReportInformation().setDate(new Date());

        replayAll();

        Object initFormData = roiServiceTest.get("init-form-data");

        verifyAll();

        assertEquals(gson.toJson(form), initFormData.toString());
    }

}
