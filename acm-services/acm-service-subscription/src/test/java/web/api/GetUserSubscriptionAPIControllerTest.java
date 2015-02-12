package web.api;

import com.armedia.acm.services.subscription.dao.SubscriptionDao;
import com.armedia.acm.services.subscription.model.AcmSubscription;
import com.armedia.acm.services.subscription.web.api.GetUserSubscriptionAPIController;
import org.codehaus.jackson.map.ObjectMapper;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Created by marjan.stefanoski on 12.02.2015.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-web-acm-web.xml",
        "classpath:/spring/spring-library-subscription-web-api-test.xml"
})
public class GetUserSubscriptionAPIControllerTest extends EasyMockSupport {
    private MockMvc mockMvc;
    private Authentication mockAuthentication;
    private MockHttpSession mockHttpSession;

    private GetUserSubscriptionAPIController mockGetUserSubscriptionAPIController;
    private SubscriptionDao mockSubscriptionDao;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Before
    public void setUp() throws Exception {

        mockSubscriptionDao = createMock(SubscriptionDao.class);
        mockGetUserSubscriptionAPIController = new GetUserSubscriptionAPIController();
        mockHttpSession = new MockHttpSession();
        mockGetUserSubscriptionAPIController.setSubscriptionDao(mockSubscriptionDao);

        mockMvc = MockMvcBuilders.standaloneSetup(mockGetUserSubscriptionAPIController).setHandlerExceptionResolvers(exceptionResolver).build();
        mockAuthentication = createMock(Authentication.class);
    }


    @Test
    public void getUserSubscription() throws Exception {

        String userId="user-acm";
        Long objectId=100L;
        String objectType="NEW_OBJ_TYPE";

        AcmSubscription subscription= new AcmSubscription();
        subscription.setSubscriptionObjectType(objectType);
        subscription.setUserId(userId);
        subscription.setObjectId(objectId);


        expect(mockSubscriptionDao.getSubscriptionByUserObjectIdAndType(userId, objectId, objectType)).andReturn(Arrays.asList(subscription)).anyTimes();

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn(userId).atLeastOnce();

        replayAll();

        MvcResult result = mockMvc.perform(
                get("/api/latest/service/subscription/{userId}/{objType}/{objId}",userId,objectType,objectId)
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .session(mockHttpSession)
                        .principal(mockAuthentication))
                        .andReturn();

        verifyAll();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE));

        String json = result.getResponse().getContentAsString();

        log.info("results: " + json);

        ObjectMapper objectMapper = new ObjectMapper();
        List<AcmSubscription> foundSubscription = objectMapper.readValue(json,
                objectMapper.getTypeFactory().constructParametricType(List.class, AcmSubscription.class));

        assertNotNull(foundSubscription);

        assertEquals(1,foundSubscription.size());

        assertEquals(subscription.getObjectId(), foundSubscription.get(0).getObjectId());
        assertEquals(subscription.getObjectType(), foundSubscription.get(0).getObjectType());
        assertEquals(subscription.getUserId(), foundSubscription.get(0).getUserId());

    }

    public MockMvc getMockMvc() {
        return mockMvc;
    }

    public void setMockMvc(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    public Authentication getMockAuthentication() {
        return mockAuthentication;
    }

    public void setMockAuthentication(Authentication mockAuthentication) {
        this.mockAuthentication = mockAuthentication;
    }

    public ExceptionHandlerExceptionResolver getExceptionResolver() {
        return exceptionResolver;
    }

    public void setExceptionResolver(ExceptionHandlerExceptionResolver exceptionResolver) {
        this.exceptionResolver = exceptionResolver;
    }

}
