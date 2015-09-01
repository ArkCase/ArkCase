package com.armedia.acm.plugins.casefile.web.api;

import com.armedia.acm.plugins.casefile.model.AcmQueue;
import com.armedia.acm.plugins.casefile.service.AcmQueueService;
import org.easymock.EasyMockSupport;
import org.easymock.IAnswer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import static org.easymock.EasyMock.expect;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by nebojsha on 31.08.2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-web-acm-web.xml",
        "classpath:/spring/spring-library-case-plugin-test.xml",
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

    private Logger log = LoggerFactory.getLogger(getClass());

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
            public List<AcmQueue> answer() throws Throwable
            {
                List<AcmQueue> queues = new ArrayList<AcmQueue>();
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