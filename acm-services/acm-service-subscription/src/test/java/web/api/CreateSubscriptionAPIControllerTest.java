package web.api;

import com.armedia.acm.pluginmanager.model.AcmPlugin;
import com.armedia.acm.services.subscription.dao.SubscriptionDao;
import com.armedia.acm.services.subscription.model.AcmSubscription;
import com.armedia.acm.services.subscription.service.SubscriptionEventPublisher;
import com.armedia.acm.services.subscription.web.api.CreateSubscriptionAPIController;
import com.armedia.acm.services.subscription.web.api.RemovingSubscriptionAPIController;
import org.codehaus.jackson.map.ObjectMapper;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Ignore;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class CreateSubscriptionAPIControllerTest extends EasyMockSupport {

    private MockMvc mockMvc;
    private Authentication mockAuthentication;
    private MockHttpSession mockHttpSession;

    private CreateSubscriptionAPIController mockCreateSubscriptionAPIController;
    private SubscriptionDao mockSubscriptionDao;
    private AcmPlugin mockSubscriptionPlugin;
    private SubscriptionEventPublisher mockSubscriptionEventPublisher;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Before
    public void setUp() throws Exception {
        mockCreateSubscriptionAPIController = new CreateSubscriptionAPIController();

        mockSubscriptionDao = createMock(SubscriptionDao.class);
        mockSubscriptionPlugin = createMock(AcmPlugin.class);
        mockSubscriptionEventPublisher = createMock(SubscriptionEventPublisher.class);
        mockAuthentication = createMock(Authentication.class);
        mockHttpSession = new MockHttpSession();

        mockCreateSubscriptionAPIController.setSubscriptionDao(mockSubscriptionDao);
        mockCreateSubscriptionAPIController.setSubscriptionPlugin(mockSubscriptionPlugin);
        mockCreateSubscriptionAPIController.setSubscriptionEventPublisher(mockSubscriptionEventPublisher);

        mockMvc = MockMvcBuilders.standaloneSetup(mockCreateSubscriptionAPIController).setHandlerExceptionResolvers(exceptionResolver).build();
    }


    @Ignore
    @Test
   public void createSubscription() throws Exception {

       String userId="user-acm";
       Long objectId=100L;
       String objectType="NEW_OBJ_TYPE";
       String ipAddress = "ipAddress";

       AcmSubscription subscription= new AcmSubscription();
       subscription.setSubscriptionObjectType(objectType);
       subscription.setUserId(userId);
       subscription.setObjectId(objectId);

       Map<String,Object> prop =  new HashMap<>();
       prop.put("key","value");

       mockHttpSession.setAttribute("acm_ip_address", ipAddress);


       expect(mockSubscriptionDao.save(subscription)).andReturn(subscription).anyTimes();
       expect(mockSubscriptionPlugin.getPluginProperties()).andReturn(prop).anyTimes();

       mockSubscriptionEventPublisher.publishSubscriptionCreatedEvent(subscription, mockAuthentication, true);


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
       AcmSubscription foundSubscription = objectMapper.readValue(json, AcmSubscription.class);

       assertNotNull(foundSubscription);

       assertEquals(subscription.getObjectId(), foundSubscription.getObjectId());
       assertEquals(subscription.getObjectType(), foundSubscription.getObjectType());
       assertEquals(subscription.getUserId(), foundSubscription.getUserId());

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
