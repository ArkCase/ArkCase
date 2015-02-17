package web.api;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.pluginmanager.model.AcmPlugin;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.services.subscription.dao.SubscriptionDao;
import com.armedia.acm.services.subscription.model.AcmSubscription;
import com.armedia.acm.services.subscription.service.SubscriptionEventPublisher;
import com.armedia.acm.services.subscription.web.api.CreateSubscriptionAPIController;
import com.armedia.acm.services.subscription.web.api.RemovingSubscriptionAPIController;
import org.codehaus.jackson.map.ObjectMapper;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.json.JSONObject;
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

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

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
    private ExecuteSolrQuery mockExecuteSolrQuery;

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
        mockExecuteSolrQuery = createMock(ExecuteSolrQuery.class);
        mockHttpSession = new MockHttpSession();

        mockCreateSubscriptionAPIController.setSubscriptionDao(mockSubscriptionDao);
        mockCreateSubscriptionAPIController.setSubscriptionPlugin(mockSubscriptionPlugin);
        mockCreateSubscriptionAPIController.setSubscriptionEventPublisher(mockSubscriptionEventPublisher);
        mockCreateSubscriptionAPIController.setExecuteSolrQuery(mockExecuteSolrQuery);

        mockMvc = MockMvcBuilders.standaloneSetup(mockCreateSubscriptionAPIController).setHandlerExceptionResolvers(exceptionResolver).build();
    }

    @Test
   public void createSubscription() throws Exception {

       String userId="user-acm";
       Long objectId=100L;
       String objectType="NEW_OBJ_TYPE";
       String ipAddress = "ipAddress";
       String solrQuery = "q=id:100-NEW_OBJ_TYPE&fq=-status_s:COMPLETE&fq=-status_s:DELETE&fq=-status_s:CLOSED";

       AcmSubscription subscription= new AcmSubscription();
        subscription.setSubscriptionId(500L);
        subscription.setSubscriptionObjectType(objectType);
        subscription.setUserId(userId);
        subscription.setObjectId(objectId);

       Map<String,Object> prop =  new HashMap<>();
       prop.put("subscription.get.object.byId","q=id:?&fq=-status_s:COMPLETE&fq=-status_s:DELETE&fq=-status_s:CLOSED");

       mockHttpSession.setAttribute("acm_ip_address", ipAddress);

        String jsonString="{response:{docs:[{name:a,title_parseable:aa},{name:d,title_parseable:bb}]}}";


        Capture<AcmSubscription> subscriptionToSave = new Capture<>();

        expect(mockSubscriptionPlugin.getPluginProperties()).andReturn(prop).once();
        expect(mockExecuteSolrQuery.getResultsByPredefinedQuery(solrQuery,"0","1","",mockAuthentication)).andReturn(jsonString).once();
        expect(mockSubscriptionDao.save(capture(subscriptionToSave))).andReturn(subscription).anyTimes();

       mockSubscriptionEventPublisher.publishSubscriptionCreatedEvent(subscription, mockAuthentication, true);


       // MVC test classes must call getName() somehow
       expect(mockAuthentication.getName()).andReturn(userId).atLeastOnce();

       replayAll();

       MvcResult result = mockMvc.perform(
               put("/api/latest/service/subscription/{userId}/{objType}/{objId}", userId, objectType, objectId)
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
