package web.api;

import com.armedia.acm.pluginmanager.model.AcmPlugin;
import com.armedia.acm.services.subscription.dao.SubscriptionDao;
import com.armedia.acm.services.subscription.model.AcmSubscription;
import com.armedia.acm.services.subscription.web.api.ListSubscriptionByUserAPIController;
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

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
public class ListSubscriptionByUserAPIControllerTest extends EasyMockSupport {

    private MockMvc mockMvc;
    private Authentication mockAuthentication;
    private MockHttpSession mockHttpSession;

    private ListSubscriptionByUserAPIController mockListSubscriptionByUserAPIController;
    private SubscriptionDao mockSubscriptionDao;
    private AcmPlugin mockSubscriptionPlugin;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Before
    public void setUp() throws Exception {

        mockListSubscriptionByUserAPIController = new ListSubscriptionByUserAPIController();
        mockSubscriptionDao = createMock(SubscriptionDao.class);
        mockSubscriptionPlugin = createMock(AcmPlugin.class);

        mockHttpSession = new MockHttpSession();

        mockListSubscriptionByUserAPIController.setSubscriptionDao(mockSubscriptionDao);
        mockListSubscriptionByUserAPIController.setSubscriptionPlugin(mockSubscriptionPlugin);

        mockMvc = MockMvcBuilders.standaloneSetup(mockListSubscriptionByUserAPIController).setHandlerExceptionResolvers(exceptionResolver).build();
        mockAuthentication = createMock(Authentication.class);
    }

    @Test
    public void getAllSubscriptionsByUserId() throws Exception{

        String userId="user-acm";
        Long objectId=100L;
        String objectType="NEW_OBJ_TYPE";

        int startRow = 0;
        int maxRow = 10;

        AcmSubscription subscription= new AcmSubscription();
        subscription.setSubscriptionObjectType(objectType);
        subscription.setUserId(userId);
        subscription.setObjectId(objectId);

        AcmSubscription subscription1 = new AcmSubscription();
        subscription1.setObjectId(200L);
        subscription1.setSubscriptionObjectType("OLD_OBJ_TYPE");
        subscription1.setUserId(userId);

        List<AcmSubscription> subscriptionList = new ArrayList<>();
        subscriptionList.add(subscription);
        subscriptionList.add(subscription1);

        expect(mockSubscriptionDao.getListOfSubscriptionsByUser(userId, startRow, maxRow)).andReturn(subscriptionList).atLeastOnce();

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn(userId).atLeastOnce();

        replayAll();

        MvcResult result = mockMvc.perform(
                get("/api/latest/service/subscription/{userId}",userId)
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

        assertEquals(2,foundSubscription.size());

        assertEquals(subscription.getObjectId(), foundSubscription.get(0).getObjectId());
        assertEquals(subscription1.getObjectType(), foundSubscription.get(1).getObjectType());
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
