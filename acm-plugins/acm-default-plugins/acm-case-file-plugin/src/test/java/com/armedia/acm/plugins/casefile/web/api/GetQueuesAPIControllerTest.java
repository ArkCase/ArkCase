package com.armedia.acm.plugins.casefile.web.api;

/*-
 * #%L
 * ACM Default Plugin: Case File
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

import static org.easymock.EasyMock.expect;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.armedia.acm.plugins.casefile.model.AcmQueue;
import com.armedia.acm.plugins.casefile.service.AcmQueueService;

import org.easymock.EasyMockSupport;
import org.easymock.IAnswer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nebojsha on 31.08.2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-web-acm-web.xml",
        "classpath:/spring/spring-library-case-plugin-unit-test.xml",
        "classpath:/spring/spring-web-queue-api-test.xml"
})
public class GetQueuesAPIControllerTest extends EasyMockSupport
{
    @Autowired
    private GetQueuesAPIController getQueuesAPIController;

    private AcmQueueService acmQueueService;

    private MockMvc mockMvc;
    private MockHttpSession mockHttpSession;
    private Authentication mockAuthentication;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    private Logger log = LogManager.getLogger(getClass());

    @Before
    public void setUp()
    {
        acmQueueService = createMock(AcmQueueService.class);

        mockHttpSession = new MockHttpSession();
        mockAuthentication = createMock(Authentication.class);
        getQueuesAPIController.setAcmQueueService(acmQueueService);
        mockMvc = MockMvcBuilders.standaloneSetup(getQueuesAPIController).setHandlerExceptionResolvers(exceptionResolver).build();
    }

    @Test
    public void testFindCaseById() throws Exception
    {
        expect(mockAuthentication.getName()).andReturn("user").atLeastOnce();

        expect(acmQueueService.listAllQueues()).andAnswer(new IAnswer<List<AcmQueue>>()
        {
            @Override
            public List<AcmQueue> answer() throws Throwable
            {
                List<AcmQueue> queues = new ArrayList<>();
                queues.add(new AcmQueue(1l, "queue1", 0));
                queues.add(new AcmQueue(2l, "queue2", 1));
                queues.add(new AcmQueue(3l, "queue3", 2));
                return queues;
            }
        });

        replayAll();

        MvcResult result = mockMvc.perform(
                get("/api/latest/plugin/queues")
                        .principal(mockAuthentication)
                        .session(mockHttpSession))
                .andExpect(status().isOk())
                .andReturn();
        verifyAll();
    }
}
